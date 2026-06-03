.PHONY: all test test-fast playwright playwright-fast playwright-gradle playwright-gradle-fast cypress cypress-fast cypress-gradle cypress-gradle-fast selenium selenium-fast karate karate-fast java clean perf-test k6-test start-server stop-server

PLAYWRIGHT_DIR := playwright-tests
PLAYWRIGHT_BIN := $(PLAYWRIGHT_DIR)/node_modules/.bin/playwright

CYPRESS_DIR := cypress-tests
CYPRESS_BIN := $(CYPRESS_DIR)/node_modules/.bin/cypress

SERVER_PID_FILE := .server.pid
SERVER_PORT := 32108
CLASSPATH_FILE := java-lib/build/classpath.txt

all: java karate playwright cypress selenium

test: all

test-fast: java karate-fast playwright-fast cypress-fast selenium-fast

playwright:
	npm --prefix $(PLAYWRIGHT_DIR) ci --no-audit --no-fund
	$(PLAYWRIGHT_BIN) install --with-deps
	$(PLAYWRIGHT_BIN) test

playwright-fast:
	npm --prefix $(PLAYWRIGHT_DIR) ci --no-audit --no-fund
	$(PLAYWRIGHT_BIN) install --with-deps
	$(PLAYWRIGHT_BIN) test --grep @fast

playwright-gradle:
	./gradlew :playwright-tests:test --no-daemon

playwright-gradle-fast:
	./gradlew :playwright-tests:testFast --no-daemon

cypress:
	npm --prefix $(CYPRESS_DIR) install --no-audit --no-fund
	$(CYPRESS_BIN) install
	$(CYPRESS_BIN) run --project $(CYPRESS_DIR)

cypress-fast:
	npm --prefix $(CYPRESS_DIR) install --no-audit --no-fund
	$(CYPRESS_BIN) install
	$(CYPRESS_BIN) run --project $(CYPRESS_DIR) --spec $(CYPRESS_DIR)/cypress/e2e/local.cy.js

cypress-gradle:
	./gradlew :cypress-tests:test --no-daemon

cypress-gradle-fast:
	./gradlew :cypress-tests:testFast --no-daemon

selenium:
	./gradlew :selenium-tests:test --no-daemon

selenium-fast:
	./gradlew :selenium-tests:test --no-daemon --tests '*SeleniumFastTest'

java:
	./gradlew :java-lib:test --no-daemon

karate:
	./gradlew :karate-tests:test --no-daemon

karate-fast:
	./gradlew :karate-tests:test --no-daemon --tests '*KarateFastTest'

clean:
	./gradlew clean --no-daemon

# Idempotent server start:
#   - If port $(SERVER_PORT) is already in use, skips startup (assumes healthy)
#   - Otherwise compiles java-lib, resolves classpath, and starts the server as a
#     fully independent background process (survives Gradle shutdown)
#   - Waits for /health to return 200
start-server:
	@echo "Checking port $(SERVER_PORT)..."
	@PID=$$(lsof -ti:$(SERVER_PORT) 2>/dev/null || true); \
	if [ -n "$$PID" ]; then \
		echo "Port $(SERVER_PORT) already in use (PID: $$PID). Skipping server start."; \
	else \
		echo "Starting HTTP server on port $(SERVER_PORT)..."; \
		./gradlew :java-lib:classes :java-lib:printClasspath --no-daemon > /dev/null 2>&1; \
		CLASSPATH=$$(cat $(CLASSPATH_FILE)); \
		SERVER_PORT=$(SERVER_PORT) nohup java -cp "$$CLASSPATH" com.example.server.HttpServer > /dev/null 2>&1 & \
		echo $$! > $(SERVER_PID_FILE); \
	fi; \
	echo "Waiting for server health check..."; \
	for i in 1 2 3 4 5 6 7 8 9 10; do \
		if curl -sf http://localhost:$(SERVER_PORT)/health > /dev/null 2>&1; then \
			PID=$$(lsof -ti:$(SERVER_PORT) 2>/dev/null || true); \
			echo "Server is healthy (PID: $$PID)"; \
			exit 0; \
		fi; \
		sleep 1; \
	done; \
	echo "Server failed to become healthy within 10s"; \
	exit 1

stop-server:
	@if [ -f $(SERVER_PID_FILE) ]; then \
		PID=$$(cat $(SERVER_PID_FILE)); \
		echo "Stopping server (PID: $$PID)..."; \
		kill $$PID 2>/dev/null || true; \
		rm -f $(SERVER_PID_FILE); \
		echo "Server stopped"; \
	else \
		PID=$$(lsof -ti:$(SERVER_PORT) 2>/dev/null || true); \
		if [ -n "$$PID" ]; then \
			echo "Stopping server on port $(SERVER_PORT) (PID: $$PID)..."; \
			kill $$PID 2>/dev/null || true; \
			echo "Server stopped"; \
		else \
			echo "No server process found on port $(SERVER_PORT)"; \
		fi; \
	fi

# Run Gatling performance tests (starts server if needed, stops after)
perf-test: start-server
	@echo "Running Gatling performance tests..."
	@./gradlew :gatling-tests:gatlingRun --no-daemon; \
	EXIT_CODE=$$?; \
	$(MAKE) stop-server || true; \
	exit $$EXIT_CODE

# Run k6 performance tests (starts server if needed, stops after)
k6-test: start-server
	@echo "Running k6 performance tests..."
	@(cd k6-test && k6 run scenarios/math_api.js); \
	EXIT_CODE=$$?; \
	$(MAKE) stop-server || true; \
	exit $$EXIT_CODE
