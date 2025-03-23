# Determine which docker compose command to use
DOCKER_COMPOSE := $(shell which docker-compose 2>/dev/null || echo "docker compose")

SERVICE_NAME := poi-api  # Ensure this matches the service name in docker-compose.yml

.PHONY: generate-token list-tokens revoke-token docker-exec help build up down logs

# Default target
help:
	@echo "Token Management Makefile"
	@echo "--------------------------"
	@echo "make generate-token - Generate a new API token"
	@echo "make list-tokens    - List all existing API tokens"
	@echo "make revoke-token   - Revoke an existing token by ID"
	@echo "make build          - Build the Docker image"
	@echo "make up             - Start the Docker container"
	@echo "make down           - Stop the Docker container"
	@echo "make logs           - View container logs"
	@echo "--------------------------"
	@echo "Using docker compose command: $(DOCKER_COMPOSE)"

# Docker related commands
build:
	$(DOCKER_COMPOSE) build

up:
	$(DOCKER_COMPOSE) up

upd:
	$(DOCKER_COMPOSE) up -d

down:
	$(DOCKER_COMPOSE) down

logs:
	$(DOCKER_COMPOSE) logs -f

# The docker-exec helper function
docker-exec:
	@container_id=$$($(DOCKER_COMPOSE) ps -q $(SERVICE_NAME)); \
	if [ -z "$$container_id" ]; then \
	echo "Error: Container is not running. Start it with 'make up'"; \
	exit 1; \
	fi; \
	docker exec -it $$container_id sh -c "$(CMD)"

# Token management commands
generate-token:
	@read -p "Enter token description: " description; \
	read -p "Enter expiration days (0 for no expiration): " days; \
	$(MAKE) docker-exec CMD="java -jar /app/app.jar --spring.main.web-application-type=none --generate-token='$$description' --expiration-days=$$days"

list-tokens:
	$(MAKE) docker-exec CMD="java -jar /app/app.jar --spring.main.web-application-type=none --list-tokens"

revoke-token:
	@read -p "Enter token ID to revoke: " id; \
	$(MAKE) docker-exec CMD="java -jar /app/app.jar --spring.main.web-application-type=none --revoke-token=$$id"