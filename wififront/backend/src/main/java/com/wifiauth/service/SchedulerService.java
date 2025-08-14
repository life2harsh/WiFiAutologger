package com.wifiauth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ScheduledFuture;

@Service
public class SchedulerService {

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private ActivityLogService activityLogService;

    private TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledTask;
    private boolean isRunning = false;
    private String schedulerType = "interval"; // "interval" or "specific"
    private int intervalValue = 30;
    private String timeUnit = "minutes";
    private String specificTime = "09:00";
    private boolean autoLogin = true;

    public SchedulerService() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("scheduler-");
        scheduler.initialize();
        this.taskScheduler = scheduler;
    }

    public void startScheduler(String type, int interval, String unit, String time, boolean autoLoginEnabled) {
        stopScheduler();
        
        this.schedulerType = type;
        this.intervalValue = interval;
        this.timeUnit = unit;
        this.specificTime = time;
        this.autoLogin = autoLoginEnabled;

        if ("interval".equals(type)) {
            startIntervalScheduler();
        } else if ("specific".equals(type)) {
            startSpecificTimeScheduler();
        }

        isRunning = true;
        activityLogService.addLog(String.format("[%s]: Scheduler started (%s)", 
            getCurrentTimestamp(), type));

        // Auto-login on start if enabled
        if (autoLogin) {
            credentialService.loginWithStoredCredentials();
        }
    }

    public void stopScheduler() {
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(false);
        }
        isRunning = false;
        activityLogService.addLog(String.format("[%s]: Scheduler stopped", getCurrentTimestamp()));
    }

    private void startIntervalScheduler() {
        long intervalMillis = convertToMillis(intervalValue, timeUnit);
        
        scheduledTask = taskScheduler.scheduleAtFixedRate(() -> {
            activityLogService.addLog(String.format("[%s]: Scheduled login attempt", getCurrentTimestamp()));
            credentialService.loginWithStoredCredentials();
        }, Duration.ofMillis(intervalMillis));
    }

    private void startSpecificTimeScheduler() {
        // For specific time, we'll check every minute if it's time to run
        scheduledTask = taskScheduler.scheduleAtFixedRate(() -> {
            LocalTime now = LocalTime.now();
            LocalTime targetTime = LocalTime.parse(specificTime);
            
            // Check if current time matches target time (within a minute)
            if (now.getHour() == targetTime.getHour() && now.getMinute() == targetTime.getMinute()) {
                activityLogService.addLog(String.format("[%s]: Scheduled login attempt (specific time)", getCurrentTimestamp()));
                credentialService.loginWithStoredCredentials();
            }
        }, Duration.ofMinutes(1));
    }

    private long convertToMillis(int value, String unit) {
        return switch (unit.toLowerCase()) {
            case "seconds" -> value * 1000L;
            case "minutes" -> value * 60 * 1000L;
            case "hours" -> value * 60 * 60 * 1000L;
            default -> value * 60 * 1000L; // default to minutes
        };
    }

    public boolean isRunning() {
        return isRunning;
    }

    public String getStatus() {
        if (!isRunning) {
            return "Stopped";
        }
        
        if ("interval".equals(schedulerType)) {
            return String.format("Running - Every %d %s", intervalValue, timeUnit);
        } else {
            return String.format("Running - Daily at %s", specificTime);
        }
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
