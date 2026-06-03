package com.example.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.BindException;
import java.util.HashMap;
import java.util.Map;

/**
 * A lightweight, idempotent HTTP server for test scenarios.
 *
 * Idempotent startup: if the configured port is already in use, the server
 * prints a message and exits cleanly (exit code 0) without crashing.
 *
 * Environment variables:
 *   SERVER_PORT  – override the default port (default: 32108)
 */
public final class HttpServer {

    private static final int DEFAULT_PORT = 32108;

    public static void main(String[] args) throws IOException {
        int port = parsePort();
        InetSocketAddress addr = new InetSocketAddress("0.0.0.0", port);

        com.sun.net.httpserver.HttpServer jdkServer;
        try {
            jdkServer = com.sun.net.httpserver.HttpServer.create(addr, 0);
        } catch (BindException e) {
            System.err.println("Port " + port + " is already in use. Server not started.");
            return;
        }

        jdkServer.createContext("/add", new AddHandler());
        jdkServer.createContext("/multiply", new MultiplyHandler());
        jdkServer.createContext("/health", new HealthHandler());

        jdkServer.setExecutor(null);
        jdkServer.start();

        System.out.println("Server started on port " + port);
        System.out.println("Endpoints:");
        System.out.println("  - http://localhost:" + port + "/add?num1=X&num2=Y");
        System.out.println("  - http://localhost:" + port + "/multiply?num1=X&num2=Y");
        System.out.println("  - http://localhost:" + port + "/health");
    }

    private static int parsePort() {
        try {
            String env = System.getenv("SERVER_PORT");
            if (env != null && !env.isBlank()) {
                return Integer.parseInt(env.trim());
            }
        } catch (NumberFormatException ignored) {
            // fall through to default
        }
        return DEFAULT_PORT;
    }

    // ---------- handlers ----------

    static class AddHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> params = parseQuery(exchange.getRequestURI());

            try {
                double num1 = Double.parseDouble(params.getOrDefault("num1", "0"));
                double num2 = Double.parseDouble(params.getOrDefault("num2", "0"));
                double result = num1 + num2;

                String response = String.format(
                    "{\"num1\": %.2f, \"num2\": %.2f, \"operation\": \"add\", \"result\": %.2f}",
                    num1, num2, result);
                sendResponse(exchange, 200, response);
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "{\"error\": \"Invalid number format\"}");
            }
        }
    }

    static class MultiplyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> params = parseQuery(exchange.getRequestURI());

            try {
                double num1 = Double.parseDouble(params.getOrDefault("num1", "0"));
                double num2 = Double.parseDouble(params.getOrDefault("num2", "0"));
                double result = num1 * num2;

                String response = String.format(
                    "{\"num1\": %.2f, \"num2\": %.2f, \"operation\": \"multiply\", \"result\": %.2f}",
                    num1, num2, result);
                sendResponse(exchange, 200, response);
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "{\"error\": \"Invalid number format\"}");
            }
        }
    }

    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            sendResponse(exchange, 200, "{\"status\": \"ok\"}");
        }
    }

    // ---------- utilities ----------

    private static Map<String, String> parseQuery(URI uri) {
        Map<String, String> params = new HashMap<>();
        String query = uri.getQuery();
        if (query != null) {
            for (String pair : query.split("&")) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    params.put(kv[0], kv[1]);
                }
            }
        }
        return params;
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
