.PHONY: all test test-fast playwright playwright-fast playwright-gradle playwright-gradle-fast cypress cypress-fast cypress-gradle cypress-gradle-fast selenium selenium-fast karate karate-fast java clean

PLAYWRIGHT_DIR := playwright-tests
PLAYWRIGHT_BIN := $(PLAYWRIGHT_DIR)/node_modules/.bin/playwright

CYPRESS_DIR := cypress-tests
CYPRESS_BIN := $(CYPRESS_DIR)/node_modules/.bin/cypress

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
