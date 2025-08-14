package com.wifiauth.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityLogService {
    
    private List<String> activityLogs = new ArrayList<>();
    
    public void addLog(String message) {
        activityLogs.add(message);
        // Keep only the last 100 logs to prevent memory issues
        if (activityLogs.size() > 100) {
            activityLogs.remove(0);
        }
    }
    
    public List<String> getAllLogs() {
        return new ArrayList<>(activityLogs);
    }
    
    public void clearLogs() {
        activityLogs.clear();
        addLog(String.format("[%s]: Activity log cleared", getCurrentTimestamp()));
    }
    
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
