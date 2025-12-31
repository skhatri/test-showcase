# Example Project (for QWay Git + Task Types)

This repo is designed to be a **known-good Git execution target** for QWay Task Types:

- `playwright` (Node-based) → runs a browser test against `google.com.au`
- `cypress` (Node-based) → runs an E2E test suite (includes a fast local smoke test)
- `selenium` (Java-based) → runs WebDriver tests (HtmlUnit driver for fast/local)
- `gradle` (Java-based) → runs a small Java module unit test suite
- `karate` (Gradle-based) → runs a Karate feature that calls SpaceX `/launches`
- `make` → provides a single wrapper interface over both Playwright + Gradle/Karate

## Quickstart

Run everything:

```bash
make test
```

Run just Playwright:

```bash
make playwright
```

Run just Cypress:

```bash
make cypress
```

Run just Selenium:

```bash
make selenium
```

Run Playwright via Gradle:

```bash
make playwright-gradle
```

Run just Java unit tests:

```bash
make java
```

Run just Karate:

```bash
make karate
```

## Notes (important)

- **Network-dependent tests**:
  - Playwright uses `google.com.au` (consent/captcha can sometimes appear)
  - Cypress includes a `google.com.au` spec as well
  - Selenium includes a `google.com.au` test as well
  - Karate calls SpaceX API (rate limits / occasional downtime)
- For deterministic/offline runs, use:

```bash
make test-fast
```

