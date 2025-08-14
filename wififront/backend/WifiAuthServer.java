import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

public class WifiAuthServer {
    private static final int PORT = 8080;
    private static final String LOGIN_URL = "http://172.16.68.6:8090/login.xml";
    private static final String LOGOUT_URL = "http://172.16.68.6:8090/logout.xml";
    private static final String LOGIN_MODE = "191";
    private static final String LOGOUT_MODE = "193";
    private static final String PRODUCTTYPE = "0";
    
    private static List<Credential> credentials = new ArrayList<>();
    private static List<String> activityLogs = new ArrayList<>();
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static boolean isSchedulerRunning = false;
    
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        server.createContext("/api/credentials", new CredentialsHandler());
        server.createContext("/api/scheduler", new SchedulerHandler());
        server.createContext("/api/activity-log", new ActivityLogHandler());
        
        server.setExecutor(null);
        server.start();
        
        addLog("Server started on port " + PORT);
        System.out.println("WiFi Auth Server started on port " + PORT);
    }
    
    static class CredentialsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, 0);
                exchange.close();
                return;
            }
            
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            
            if ("GET".equals(method)) {
                handleGetCredentials(exchange);
            } else if ("POST".equals(method)) {
                if (path.endsWith("/login")) {
                    handleLogin(exchange);
                } else if (path.endsWith("/logout")) {
                    handleLogout(exchange);
                } else {
                    handleAddCredential(exchange);
                }
            } else if ("DELETE".equals(method)) {
                handleDeleteCredential(exchange);
            }
        }
        
        private void handleGetCredentials(HttpExchange exchange) throws IOException {
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < credentials.size(); i++) {
                if (i > 0) json.append(",");
                Credential cred = credentials.get(i);
                json.append("{")
                    .append("\"id\":").append(cred.id).append(",")
                    .append("\"username\":\"").append(cred.username).append("\",")
                    .append("\"status\":\"").append(cred.status).append("\"")
                    .append("}");
            }
            json.append("]");
            
            sendResponse(exchange, 200, json.toString());
        }
        
        private void handleAddCredential(HttpExchange exchange) throws IOException {
            String requestBody = readRequestBody(exchange);
            Map<String, String> data = parseJson(requestBody);
            
            String username = data.get("username");
            String password = data.get("password");
            
            if (username != null && password != null) {
                Credential cred = new Credential(username, password);
                credentials.add(cred);
                addLog("New credential added for user: " + username);
                
                String response = "{\"id\":" + cred.id + ",\"username\":\"" + username + "\",\"status\":\"Ready\"}";
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 400, "{\"error\":\"Missing username or password\"}");
            }
        }
        
        private void handleDeleteCredential(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length > 0) {
                try {
                    int id = Integer.parseInt(parts[parts.length - 1]);
                    credentials.removeIf(cred -> cred.id == id);
                    addLog("Credential deleted (ID: " + id + ")");
                    sendResponse(exchange, 200, "{\"success\":true}");
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "{\"error\":\"Invalid ID\"}");
                }
            }
        }
        
        private void handleLogin(HttpExchange exchange) throws IOException {
            String requestBody = readRequestBody(exchange);
            Map<String, String> data = parseJson(requestBody);
            
            String username = data.get("username");
            String password = data.get("password");
            
            boolean success;
            if (username != null && password != null) {
                success = attemptLogin(username, password);
            } else {
                success = loginWithStoredCredentials();
            }
            
            String message = success ? "Login successful" : "Login failed";
            String response = "{\"success\":" + success + ",\"message\":\"" + message + "\"}";
            sendResponse(exchange, 200, response);
        }
        
        private void handleLogout(HttpExchange exchange) throws IOException {
            boolean success = attemptLogout();
            String message = success ? "Logout successful" : "Logout failed";
            String response = "{\"success\":" + success + ",\"message\":\"" + message + "\"}";
            sendResponse(exchange, 200, response);
        }
    }
    
    static class SchedulerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, 0);
                exchange.close();
                return;
            }
            
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            
            if ("POST".equals(method)) {
                if (path.endsWith("/start")) {
                    handleStartScheduler(exchange);
                } else if (path.endsWith("/stop")) {
                    handleStopScheduler(exchange);
                }
            } else if ("GET".equals(method) && path.endsWith("/status")) {
                handleGetStatus(exchange);
            }
        }
        
        private void handleStartScheduler(HttpExchange exchange) throws IOException {
            String requestBody = readRequestBody(exchange);
            Map<String, Object> data = parseJsonObject(requestBody);
            
            String type = (String) data.get("type");
            int interval = data.get("interval") != null ? ((Number) data.get("interval")).intValue() : 30;
            String timeUnit = (String) data.getOrDefault("timeUnit", "minutes");
            boolean autoLogin = data.get("autoLogin") != null ? (Boolean) data.get("autoLogin") : true;
            
            startScheduler(type, interval, timeUnit, autoLogin);
            
            String response = "{\"success\":true,\"message\":\"Scheduler started successfully\",\"status\":\"Running\"}";
            sendResponse(exchange, 200, response);
        }
        
        private void handleStopScheduler(HttpExchange exchange) throws IOException {
            stopScheduler();
            String response = "{\"success\":true,\"message\":\"Scheduler stopped successfully\",\"status\":\"Stopped\"}";
            sendResponse(exchange, 200, response);
        }
        
        private void handleGetStatus(HttpExchange exchange) throws IOException {
            String status = isSchedulerRunning ? "Running" : "Stopped";
            String response = "{\"isRunning\":" + isSchedulerRunning + ",\"status\":\"" + status + "\"}";
            sendResponse(exchange, 200, response);
        }
    }
    
    static class ActivityLogHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, 0);
                exchange.close();
                return;
            }
            
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            
            if ("GET".equals(method)) {
                handleGetLogs(exchange);
            } else if ("POST".equals(method) && path.endsWith("/clear")) {
                handleClearLogs(exchange);
            }
        }
        
        private void handleGetLogs(HttpExchange exchange) throws IOException {
            StringBuilder json = new StringBuilder("{\"logs\":[");
            for (int i = 0; i < activityLogs.size(); i++) {
                if (i > 0) json.append(",");
                json.append("\"").append(activityLogs.get(i).replace("\"", "\\\"")).append("\"");
            }
            json.append("]}");
            sendResponse(exchange, 200, json.toString());
        }
        
        private void handleClearLogs(HttpExchange exchange) throws IOException {
            activityLogs.clear();
            addLog("Activity log cleared");
            String response = "{\"success\":true,\"message\":\"Activity logs cleared successfully\"}";
            sendResponse(exchange, 200, response);
        }
    }
    
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
    
    private static String readRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            return body.toString();
        }
    }
    
    private static Map<String, String> parseJson(String json) {
        Map<String, String> result = new HashMap<>();
        if (json == null || json.trim().isEmpty()) return result;
        
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
            String[] pairs = json.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim().replace("\"", "");
                    result.put(key, value);
                }
            }
        }
        return result;
    }
    
    private static Map<String, Object> parseJsonObject(String json) {
        Map<String, Object> result = new HashMap<>();
        if (json == null || json.trim().isEmpty()) return result;
        
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
            String[] pairs = json.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replace("\"", "");
                    String valueStr = keyValue[1].trim();
                    
                    Object value;
                    if (valueStr.equals("true") || valueStr.equals("false")) {
                        value = Boolean.parseBoolean(valueStr);
                    } else if (valueStr.matches("\\d+")) {
                        value = Integer.parseInt(valueStr);
                    } else {
                        value = valueStr.replace("\"", "");
                    }
                    result.put(key, value);
                }
            }
        }
        return result;
    }
    
    private static void addLog(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        activityLogs.add("[" + timestamp + "]: " + message);
        if (activityLogs.size() > 100) {
            activityLogs.remove(0);
        }
        System.out.println("[" + timestamp + "]: " + message);
    }
    
    private static boolean attemptLogin(String username, String password) {
        try {
            String params = String.format("mode=%s&username=%s&password=%s&producttype=%s", 
                LOGIN_MODE, username, password, PRODUCTTYPE);
            
            URL url = new URL(LOGIN_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            connection.getOutputStream().write(params.getBytes(StandardCharsets.UTF_8));
            
            int responseCode = connection.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                addLog("Login successful for user: " + username);
                return true;
            } else {
                addLog("Login failed for user: " + username + " (Response: " + responseCode + ")");
                return false;
            }
        } catch (Exception e) {
            addLog("Login error for user: " + username + " - " + e.getMessage());
            return false;
        }
    }
    
    private static boolean attemptLogout() {
        try {
            String params = String.format("mode=%s", LOGOUT_MODE);
            
            URL url = new URL(LOGOUT_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            connection.getOutputStream().write(params.getBytes(StandardCharsets.UTF_8));
            
            int responseCode = connection.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                addLog("Logout successful");
                return true;
            } else {
                addLog("Logout failed (Response: " + responseCode + ")");
                return false;
            }
        } catch (Exception e) {
            addLog("Logout error - " + e.getMessage());
            return false;
        }
    }
    
    private static boolean loginWithStoredCredentials() {
        for (Credential credential : credentials) {
            String password = CredentialEncryption.decrypt(credential.encryptedPassword);
            if (attemptLogin(credential.username, password)) {
                credential.status = "Logged In";
                return true;
            }
        }
        addLog("No successful login with stored credentials");
        return false;
    }
    
    private static void startScheduler(String type, int interval, String timeUnit, boolean autoLogin) {
        stopScheduler();
        
        long intervalMillis = convertToMillis(interval, timeUnit);
        
        scheduler.scheduleAtFixedRate(() -> {
            addLog("Scheduled login attempt");
            loginWithStoredCredentials();
        }, 0, intervalMillis, TimeUnit.MILLISECONDS);
        
        isSchedulerRunning = true;
        addLog("Scheduler started (" + type + ")");
        
        if (autoLogin) {
            loginWithStoredCredentials();
        }
    }
    
    private static void stopScheduler() {
        scheduler.shutdownNow();
        scheduler = Executors.newScheduledThreadPool(1);
        isSchedulerRunning = false;
        addLog("Scheduler stopped");
    }
    
    private static long convertToMillis(int value, String unit) {
        return switch (unit.toLowerCase()) {
            case "seconds" -> value * 1000L;
            case "minutes" -> value * 60 * 1000L;
            case "hours" -> value * 60 * 60 * 1000L;
            default -> value * 60 * 1000L;
        };
    }
    
    static class Credential {
        static int nextId = 1;
        int id;
        String username;
        String encryptedPassword;
        String status = "Ready";
        
        public Credential(String username, String password) {
            this.id = nextId++;
            this.username = username;
            this.encryptedPassword = CredentialEncryption.encrypt(password);
        }
    }
    
    static class CredentialEncryption {
        private static final String ALGORITHM = "AES";
        private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
        private static final String SECRET_KEY = "WifiAuthLogger2024";

        private static SecretKeySpec getSecretKey() {
            try {
                byte[] key = SECRET_KEY.getBytes("UTF-8");
                MessageDigest sha = MessageDigest.getInstance("SHA-256");
                key = sha.digest(key);
                key = Arrays.copyOf(key, 16);
                return new SecretKeySpec(key, ALGORITHM);
            } catch (Exception e) {
                throw new RuntimeException("Error creating secret key", e);
            }
        }

        public static String encrypt(String plainText) {
            try {
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
                byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
                return Base64.getEncoder().encodeToString(encryptedBytes);
            } catch (Exception e) {
                throw new RuntimeException("Error encrypting data", e);
            }
        }

        public static String decrypt(String encryptedText) {
            try {
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
                byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
                return new String(decryptedBytes, "UTF-8");
            } catch (Exception e) {
                throw new RuntimeException("Error decrypting data", e);
            }
        }
    }
}
