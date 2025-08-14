@echo off
echo Compiling WiFi Auth Server...
javac WifiAuthServer.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo Starting WiFi Auth Server on port 8080...
    java WifiAuthServer
) else (
    echo Compilation failed!
    pause
)
