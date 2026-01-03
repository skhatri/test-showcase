# Gatling Performance Tests

This module contains a Java 17 HTTP server and Gatling performance tests.

## HTTP Server

The server provides two mathematical endpoints:

### Endpoints

1. **Add**: `http://localhost:32108/add?num1=X&num2=Y`
   - Adds two numbers together
   - Example: `/add?num1=10&num2=20` returns `{"num1": 10.00, "num2": 20.00, "operation": "add", "result": 30.00}`

2. **Multiply**: `http://localhost:32108/multiply?num1=X&num2=Y`
   - Multiplies two numbers
   - Example: `/multiply?num1=5&num2=6` returns `{"num1": 5.00, "num2": 6.00, "operation": "multiply", "result": 30.00}`

3. **Health**: `http://localhost:32108/health`
   - Health check endpoint
   - Returns `{"status": "ok"}`

### Running the Server

**Using Make (recommended):**
```bash
make start-server   # Start the server in background
make stop-server    # Stop the server
```

**Using Gradle:**
```bash
./gradlew :gatling-tests:runServer
```

Or directly with Java:
```bash
./gradlew :gatling-tests:classes
java -cp gatling-tests/build/classes/java/main com.example.server.HttpServer
```

## Gatling Performance Tests

The module includes comprehensive Gatling performance tests that simulate load on the HTTP server.

### Test Scenarios

1. **Add Numbers Test**: Tests the `/add` endpoint with constant load
2. **Multiply Numbers Test**: Tests the `/multiply` endpoint with constant load
3. **Mixed Operations Test**: Tests both endpoints with random number combinations

### Running Performance Tests

```bash
make perf-test
```

Or using Gradle directly:
```bash
./gradlew :gatling-tests:gatlingRun
```

### Test Flow

**Automated (recommended):**
```bash
make perf-test
```
This will automatically start the server, run the tests, and stop the server when done.

**Manual:**
1. Start the HTTP server: `make start-server`
2. Run the performance tests: `./gradlew :gatling-tests:gatlingRun`
3. Stop the server: `make stop-server`
4. View the Gatling report (location printed at the end of the test run)

### Performance Assertions

The tests include the following assertions:
- Maximum response time < 500ms
- Successful requests > 95%

### Load Profile

- Add/Multiply scenarios: Ramp from 1 to 10 users/sec over 30 seconds, then maintain 10 users/sec for 60 seconds
- Mixed scenario: Constant 5 users/sec for 90 seconds
