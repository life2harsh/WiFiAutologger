@echo off
echo Starting WiFi AutoLogger Backend...
cd /d "%~dp0"
mvn spring-boot:run
pause
