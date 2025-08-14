package com.wifiauth.controller;

import com.wifiauth.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activity-log")
@CrossOrigin(origins = "*")
public class ActivityLogController {

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getActivityLogs() {
        List<String> logs = activityLogService.getAllLogs();
        return ResponseEntity.ok(Map.of("logs", logs));
    }

    @PostMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearActivityLogs() {
        activityLogService.clearLogs();
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Activity logs cleared successfully"
        ));
    }
}
