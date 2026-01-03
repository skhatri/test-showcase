.PHONY: all test test-fast playwright playwright-fast playwright-gradle playwright-gradle-fast cypress cypress-fast cypress-gradle cypress-gradle-fast selenium selenium-fast karate karate-fast java clean perf-test start-server stop-server

PLAYWRIGHT_DIR := playwright-tests
PLAYWRIGHT_BIN := $(PLAYWRIGHT_DIR)/node_modules/.bin/playwright

CYPRESS_DIR := cypress-tests
CYPRESS_BIN := $(CYPRESS_DIR)/node_modules/.bin/cypress

SERVER_PID_FILE := .server.pid
SERVER_PORT := 32108

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

start-server:
	@echo "Starting HTTP server on port $(SERVER_PORT)..."
	@./gradlew :gatling-tests:runServer --no-daemon > /dev/null 2>&1 & echo $$! > $(SERVER_PID_FILE)
	@echo "Waiting for server to start..."
	@for i in 1 2 3 4 5; do \
		if curl -s http://localhost:$(SERVER_PORT)/health > /dev/null 2>&1; then \
			echo "Server started successfully (PID: $$(cat $(SERVER_PID_FILE)))"; \
			exit 0; \
		fi; \
		sleep 1; \
	done; \
	echo "Server failed to start" && exit 1

stop-server:
	@if [ -f $(SERVER_PID_FILE) ]; then \
		PID=$$(cat $(SERVER_PID_FILE)); \
		echo "Stopping server (PID: $$PID)..."; \
		kill $$PID 2>/dev/null || true; \
		rm -f $(SERVER_PID_FILE); \
		echo "Server stopped"; \
	else \
		echo "Server PID file not found. Checking for process on port $(SERVER_PORT)..."; \
		PID=$$(lsof -ti:$(SERVER_PORT)); \
		if [ -n "$$PID" ]; then \
			echo "Found process $$PID on port $(SERVER_PORT), killing it..."; \
			kill $$PID 2>/dev/null || true; \
		else \
			echo "No server process found"; \
		fi; \
	fi

perf-test: start-server
	@echo "Running Gatling performance tests..."
	@./gradlew :gatling-tests:gatlingRun --no-daemon; \
	EXIT_CODE=$$?; \
	$(MAKE) stop-server; \
	exit $$EXIT_CODE
