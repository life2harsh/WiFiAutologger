package com.wifiauth.service;

import com.wifiauth.model.Credential;
import com.wifiauth.repository.CredentialRepository;
import com.wifiauth.util.CredentialEncryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List; 

@Service
public class CredentialService {

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private ActivityLogService activityLogService;

    private static final String LOGIN_URL = "http://172.16.68.6:8090/login.xml";
    private static final String LOGOUT_URL = "http://172.16.68.6:8090/logout.xml";
    private static final String LOGIN_MODE = "191";
    private static final String LOGOUT_MODE = "193";
    private static final String PRODUCTTYPE = "0";

    public List<Credential> getAllCredentials() {
        return credentialRepository.findAll();
    }

    public Credential saveCredential(String username, String password) {
        String encryptedPassword = CredentialEncryption.encrypt(password);
        Credential credential = new Credential(username, encryptedPassword);
        Credential saved = credentialRepository.save(credential);
        
        activityLogService.addLog(String.format("[%s]: New credential added for user: %s", 
            getCurrentTimestamp(), username));
        
        return saved;
    }

    public void deleteCredential(Long id) {
        Credential credential = credentialRepository.findById(id).orElse(null);
        if (credential != null) {
            credentialRepository.deleteById(id);
            activityLogService.addLog(String.format("[%s]: Credential deleted for user: %s", 
                getCurrentTimestamp(), credential.getUsername()));
        }
    }

    public boolean attemptLogin(String username, String password) {
        try {
            // Use the exact format from your working Java application
            String urlParameters = String.format(
                "mode=%s&username=%s&password=%s&a=%d&producttype=%s",
                LOGIN_MODE,
                URLEncoder.encode(username, StandardCharsets.UTF_8),
                URLEncoder.encode(password, StandardCharsets.UTF_8),
                System.currentTimeMillis(),
                PRODUCTTYPE
            );

            URL url = new URL(LOGIN_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            
            // Add browser-like headers that match your working application
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("User-Agent", "WiFi-Authenticator/2.0");
            connection.setRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            // Send the parameters
            try (OutputStream os = connection.getOutputStream()) {
                os.write(urlParameters.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            
            // Read response to check for success/failure message
            String response = "";
            try {
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder responseBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                response = responseBuilder.toString();
                reader.close();
            } catch (Exception e) {
                // Handle error response
            }

            activityLogService.addLog(String.format("[%s]: Login attempt for user: %s - Response: %d", 
                getCurrentTimestamp(), username, responseCode));
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Check if response contains success indicators
                if (response.contains("LIVE") || response.contains("status")) {
                    // Parse XML response to check actual status
                    if (response.contains("<status><![CDATA[LIVE]]></status>")) {
                        activityLogService.addLog(String.format("[%s]: Login successful for user: %s", 
                            getCurrentTimestamp(), username));
                        return true;
                    } else {
                        activityLogService.addLog(String.format("[%s]: Login failed for user: %s - Portal rejected credentials", 
                            getCurrentTimestamp(), username));
                        return false;
                    }
                } else {
                    activityLogService.addLog(String.format("[%s]: Login successful for user: %s (assuming success)", 
                        getCurrentTimestamp(), username));
                    return true;
                }
            } else {
                activityLogService.addLog(String.format("[%s]: Login failed for user: %s (Response: %d)", 
                    getCurrentTimestamp(), username, responseCode));
                return false;
            }
        } catch (Exception e) {
            activityLogService.addLog(String.format("[%s]: Login error for user: %s - %s", 
                getCurrentTimestamp(), username, e.getMessage()));
            return false;
        }
    }

    public boolean attemptLogout() {
        try {
            // Include username in logout (from your working application)
            String urlParameters = String.format(
                "mode=%s&username=%s&a=%d&producttype=%s",
                LOGOUT_MODE,
                "user", // Default username for logout
                System.currentTimeMillis(),
                PRODUCTTYPE
            );

            URL url = new URL(LOGOUT_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            
            // Add the same headers as login
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("User-Agent", "WiFi-Authenticator/2.0");
            connection.setRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            // Send the parameters
            try (OutputStream os = connection.getOutputStream()) {
                os.write(urlParameters.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            
            activityLogService.addLog(String.format("[%s]: Logout attempt - Response: %d", 
                getCurrentTimestamp(), responseCode));
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                activityLogService.addLog(String.format("[%s]: Logout successful", getCurrentTimestamp()));
                return true;
            } else {
                activityLogService.addLog(String.format("[%s]: Logout failed (Response: %d)", 
                    getCurrentTimestamp(), responseCode));
                return false;
            }
        } catch (java.net.SocketTimeoutException e) {
            // Timeout often means successful logout for WiFi systems
            activityLogService.addLog(String.format("[%s]: Logout timed out - likely successful", 
                getCurrentTimestamp()));
            return true;
        } catch (Exception e) {
            activityLogService.addLog(String.format("[%s]: Logout error - %s", 
                getCurrentTimestamp(), e.getMessage()));
            return false;
        }
    }

    public boolean loginWithStoredCredentials() {
        List<Credential> credentials = getAllCredentials();
        
        for (Credential credential : credentials) {
            String password = CredentialEncryption.decrypt(credential.getEncryptedPassword());
            if (attemptLogin(credential.getUsername(), password)) {
                credential.setStatus("Logged In");
                credentialRepository.save(credential);
                return true;
            }
        }
        
        activityLogService.addLog(String.format("[%s]: No successful login with stored credentials", 
            getCurrentTimestamp()));
        return false;
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
