# WiFi AutoLogger

A modern, secure, and automated WiFi authentication manager with advanced scheduling and credential management.
<img width="1168" height="822" alt="image" src="https://github.com/user-attachments/assets/ada9e5a9-8b54-448c-8653-17e639679a40" />

## Features

### Secure Credential Management
- **AES-256 Encryption** for stored passwords
- Multiple credential support with failover
- Secure local storage (no cloud dependencies)
- Real-time credential status monitoring

### Advanced Scheduling
- **Interval-based scheduling** (seconds, minutes, hours)
- **Time-specific scheduling** (daily login at specific time)
- Auto-start scheduler on application launch
- Auto-login on system startup

### Modern UI Design
- **Dark theme** with professional styling
- Real-time connection status monitoring
- Comprehensive activity logging
- System tray integration with notifications

### Smart Features
- **Auto-retry logic** with multiple credentials
- **Network connectivity detection**
- **System startup integration**
- **Minimize to tray** functionality
- **Persistent settings** across sessions

## Requirements

- **Java 17+** (bundled in installer)
- **Windows 10/11** (primary support)
- **Network access** to WiFi authentication portal

## Installation

### Option 1: Download Installer (Recommended)
1. Download `WiFiAutoLogger-Setup.exe` from releases
<img width="603" height="473" alt="image" src="https://github.com/user-attachments/assets/f2bd3dcd-e986-4464-bc22-66c7586b0578" />

2. Run the installer as Administrator
3. Follow the setup wizard
4. Launch from Start Menu or Desktop shortcut

### Option 2: Build from Source
```powershell
# Clone repository
git clone https://github.com/life2harsh/wifi-autologger.git
cd wifi-autologger

# Build installer (requires Java 17+ and jpackage)
if (Test-Path custom-jre) { Remove-Item -Recurse -Force custom-jre }; if (Test-Path "*.exe") { Remove-Item -Force "*.exe" }; if (Test-Path "*.class") { Remove-Item -Force "*.class" }; javac ImprovedWifiAuthenticator.java; jar cfe WiFiAutoLogger.jar ImprovedWifiAuthenticator *.class; jlink --module-path "$env:JAVA_HOME\jmods" --add-modules java.base,java.desktop,java.logging,java.net.http,java.xml,java.naming,java.management --output custom-jre --compress=2 --no-header-files --no-man-pages --strip-debug; jpackage --runtime-image custom-jre --input . --name "WiFiAutoLogger" --main-jar WiFiAutoLogger.jar --main-class ImprovedWifiAuthenticator --type exe --description "Automated WiFi Login Manager" --vendor "WiFiAutoLogger" --app-version "2.0.0" --win-dir-chooser --win-menu --win-shortcut --win-console
```

## Quick Start

### 1. Add Your Credentials
- Enter your WiFi username and password
- Click "Add" to securely store credentials
- Add multiple accounts for automatic failover

### 2. Configure Scheduling
Choose your preferred method:
- **Interval**: Login every X minutes/hours
- **Specific Time**: Login daily at a specific time (e.g., 9:00 AM)

### 3. Enable Auto-Features
- **Auto-start scheduler**: Automatically start when app opens
- **Auto-login on start**: Login immediately when app starts
- **Start on system boot**: Launch with Windows
- **Minimize to tray**: Hide in system tray when closed

### 4. Start Automation
Click "Start Scheduler" and let WiFi AutoLogger handle your authentication!

## Usage Guide

### Main Interface

#### Credentials Section
- **Add credentials**: Enter username/password and click "Add"
- **Remove credentials**: Select a row and click "Remove Selected"
- **Status indicators**: 
  - **Active**: Currently logged in
  - **Trying...**: Login attempt in progress
  - **Failed**: Login failed
  - **Ready**: Available for use

#### Scheduler Settings
- **Interval Mode**: Set login frequency (e.g., every 30 minutes)
- **Specific Time Mode**: Set daily login time (e.g., 09:00)
- **Checkboxes**: Configure auto-start and startup behavior

#### Activity Log
- Real-time logging of all activities
- Connection test functionality
- Clear log button for maintenance

#### Control Buttons
- **Login Now**: Immediate login attempt
- **Logout**: Disconnect from WiFi network
- **Start/Stop Scheduler**: Control automatic scheduling
- **Test Connection**: Verify network connectivity

### System Tray Menu
Right-click the tray icon for quick access:
- **Show**: Restore main window
- **Login Now**: Quick login
- **Logout**: Quick logout
- **Exit**: Close application

## Troubleshooting

### Common Issues

#### "Login Failed" Error
1. Verify username and password are correct
2. Check if WiFi portal is accessible
3. Try manual login through browser first
4. Check activity log for detailed error messages

#### "Connection Failed" Error
1. Ensure you're connected to the WiFi network
2. Test internet connectivity using "Test Connection"
3. Verify the login URL is accessible
4. Check firewall/antivirus settings

#### Scheduler Not Working
1. Verify scheduler is started (button shows "Stop Scheduler")
2. Check if credentials are added and valid
3. Ensure interval/time settings are correct
4. Review activity log for scheduler events

#### Application Won't Start
1. Ensure Java is installed (bundled in installer)
2. Run as Administrator if needed
3. Check Windows Event Viewer for errors
4. Reinstall using the latest installer

### Advanced Troubleshooting

#### Enable Debug Logging
1. Open application
2. Monitor "Activity Log" section
3. Use "Test Connection" for network diagnostics
4. Check console output if running from command line

#### Reset Application Data
To completely reset the application:
```powershell
# Remove all saved data
Remove-Item -Recurse -Force "$env:USERPROFILE\.wifiauth"
```

## Project Structure

```
WiFiAutoLogger/
├── ImprovedWifiAuthenticator.java    # Main application source
├── README.md                         # This file
├── LICENSE                          # MIT License
├── build-installer.ps1             # Installer build script
├── .gitignore                       # Git ignore rules
└── docs/                           # Documentation
    ├── CONFIGURATION.md            # Advanced configuration
    ├── TROUBLESHOOTING.md          # Detailed troubleshooting
    └── DEVELOPMENT.md              # Development guide
```

## Security Features

### Encryption
- **AES-256 encryption** for stored passwords
- **User-specific encryption keys** based on system properties
- **No cloud storage** - all data remains local

### Network Security
- **HTTPS support** for secure portals
- **Timeout handling** to prevent hanging connections
- **User-Agent spoofing** for compatibility

### Privacy
- **No telemetry** or data collection
- **Local-only storage** of credentials
- **Open source** for transparency

## Contributing

### Development Setup
1. Install Java 17+ JDK
2. Clone the repository
3. Open in your favorite IDE
4. Make changes and test thoroughly
5. Submit a pull request

### Building from Source
```powershell
# Compile Java source
javac ImprovedWifiAuthenticator.java

# Create JAR file
jar cfe WiFiAutoLogger.jar ImprovedWifiAuthenticator *.class

# Run application
java -jar WiFiAutoLogger.jar
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

### Getting Help
- **Issues**: [GitHub Issues](https://github.com/life2harsh/wifi-autologger/issues)
- **Discussions**: [GitHub Discussions](https://github.com/life2harsh/wifi-autologger/discussions)

### Feature Requests
Have an idea for improvement? love to hear it!
1. Check existing [feature requests](https://github.com/life2harsh/wifi-autologger/labels/enhancement)
2. Create a new issue with the "enhancement" label
3. Describe your use case and proposed solution

---

peace out.
