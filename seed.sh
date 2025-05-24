#!/usr/bin/env bash
set -euo pipefail

# ─── CONFIG ────────────────────────────────────────────────────────────────
NAMESPACE=wayly
SECRET_NAME=poi-secrets
LABEL_SELECTOR="app=postgres"
SQL_FILE="./data.sql"
# ────────────────────────────────────────────────────────────────────────────

fetch_secrets() {
  echo "→ Fetching secrets from Secret/${SECRET_NAME} in namespace ${NAMESPACE}"
  local key
  for key in POSTGRES_USER POSTGRES_PASSWORD POSTGRES_DB; do
    local val
    val=$(kubectl get secret "${SECRET_NAME}" -n "${NAMESPACE}" \
      -o jsonpath="{.data.${key}}" | base64 --decode)
    export "${key}=${val}"
  done
  export PGPASSWORD="$POSTGRES_PASSWORD"
}

get_postgres_pod() {
  echo -n "→ Finding Postgres pod with label '${LABEL_SELECTOR}'… "
  POD=$(kubectl get pods -n "${NAMESPACE}" -l "${LABEL_SELECTOR}" -o name \
        | head -n1 | cut -d/ -f2)
  if [[ -z "$POD" ]]; then
    echo "failed"
    echo "❌ No pod found matching label selector '${LABEL_SELECTOR}'"
    exit 1
  fi
  echo "found '${POD}'"
}

wait_for_schema() {
  echo -n "→ Waiting for 'points_of_interest' table in pod '${POD}'… "
  until kubectl exec -n "${NAMESPACE}" "${POD}" -- \
      psql --username="$POSTGRES_USER" --dbname="$POSTGRES_DB" \
           -c '\d points_of_interest' &>/dev/null; do
    echo -n "."
    sleep 2
  done
  echo " OK"
}

load_sql_data() {
  echo "→ Loading SQL file: ${SQL_FILE}"
  if [[ ! -f "$SQL_FILE" ]]; then
    echo "❌ File not found: ${SQL_FILE}"
    exit 1
  fi

  cat "$SQL_FILE" | kubectl exec -i -n "${NAMESPACE}" "${POD}" -- \
    psql --username="$POSTGRES_USER" --dbname="$POSTGRES_DB"
  echo "✅ SQL file executed successfully."
}

main() {
  fetch_secrets
  get_postgres_pod
  echo "Using pod:     $POD"
  echo "User:          $POSTGRES_USER"
  echo "Database:      $POSTGRES_DB"
  wait_for_schema
  load_sql_data
}

main "$@"
