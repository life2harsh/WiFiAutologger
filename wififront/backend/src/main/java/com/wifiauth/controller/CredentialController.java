package com.wifiauth.controller;

import com.wifiauth.model.Credential;
import com.wifiauth.service.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/credentials")
@CrossOrigin(origins = "*")
public class CredentialController {

    @Autowired
    private CredentialService credentialService;

    @GetMapping
    public List<Credential> getAllCredentials() {
        return credentialService.getAllCredentials();
    }

    @PostMapping
    public ResponseEntity<Credential> addCredential(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        if (username == null || password == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Credential credential = credentialService.saveCredential(username, password);
        return ResponseEntity.ok(credential);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCredential(@PathVariable Long id) {
        credentialService.deleteCredential(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        if (username != null && password != null) {
            boolean success = credentialService.attemptLogin(username, password);
            return ResponseEntity.ok(Map.of("success", success, "message", 
                success ? "Login successful" : "Login failed"));
        } else {
            boolean success = credentialService.loginWithStoredCredentials();
            return ResponseEntity.ok(Map.of("success", success, "message", 
                success ? "Login successful with stored credentials" : "Login failed"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        boolean success = credentialService.attemptLogout();
        return ResponseEntity.ok(Map.of("success", success, "message", 
            success ? "Logout successful" : "Logout failed"));
    }
}
