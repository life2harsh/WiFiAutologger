package com.wifiauth.controller;

import com.wifiauth.service.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/scheduler")
@CrossOrigin(origins = "*")
public class SchedulerController {

    @Autowired
    private SchedulerService schedulerService;

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startScheduler(@RequestBody Map<String, Object> request) {
        String type = (String) request.get("type");
        int interval = (Integer) request.getOrDefault("interval", 30);
        String timeUnit = (String) request.getOrDefault("timeUnit", "minutes");
        String specificTime = (String) request.getOrDefault("specificTime", "09:00");
        boolean autoLogin = (Boolean) request.getOrDefault("autoLogin", true);

        schedulerService.startScheduler(type, interval, timeUnit, specificTime, autoLogin);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Scheduler started successfully",
            "status", schedulerService.getStatus()
        ));
    }

    @PostMapping("/stop")
    public ResponseEntity<Map<String, Object>> stopScheduler() {
        schedulerService.stopScheduler();
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Scheduler stopped successfully",
            "status", schedulerService.getStatus()
        ));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSchedulerStatus() {
        return ResponseEntity.ok(Map.of(
            "isRunning", schedulerService.isRunning(),
            "status", schedulerService.getStatus()
        ));
    }
}
