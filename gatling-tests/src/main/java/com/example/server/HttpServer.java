package com.example.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
    private static final int PORT = 32108;

    public static void main(String[] args) throws IOException {
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(
            new InetSocketAddress(PORT), 0);

        server.createContext("/add", new AddHandler());
        server.createContext("/multiply", new MultiplyHandler());
        server.createContext("/health", new HealthHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("Server started on port " + PORT);
        System.out.println("Endpoints:");
        System.out.println("  - http://localhost:" + PORT + "/add?num1=X&num2=Y");
        System.out.println("  - http://localhost:" + PORT + "/multiply?num1=X&num2=Y");
        System.out.println("  - http://localhost:" + PORT + "/health");
    }

    static class AddHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> params = parseQuery(exchange.getRequestURI());

            try {
                double num1 = Double.parseDouble(params.getOrDefault("num1", "0"));
                double num2 = Double.parseDouble(params.getOrDefault("num2", "0"));
                double result = num1 + num2;

                String response = String.format("{\"num1\": %.2f, \"num2\": %.2f, \"operation\": \"add\", \"result\": %.2f}",
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

                String response = String.format("{\"num1\": %.2f, \"num2\": %.2f, \"operation\": \"multiply\", \"result\": %.2f}",
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

    private static Map<String, String> parseQuery(URI uri) {
        Map<String, String> params = new HashMap<>();
        String query = uri.getQuery();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
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
