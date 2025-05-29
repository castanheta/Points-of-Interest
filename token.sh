#!/usr/bin/env bash
set -euo pipefail

# ─── CONFIG ────────────────────────────────────────────────────────────────
NAMESPACE=wayly
SECRET_NAME=poiuser.poi-db-cluster.credentials.postgresql.acid.zalan.do
LABEL_SELECTOR="cluster-name=poi-db-cluster,spilo-role=master"
POSTGRES_DB=poidb
# ────────────────────────────────────────────────────────────────────────────

# Fetch and decode Postgres credentials into environment variables
fetch_secrets() {
  echo "→ Loading secrets from Secret/${SECRET_NAME} in namespace ${NAMESPACE}"
  for key in username password; do
    val=$(kubectl get secret "${SECRET_NAME}" -n "${NAMESPACE}" \
      -o go-template="{{ index .data \"${key}\" }}" | base64 --decode)
    export ${key}="${val}"
  done
  export POSTGRES_USER="$username"
  export PGPASSWORD="$password"
}

# Pick the first matching Postgres pod
get_postgres_pod() {
  echo -n "→ Locating Postgres pod with label '${LABEL_SELECTOR}'… "
  POD=$(kubectl get pods -n "${NAMESPACE}" -l "${LABEL_SELECTOR}" -o name \
        | head -n1 | cut -d/ -f2)
  if [[ -z "$POD" ]]; then
    echo "failed"
    echo "❌ No pod found matching label selector '${LABEL_SELECTOR}'" >&2
    exit 1
  fi
  echo "found '${POD}'"
}

# Wait until the api_tokens table exists in the database
wait_for_api_tokens_table() {
  echo -n "→ Waiting for api_tokens table in pod '${POD}'… "
  until kubectl exec -n "${NAMESPACE}" "${POD}" -- \
      psql --username="$POSTGRES_USER" --dbname="$POSTGRES_DB" \
          -c '\d api_tokens' &>/dev/null; do
    echo -n "."
    sleep 2
  done
  echo " OK"
}

# Generate a base64 API token and insert it into the table
generate_new_token() {
  echo "→ Generating new API token…"
  token=$(openssl rand -base64 32 | tr '+/' '-_' | tr -d '=')

  cat <<EOF | kubectl exec -i -n "${NAMESPACE}" "${POD}" -- \
      psql --username="$POSTGRES_USER" --dbname="$POSTGRES_DB"
INSERT INTO api_tokens (token, description, expires_at, active)
VALUES ('$token', 'Auto-generated token', NULL, TRUE)
ON CONFLICT (token) DO NOTHING;
EOF

  echo "✅ Token inserted."
  echo "New API token: $token"
}

# List all tokens in the table
list_all_tokens() {
  echo "→ Listing all API tokens…"
  kubectl exec -n "${NAMESPACE}" "${POD}" -- \
    psql --username="$POSTGRES_USER" --dbname="$POSTGRES_DB" -c \
    "SELECT token, description, expires_at, active FROM api_tokens ORDER BY id;"
}

# Revoke (deactivate) a token by value
revoke_token() {
  read -rp "Enter the token to revoke: " token_to_revoke
  echo "→ Revoking token…"
  cat <<EOF | kubectl exec -i -n "${NAMESPACE}" "${POD}" -- \
      psql --username="$POSTGRES_USER" --dbname="$POSTGRES_DB"
UPDATE api_tokens SET active = FALSE WHERE token = '$token_to_revoke';
EOF
  echo "✅ Token revoked (if it existed)."
}

# Display menu and handle user input
show_menu() {
  echo ""
  echo "API Token Management"
  echo "----------------------"
  echo "1. Generate new token"
  echo "2. List all tokens"
  echo "3. Revoke a token"
  echo "4. Exit"
  echo "----------------------"
  echo ""
  read -rp "Select option [1-4]: " option
  case "$option" in
    1) generate_new_token ;;
    2) list_all_tokens   ;;
    3) revoke_token      ;;
    4) echo "Goodbye!"; exit 0 ;;
    *) echo "Invalid option";;
  esac
}

main() {
  fetch_secrets
  get_postgres_pod
  wait_for_api_tokens_table

  # Loop the menu until exit
  while true; do
    show_menu
  done
}

main "$@"
