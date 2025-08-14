import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.TrayIcon.MessageType;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

class ModernColors {
    public static final Color BACKGROUND = new Color(30, 30, 30);
    public static final Color PANEL_BG = new Color(40, 40, 40);
    public static final Color CARD_BG = new Color(50, 50, 50);
    public static final Color ACCENT = new Color(64, 128, 255);
    public static final Color ACCENT_HOVER = new Color(80, 144, 255);
    public static final Color TEXT_PRIMARY = new Color(220, 220, 220);
    public static final Color TEXT_SECONDARY = new Color(160, 160, 160);
    public static final Color SUCCESS = new Color(76, 175, 80);
    public static final Color ERROR = new Color(244, 67, 54);
    public static final Color BORDER = new Color(70, 70, 70);
}

class RoundedBorder extends AbstractBorder {
    private final int radius;
    private final Color color;

    public RoundedBorder(int radius, Color color) {
        this.radius = radius;
        this.color = color;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(8, 12, 8, 12);
    }
}

class ModernButton extends JButton {
    private Color backgroundColor = ModernColors.ACCENT;
    private Color hoverColor = ModernColors.ACCENT_HOVER;
    private boolean isHovered = false;

    public ModernButton(String text) {
        super(text);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFont(new Font("SansSerif", Font.PLAIN, 13));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(120, 32));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    public void setButtonColor(Color bg, Color hover) {
        this.backgroundColor = bg;
        this.hoverColor = hover;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bgColor = isHovered ? hoverColor : backgroundColor;
        if (!isEnabled()) {
            bgColor = ModernColors.BORDER;
        }

        g2.setColor(bgColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));

        g2.dispose();
        super.paintComponent(g);
    }
}

class ModernTextField extends JTextField {
    private String placeholder = "";

    public ModernTextField(int columns) {
        super(columns);
        setBackground(new Color(90, 90, 90));
        setForeground(Color.WHITE);
        setCaretColor(Color.YELLOW);
        setSelectionColor(new Color(100, 150, 255));
        setSelectedTextColor(Color.BLACK);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(120, 120, 120), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        setFont(new Font("SansSerif", Font.BOLD, 16));
        setOpaque(true);
        setEditable(true);
        setDisabledTextColor(new Color(180, 180, 180));
        setMinimumSize(new Dimension(120, 40));
        setPreferredSize(new Dimension(150, 40));
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isEnabled()) {
            setForeground(Color.WHITE);
            setBackground(new Color(90, 90, 90));
        } else {
            setForeground(new Color(180, 180, 180));
            setBackground(new Color(60, 60, 60));
        }

        if (getText().isEmpty() && !placeholder.isEmpty() && !hasFocus()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(160, 160, 160));
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            FontMetrics fm = g2.getFontMetrics();
            int x = getInsets().left;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(placeholder, x, y);
            g2.dispose();
        }
    }
}

class ModernPasswordField extends JPasswordField {
    private String placeholder = "";

    public ModernPasswordField(int columns) {
        super(columns);
        setBackground(new Color(90, 90, 90));
        setForeground(Color.WHITE);
        setCaretColor(Color.YELLOW);
        setSelectionColor(new Color(100, 150, 255));
        setSelectedTextColor(Color.BLACK);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(120, 120, 120), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        setFont(new Font("SansSerif", Font.BOLD, 16));
        setOpaque(true);
        setEditable(true);
        setMinimumSize(new Dimension(120, 40));
        setPreferredSize(new Dimension(150, 40));
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (getPassword().length == 0 && !placeholder.isEmpty() && !hasFocus()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(160, 160, 160));
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            FontMetrics fm = g2.getFontMetrics();
            int x = getInsets().left;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(placeholder, x, y);
            g2.dispose();
        }
    }
}

class ModernPanel extends JPanel {
    private final int radius;

    public ModernPanel(int radius) {
        this.radius = radius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
        g2.dispose();
        super.paintComponent(g);
    }
}

class ModernTable extends JTable {
    public ModernTable(DefaultTableModel model) {
        super(model);

        setBackground(ModernColors.CARD_BG);
        setForeground(ModernColors.TEXT_PRIMARY);
        setSelectionBackground(ModernColors.ACCENT);
        setSelectionForeground(Color.WHITE);
        setGridColor(new Color(60, 60, 60));
        setShowGrid(true);
        setIntercellSpacing(new Dimension(1, 1));
        setRowHeight(40);
        setFont(new Font("SansSerif", Font.PLAIN, 13));

        JTableHeader header = getTableHeader();
        header.setBackground(ModernColors.PANEL_BG);
        header.setForeground(ModernColors.TEXT_PRIMARY);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ModernColors.ACCENT));
        header.setPreferredSize(new Dimension(0, 45));
        header.setOpaque(true);

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(ModernColors.PANEL_BG);
                setForeground(ModernColors.TEXT_PRIMARY);
                setFont(new Font("SansSerif", Font.BOLD, 14));
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, ModernColors.ACCENT),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
                setHorizontalAlignment(SwingConstants.LEFT);
                setOpaque(true);
                return this;
            }
        });

        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    setBackground(ModernColors.ACCENT);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(ModernColors.CARD_BG);
                    setForeground(ModernColors.TEXT_PRIMARY);
                }

                setBorder(new EmptyBorder(8, 12, 8, 12));

                if (column == 2 && value != null) {
                    String status = value.toString();
                    switch (status) {
                        case "Active":
                            if (!isSelected) {
                                setForeground(ModernColors.SUCCESS);
                            }
                            break;
                        case "Failed":
                            if (!isSelected) {
                                setForeground(ModernColors.ERROR);
                            }
                            break;
                        case "Trying...":
                            if (!isSelected) {
                                setForeground(ModernColors.ACCENT);
                            }
                            break;
                    }
                }

                return this;
            }
        });
    }
}

class ModernTextArea extends JTextArea {
    public ModernTextArea() {
        setBackground(ModernColors.CARD_BG);
        setForeground(ModernColors.TEXT_PRIMARY);
        setCaretColor(ModernColors.ACCENT);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setFont(new Font("Monospaced", Font.PLAIN, 12));
        setLineWrap(true);
        setWrapStyleWord(true);
    }
}

class CredentialEncryption {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    private static SecretKeySpec getSecretKey() {
        try {
            String keySource = System.getProperty("user.name") + System.getProperty("os.name") + "WiFiAuth2024";
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] key = sha.digest(keySource.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(Arrays.copyOf(key, 16), ALGORITHM);
        } catch (Exception e) {
            byte[] fallbackKey = "WiFiAuthKey12345".getBytes(StandardCharsets.UTF_8);
            return new SecretKeySpec(Arrays.copyOf(fallbackKey, 16), ALGORITHM);
        }
    }

    public static String encrypt(String plainText) {
        try {
            SecretKeySpec secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            System.err.println("Encryption failed: " + e.getMessage());
            return plainText;
        }
    }

    public static String decrypt(String encryptedText) {
        try {
            SecretKeySpec secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Decryption failed: " + e.getMessage());
            return encryptedText;
        }
    }
}

class Credential implements Serializable {
    private static final long serialVersionUID = 1L;
    public String username;
    String encryptedPassword;

    public Credential(String username, String password) {
        this.username = username;
        this.encryptedPassword = CredentialEncryption.encrypt(password);
    }

    public String getPassword() {
        return CredentialEncryption.decrypt(encryptedPassword);
    }

    public void setPassword(String password) {
        this.encryptedPassword = CredentialEncryption.encrypt(password);
    }
}

class CredentialManager {
    private static final String APP_DIR = System.getProperty("user.home") + File.separator + ".wifiauth";
    private static final String CREDENTIALS_FILE = APP_DIR + File.separator + "credentials.enc";
    private static final String SETTINGS_FILE = APP_DIR + File.separator + "settings.conf";

    static {
        File dir = new File(APP_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void saveCredentials(List<Credential> credentials) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CREDENTIALS_FILE))) {
            List<String[]> encryptedData = new ArrayList<>();
            for (Credential cred : credentials) {
                encryptedData.add(new String[]{cred.username, cred.encryptedPassword});
            }
            oos.writeObject(encryptedData);
            System.out.println("Credentials saved to: " + CREDENTIALS_FILE);
        } catch (Exception e) {
            System.err.println("Error saving credentials: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Credential> loadCredentials() {
        List<Credential> credentials = new ArrayList<>();
        File file = new File(CREDENTIALS_FILE);

        if (!file.exists()) {
            System.out.println("No saved credentials found. Starting fresh.");
            return credentials;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CREDENTIALS_FILE))) {
            List<String[]> encryptedData = (List<String[]>) ois.readObject();

            for (String[] data : encryptedData) {
                String username = data[0];
                String encryptedPassword = data[1];

                Credential cred = new Credential("", "");
                cred.username = username;
                cred.encryptedPassword = encryptedPassword;
                credentials.add(cred);
            }

            System.out.println("Loaded " + credentials.size() + " saved credentials from: " + CREDENTIALS_FILE);
        } catch (Exception e) {
            System.err.println("Error loading credentials: " + e.getMessage());
            return new ArrayList<>();
        }

        return credentials;
    }

    public static void saveSettings(boolean autoStart, boolean autoLogin, boolean startOnBoot, 
                                   boolean minimizeToTray, boolean isInterval, String intervalValue, 
                                   String timeUnit, String specificTime) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SETTINGS_FILE))) {
            writer.println("# WiFi Authenticator Settings");
            writer.println("# Auto-generated configuration file");
            writer.println("autoStart=" + autoStart);
            writer.println("autoLogin=" + autoLogin);
            writer.println("startOnBoot=" + startOnBoot);
            writer.println("minimizeToTray=" + minimizeToTray);
            writer.println("isInterval=" + isInterval);
            writer.println("intervalValue=" + intervalValue);
            writer.println("timeUnit=" + timeUnit);
            writer.println("specificTime=" + specificTime);
            writer.println("# Last saved: " + java.time.LocalDateTime.now());
            System.out.println("Settings saved to: " + SETTINGS_FILE);
        } catch (Exception e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
    }

    public static java.util.Map<String, String> loadSettings() {
        java.util.Map<String, String> settings = new java.util.HashMap<>();
        File file = new File(SETTINGS_FILE);

        if (!file.exists()) {
            System.out.println("No saved settings found. Using defaults.");
            return settings;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(SETTINGS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    settings.put(parts[0].trim(), parts[1].trim());
                }
            }
            System.out.println("Settings loaded from: " + SETTINGS_FILE);
        } catch (Exception e) {
            System.err.println("Error loading settings: " + e.getMessage());
        }

        return settings;
    }

    public static void deleteAllData() {
        try {
            File credFile = new File(CREDENTIALS_FILE);
            File settingsFile = new File(SETTINGS_FILE);
            File appDir = new File(APP_DIR);

            if (credFile.exists()) credFile.delete();
            if (settingsFile.exists()) settingsFile.delete();
            if (appDir.exists()) appDir.delete();

            System.out.println("All application data deleted.");
        } catch (Exception e) {
            System.err.println("Error deleting application data: " + e.getMessage());
        }
    }
}

class StartupUtils {
    private static final String APP_NAME = "WiFiAutoLogger";

    public static void setStartupEnabled(boolean enabled) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                setWindowsStartup(enabled);
            } else if (os.contains("mac")) {
                setMacStartup(enabled);
            } else {
                setLinuxStartup(enabled);
            }
        } catch (Exception e) {
            System.err.println("Error setting startup: " + e.getMessage());
        }
    }

    private static void setWindowsStartup(boolean enabled) {
        try {
            String jarPath = System.getProperty("java.class.path");
            String javaHome = System.getProperty("java.home");
            String javaBin = javaHome + File.separator + "bin" + File.separator + "java.exe";

            String command = "\"" + javaBin + "\" -jar \"" + jarPath + "\"";

            if (enabled) {
                String regAdd = "reg add \"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run\" /v " + APP_NAME + " /t REG_SZ /d \"" + command + "\" /f";
                Process process = Runtime.getRuntime().exec(regAdd);
                process.waitFor();
            } else {
                String regDelete = "reg delete \"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run\" /v " + APP_NAME + " /f";
                Process process = Runtime.getRuntime().exec(regDelete);
                process.waitFor();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure Windows startup", e);
        }
    }

    private static void setMacStartup(boolean enabled) {
        System.out.println("Mac startup configuration not implemented");
    }

    private static void setLinuxStartup(boolean enabled) {
        System.out.println("Linux startup configuration not implemented");
    }
}

class NetworkUtils {
    public static boolean isInternetAvailable() {
        return isWiFiPortalAvailable();
    }
    
    public static boolean isWiFiPortalAvailable() {
        try {
            URL url = new URL("http://172.16.68.6:8090/login.xml");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            connection.disconnect();
            return (responseCode > 0);
        } catch (Exception e) {
            return false;
        }
    }
    
    public static String getNetworkStatus() {
        boolean portalAvailable = isWiFiPortalAvailable();
        
        if (portalAvailable) {
            return "Connected to WiFi - Authentication Required";
        } else {
            return "No Network Connection";
        }
    }
}
public class ImprovedWifiAuthenticator extends JFrame {

    private static final String LOGIN_URL = "http://172.16.68.6:8090/login.xml";
    private static final String LOGOUT_URL = "http://172.16.68.6:8090/logout.xml";
    private static final String LOGIN_MODE = "191";
    private static final String LOGOUT_MODE = "193";
    private static final String PRODUCTTYPE = "0";

    private ModernTextField usernameField;
    private ModernPasswordField passwordField;
    private JComboBox<String> timeUnitCombo;
    private ModernTextField intervalField;
    private ModernTextField specificTimeField;
    private JRadioButton intervalRadio, specificTimeRadio;
    private ModernButton addCredentialButton;
    private ModernButton removeCredentialButton;
    private ModernButton loginButton;
    private ModernButton logoutButton;
    private ModernButton scheduleButton;
    private ModernButton stopButton;
    private ModernTextArea logArea;
    private JCheckBox autoStartCheckBox;
    private JCheckBox autoLoginOnStartCheckBox;
    private JCheckBox startOnBootCheckBox;
    private ModernTable credentialsTable;
    private DefaultTableModel tableModel;
    private JCheckBox minimizeToTrayCheckBox;
    private JLabel statusLabel;
    private JLabel connectionStatusLabel;

    private ScheduledExecutorService scheduler;
    private boolean isScheduled = false;
    private TrayIcon trayIcon;
    private SystemTray tray;
    private List<Credential> actualCredentials = new ArrayList<>();
    private long lastScheduledRun = 0;
    private volatile boolean isLoginInProgress = false;
    
    public ImprovedWifiAuthenticator() {
        setupCrispRendering();
        initializeModernGUI();
        setupSystemTray();

        loadSavedData();

        if (autoStartCheckBox.isSelected()) {
            SwingUtilities.invokeLater(() -> startScheduler());
        }

        if (autoLoginOnStartCheckBox.isSelected()) {
            SwingUtilities.invokeLater(() -> {
                logMessage("Auto-login on start enabled - performing login...");
                performLogin();
            });
        }
    }

    private void setupCrispRendering() {
        System.setProperty("awt.useSystemAAFontSettings", "gasp");
        System.setProperty("swing.aatext", "true");
        System.setProperty("swing.plaf.metal.controlFont", "SansSerif-PLAIN-13");
        System.setProperty("swing.plaf.metal.userFont", "SansSerif-PLAIN-13");
    }

    private void initializeModernGUI() {
        setTitle("WiFi Autologger");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(950, 700);
        setLocationRelativeTo(null);

        try {
            setUndecorated(false);
            getRootPane().setWindowDecorationStyle(JRootPane.FRAME);

            if (System.getProperty("java.version").startsWith("17") || 
                System.getProperty("java.version").startsWith("18") || 
                System.getProperty("java.version").startsWith("19") ||
                System.getProperty("java.version").startsWith("20") ||
                System.getProperty("java.version").startsWith("21")) {

                getRootPane().putClientProperty("apple.awt.windowTitleVisible", false);
                getRootPane().putClientProperty("apple.awt.fullWindowContent", true);
                getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);

                getRootPane().putClientProperty("JRootPane.titleBarBackground", ModernColors.BACKGROUND);
                getRootPane().putClientProperty("JRootPane.titleBarForeground", ModernColors.TEXT_PRIMARY);
            }
        } catch (Exception e) {

        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (minimizeToTrayCheckBox.isSelected() && SystemTray.isSupported()) {
                    setVisible(false);
                    displayNotification("WiFi AutoLogger minimized to system tray", MessageType.INFO);
                } else {
                    exitApplication();
                }
            }
        });

        getContentPane().setBackground(ModernColors.BACKGROUND);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(ModernColors.BACKGROUND);
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel header = createHeader();
        mainContainer.add(header, BorderLayout.NORTH);

        JPanel contentArea = createImprovedContentArea();
        mainContainer.add(contentArea, BorderLayout.CENTER);

        JPanel footer = createFooter();
        mainContainer.add(footer, BorderLayout.SOUTH);

        add(mainContainer);

        logMessage("Application started successfully");
        updateConnectionStatus();

        startConnectionMonitoring();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ModernColors.BACKGROUND);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("WiFi Autologger");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(ModernColors.TEXT_PRIMARY);


        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(ModernColors.BACKGROUND);
        titlePanel.add(title, BorderLayout.NORTH);

        connectionStatusLabel = new JLabel("⚫ Checking...");
        connectionStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        connectionStatusLabel.setForeground(ModernColors.TEXT_SECONDARY);

        header.add(titlePanel, BorderLayout.WEST);

        return header;
    }

    private JPanel createImprovedContentArea() {
        JPanel content = new JPanel(new BorderLayout(20, 0));
        content.setBackground(ModernColors.BACKGROUND);

        JPanel leftSide = new JPanel(new BorderLayout(0, 20));
        leftSide.setBackground(ModernColors.BACKGROUND);
        leftSide.setPreferredSize(new Dimension(450, 0));

        ModernPanel credentialsCard = createExtendedCredentialsCard();
        leftSide.add(credentialsCard, BorderLayout.CENTER);

        ModernPanel settingsCard = createEnhancedSettingsCard();
        leftSide.add(settingsCard, BorderLayout.SOUTH);

        content.add(leftSide, BorderLayout.WEST);

        JPanel rightSide = new JPanel(new BorderLayout(0, 20));
        rightSide.setBackground(ModernColors.BACKGROUND);

        ModernPanel logCard = createExpandedLogCard();
        rightSide.add(logCard, BorderLayout.CENTER);

        ModernPanel statusCard = createCompactStatusCard();
        rightSide.add(statusCard, BorderLayout.SOUTH);

        content.add(rightSide, BorderLayout.CENTER);

        return content;
    }

    private ModernPanel createExtendedCredentialsCard() {
        ModernPanel card = new ModernPanel(12);
        card.setBackground(ModernColors.PANEL_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel cardTitle = new JLabel("Enter Credentials");
        cardTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        cardTitle.setForeground(ModernColors.TEXT_PRIMARY);

        JPanel inputArea = new JPanel(new GridBagLayout());
        inputArea.setBackground(ModernColors.PANEL_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4;
        usernameField = new ModernTextField(15);
        usernameField.setPlaceholder("Enter username");
        usernameField.setToolTipText("Enter your WiFi username");
        inputArea.add(usernameField, gbc);

        gbc.gridx = 1; gbc.weightx = 0.4;
        passwordField = new ModernPasswordField(15);
        passwordField.setPlaceholder("Enter password");
        passwordField.setToolTipText("Enter your WiFi password");
        inputArea.add(passwordField, gbc);

        gbc.gridx = 2; gbc.weightx = 0.2;
        addCredentialButton = new ModernButton("Add");
        addCredentialButton.addActionListener(e -> addCredential());
        inputArea.add(addCredentialButton, gbc);

        String[] columns = {"Username", "Password", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        credentialsTable = new ModernTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(credentialsTable);
        scrollPane.setBackground(ModernColors.CARD_BG);
        scrollPane.setBorder(new RoundedBorder(8, ModernColors.BORDER));
        scrollPane.getViewport().setBackground(ModernColors.CARD_BG);
        scrollPane.setPreferredSize(new Dimension(0, 280));

        removeCredentialButton = new ModernButton("Remove Selected");
        removeCredentialButton.setButtonColor(ModernColors.ERROR, new Color(220, 67, 54));
        removeCredentialButton.addActionListener(e -> removeCredential());

        card.add(cardTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
        centerPanel.setBackground(ModernColors.PANEL_BG);
        centerPanel.add(inputArea, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(removeCredentialButton, BorderLayout.SOUTH);

        card.add(centerPanel, BorderLayout.CENTER);

        return card;
    }

    private ModernPanel createEnhancedSettingsCard() {
        ModernPanel card = new ModernPanel(12);
        card.setBackground(ModernColors.PANEL_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        card.setPreferredSize(new Dimension(0, 220));

        JLabel cardTitle = new JLabel("Scheduler Settings");
        cardTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        cardTitle.setForeground(ModernColors.TEXT_PRIMARY);

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBackground(ModernColors.PANEL_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        ButtonGroup schedulerGroup = new ButtonGroup();
        intervalRadio = new JRadioButton("Interval:", true);
        specificTimeRadio = new JRadioButton("Specific time:");

        intervalRadio.setBackground(ModernColors.PANEL_BG);
        intervalRadio.setForeground(ModernColors.TEXT_PRIMARY);
        specificTimeRadio.setBackground(ModernColors.PANEL_BG);
        specificTimeRadio.setForeground(ModernColors.TEXT_PRIMARY);

        schedulerGroup.add(intervalRadio);
        schedulerGroup.add(specificTimeRadio);

        intervalField = new ModernTextField(15);
        intervalField.setText("30");
        intervalField.setPreferredSize(new Dimension(120, 40));

        timeUnitCombo = new JComboBox<>(new String[]{"Seconds", "Minutes", "Hours"});
        timeUnitCombo.setSelectedIndex(1);
        timeUnitCombo.setBackground(ModernColors.CARD_BG);
        timeUnitCombo.setForeground(ModernColors.TEXT_PRIMARY);
        timeUnitCombo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        timeUnitCombo.setBorder(new RoundedBorder(6, ModernColors.BORDER));
        timeUnitCombo.setFocusable(false);
        timeUnitCombo.setOpaque(true);

        timeUnitCombo.setUI(new BasicComboBoxUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                comboBox.setFocusable(false);
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {

            }
        });

        timeUnitCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? ModernColors.ACCENT : ModernColors.CARD_BG);
                setForeground(isSelected ? Color.WHITE : ModernColors.TEXT_PRIMARY);
                setBorder(new EmptyBorder(5, 10, 5, 10));
                setOpaque(true);
                return this;
            }
        });

        specificTimeField = new ModernTextField(20);
        specificTimeField.setText("09:00");
        specificTimeField.setToolTipText("Format: HH:MM or HH:MM:SS");
        specificTimeField.setEnabled(false);
        specificTimeField.setPreferredSize(new Dimension(150, 40));

        updateFieldStates();

        intervalRadio.addActionListener(e -> updateFieldStates());
        specificTimeRadio.addActionListener(e -> updateFieldStates());

        autoStartCheckBox = new JCheckBox("Auto-start scheduler");
        autoStartCheckBox.setBackground(ModernColors.PANEL_BG);
        autoStartCheckBox.setForeground(ModernColors.TEXT_PRIMARY);

        autoLoginOnStartCheckBox = new JCheckBox("Auto-login on start");
        autoLoginOnStartCheckBox.setBackground(ModernColors.PANEL_BG);
        autoLoginOnStartCheckBox.setForeground(ModernColors.TEXT_PRIMARY);
        autoLoginOnStartCheckBox.setSelected(true);

        startOnBootCheckBox = new JCheckBox("Start on system boot");
        startOnBootCheckBox.setBackground(ModernColors.PANEL_BG);
        startOnBootCheckBox.setForeground(ModernColors.TEXT_PRIMARY);

        minimizeToTrayCheckBox = new JCheckBox("Minimize to tray");
        minimizeToTrayCheckBox.setBackground(ModernColors.PANEL_BG);
        minimizeToTrayCheckBox.setForeground(ModernColors.TEXT_PRIMARY);
        minimizeToTrayCheckBox.setSelected(true);

        gbc.gridx = 0; gbc.gridy = 0;
        settingsPanel.add(intervalRadio, gbc);
        gbc.gridx = 1;
        settingsPanel.add(intervalField, gbc);
        gbc.gridx = 2;
        settingsPanel.add(timeUnitCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        settingsPanel.add(specificTimeRadio, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        settingsPanel.add(specificTimeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        settingsPanel.add(autoStartCheckBox, gbc);
        gbc.gridx = 1;
        settingsPanel.add(autoLoginOnStartCheckBox, gbc);
        gbc.gridx = 2;
        settingsPanel.add(minimizeToTrayCheckBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        startOnBootCheckBox.addActionListener(e -> {
            try {
                StartupUtils.setStartupEnabled(startOnBootCheckBox.isSelected());
                if (startOnBootCheckBox.isSelected()) {
                    logMessage("Application set to start on system boot");
                    displayNotification("Application will start on system boot", MessageType.INFO);
                } else {
                    logMessage("Application removed from system startup");
                    displayNotification("Application removed from system startup", MessageType.INFO);
                }
            } catch (Exception ex) {
                logMessage("Error configuring startup: " + ex.getMessage());
                startOnBootCheckBox.setSelected(false);
            }
        });
        settingsPanel.add(startOnBootCheckBox, gbc);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(settingsPanel, BorderLayout.CENTER);

        return card;
    }

    private void updateFieldStates() {
        boolean intervalSelected = intervalRadio.isSelected();

        intervalField.setEnabled(intervalSelected);
        timeUnitCombo.setEnabled(intervalSelected);
        specificTimeField.setEnabled(!intervalSelected);

        if (intervalSelected) {
            intervalField.setBackground(new Color(90, 90, 90));
            intervalField.setForeground(Color.WHITE);
            intervalField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernColors.ACCENT, 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));

            specificTimeField.setBackground(new Color(60, 60, 60));
            specificTimeField.setForeground(new Color(180, 180, 180));
            specificTimeField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
        } else {
            intervalField.setBackground(new Color(60, 60, 60));
            intervalField.setForeground(new Color(180, 180, 180));
            intervalField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));

            specificTimeField.setBackground(new Color(90, 90, 90));
            specificTimeField.setForeground(Color.WHITE);
            specificTimeField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernColors.ACCENT, 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
        }
    }

    private ModernPanel createExpandedLogCard() {
        ModernPanel card = new ModernPanel(12);
        card.setBackground(ModernColors.PANEL_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel cardTitle = new JLabel("Activity Log");
        cardTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        cardTitle.setForeground(ModernColors.TEXT_PRIMARY);

        logArea = new ModernTextArea();
        logArea.setEditable(false);

        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBackground(ModernColors.CARD_BG);
        logScrollPane.setBorder(new RoundedBorder(8, ModernColors.BORDER));
        logScrollPane.getViewport().setBackground(ModernColors.CARD_BG);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setBackground(ModernColors.PANEL_BG);

        ModernButton clearButton = new ModernButton("Clear Log");
        clearButton.setButtonColor(ModernColors.BORDER, new Color(90, 90, 90));
        clearButton.addActionListener(e -> {
            logArea.setText("");
            logMessage("Log cleared");
        });

        ModernButton testConnectionButton = new ModernButton("Test Connection");
        testConnectionButton.setButtonColor(ModernColors.ACCENT, ModernColors.ACCENT_HOVER);
        testConnectionButton.addActionListener(e -> testNetworkConnection());

        buttonPanel.add(clearButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(testConnectionButton);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(logScrollPane, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    private ModernPanel createCompactStatusCard() {
        ModernPanel card = new ModernPanel(12);
        card.setBackground(ModernColors.PANEL_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(10, 20, 10, 20));
        card.setPreferredSize(new Dimension(0, 60));

        JLabel cardTitle = new JLabel("Status:");
        cardTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        cardTitle.setForeground(ModernColors.TEXT_SECONDARY);

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusLabel.setForeground(ModernColors.SUCCESS);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        statusPanel.setBackground(ModernColors.PANEL_BG);
        statusPanel.add(cardTitle);
        statusPanel.add(statusLabel);

        card.add(statusPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        footer.setBackground(ModernColors.BACKGROUND);
        footer.setBorder(new EmptyBorder(20, 0, 0, 0));

        loginButton = new ModernButton("Login Now");
        loginButton.setPreferredSize(new Dimension(120, 36));
        loginButton.addActionListener(e -> performLogin());

        logoutButton = new ModernButton("Logout");
        logoutButton.setPreferredSize(new Dimension(100, 36));
        logoutButton.setButtonColor(new Color(255, 140, 0), new Color(255, 160, 20));
        logoutButton.addActionListener(e -> performLogout());

        scheduleButton = new ModernButton("Start Scheduler");
        scheduleButton.setPreferredSize(new Dimension(120, 36));
        scheduleButton.setButtonColor(ModernColors.SUCCESS, new Color(96, 195, 100));
        scheduleButton.addActionListener(e -> toggleScheduler());

        stopButton = new ModernButton("Stop");
        stopButton.setPreferredSize(new Dimension(80, 36));
        stopButton.setButtonColor(ModernColors.ERROR, new Color(220, 67, 54));
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopScheduler());

        footer.add(loginButton);
        footer.add(logoutButton);
        footer.add(scheduleButton);
        footer.add(stopButton);

        return footer;
    }

    private void setupSystemTray() {
        if (!SystemTray.isSupported()) {
            return;
        }

        try {
            tray = SystemTray.getSystemTray();

            BufferedImage iconImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = iconImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(ModernColors.ACCENT);
            g2d.fillOval(2, 2, 12, 12);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 8));
            g2d.drawString("W", 5, 11);
            g2d.dispose();

            trayIcon = new TrayIcon(iconImage, "WiFi Autologger");
            trayIcon.setImageAutoSize(true);

            PopupMenu popup = new PopupMenu();

            MenuItem showItem = new MenuItem("Show");
            showItem.addActionListener(e -> {
                setVisible(true);
                setExtendedState(JFrame.NORMAL);
                toFront();
            });
            popup.add(showItem);

            MenuItem loginItem = new MenuItem("Login Now");
            loginItem.addActionListener(e -> performLogin());
            popup.add(loginItem);

            MenuItem logoutItem = new MenuItem("Logout");
            logoutItem.addActionListener(e -> performLogout());
            popup.add(logoutItem);

            popup.addSeparator();

            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(e -> exitApplication());
            popup.add(exitItem);

            trayIcon.setPopupMenu(popup);

            trayIcon.addActionListener(e -> {
                setVisible(true);
                setExtendedState(JFrame.NORMAL);
                toFront();
            });

        } catch (Exception e) {
            logMessage("Error setting up system tray: " + e.getMessage());
        }
    }

    private void startConnectionMonitoring() {
        ScheduledExecutorService connectionMonitor = Executors.newScheduledThreadPool(1);
        connectionMonitor.scheduleAtFixedRate(this::updateConnectionStatus, 0, 30, TimeUnit.SECONDS);
    }

    private void updateConnectionStatus() {
    SwingUtilities.invokeLater(() -> {
        String networkStatus = NetworkUtils.getNetworkStatus();
        
        if (networkStatus.contains("Connected to Internet")) {
            connectionStatusLabel.setText("🟢 Online");
            connectionStatusLabel.setForeground(ModernColors.SUCCESS);
        } else if (networkStatus.contains("Authentication Required")) {
            connectionStatusLabel.setText("🟡 WiFi Connected - Auth Required");
            connectionStatusLabel.setForeground(new Color(255, 193, 7));
        } else {
            connectionStatusLabel.setText("🔴 No Network");
            connectionStatusLabel.setForeground(ModernColors.ERROR);
        }
    });
}

    private void testNetworkConnection() {
        new Thread(() -> {
            logMessage("Testing network connection...");
            updateStatus("Testing connection...", ModernColors.ACCENT);

            boolean connected = NetworkUtils.isInternetAvailable();
            if (connected) {
                logMessage("✓ Network connection successful");
                updateStatus("Connected", ModernColors.SUCCESS);
                displayNotification("Network connection test successful", MessageType.INFO);
            } else {
                logMessage("✗ Network connection failed");
                updateStatus("Connection failed", ModernColors.ERROR);
                displayNotification("Network connection test failed", MessageType.ERROR);
            }
        }).start();
    }

    private void loadSavedData() {
        actualCredentials = CredentialManager.loadCredentials();

        for (Credential cred : actualCredentials) {
            tableModel.addRow(new String[]{cred.username, "••••••••••", "Ready"});
        }

        if (actualCredentials.isEmpty()) {
            logMessage("No saved credentials found. Please add credentials to get started.");
        } else {
            logMessage("Loaded " + actualCredentials.size() + " saved credential(s)");
        }

        java.util.Map<String, String> settings = CredentialManager.loadSettings();

        autoStartCheckBox.setSelected(Boolean.parseBoolean(settings.getOrDefault("autoStart", "false")));
        autoLoginOnStartCheckBox.setSelected(Boolean.parseBoolean(settings.getOrDefault("autoLogin", "true")));
        startOnBootCheckBox.setSelected(Boolean.parseBoolean(settings.getOrDefault("startOnBoot", "false")));
        minimizeToTrayCheckBox.setSelected(Boolean.parseBoolean(settings.getOrDefault("minimizeToTray", "true")));

        boolean isInterval = Boolean.parseBoolean(settings.getOrDefault("isInterval", "true"));
        if (isInterval) {
            intervalRadio.setSelected(true);
        } else {
            specificTimeRadio.setSelected(true);
        }

        intervalField.setText(settings.getOrDefault("intervalValue", "30"));
        String timeUnit = settings.getOrDefault("timeUnit", "Minutes");
        for (int i = 0; i < timeUnitCombo.getItemCount(); i++) {
            if (timeUnitCombo.getItemAt(i).equals(timeUnit)) {
                timeUnitCombo.setSelectedIndex(i);
                break;
            }
        }
        specificTimeField.setText(settings.getOrDefault("specificTime", "09:00"));

        updateFieldStates();
        logMessage("Settings loaded and applied");
    }

    private void addCredential() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (Credential cred : actualCredentials) {
            if (cred.username.equals(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        actualCredentials.add(new Credential(username, password));
        tableModel.addRow(new String[]{username, "••••••••••", "Ready"});

        usernameField.setText("");
        passwordField.setText("");

        CredentialManager.saveCredentials(actualCredentials);

        logMessage("Added and saved credential: " + username + " (encrypted and persisted to disk)");
        updateStatus("Credential added", ModernColors.SUCCESS);
    }

    private void removeCredential() {
        int selectedRow = credentialsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a credential to remove", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = (String) tableModel.getValueAt(selectedRow, 0);

        actualCredentials.removeIf(cred -> cred.username.equals(username));
        tableModel.removeRow(selectedRow);

        CredentialManager.saveCredentials(actualCredentials);

        logMessage("Removed and saved changes for credential: " + username + " (persisted to disk)");
        updateStatus("Credential removed", ModernColors.ERROR);
    }

    private String currentLoggedInUsername = "";

private void performLogin() {
    if (actualCredentials.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No credentials available", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (isLoginInProgress) {
        logMessage("Login already in progress, skipping...");
        return;
    }

    SwingUtilities.invokeLater(() -> {
        loginButton.setEnabled(false);
        updateStatus("Logging in...", ModernColors.ACCENT);
        logMessage("Starting login attempts...");
        isLoginInProgress = true;

        new Thread(() -> {
            boolean loggedIn = false;

            for (int i = 0; i < actualCredentials.size(); i++) {
                Credential cred = actualCredentials.get(i);
                final int credIndex = i;

                SwingUtilities.invokeLater(() -> {
                    tableModel.setValueAt("Trying...", credIndex, 2);
                });

                logMessage("Attempting login with username: " + cred.username);
                boolean success = attemptLogin(cred.username, cred.getPassword());

                if (success) {
                    currentLoggedInUsername = cred.username; 
                    logMessage("✓ Successfully logged in with username: " + cred.username);
                    displayNotification("Successfully logged in with: " + cred.username, MessageType.INFO);
                    SwingUtilities.invokeLater(() -> {
                        updateStatus("Connected", ModernColors.SUCCESS);
                        tableModel.setValueAt("Active", credIndex, 2);
                        for (int j = 0; j < actualCredentials.size(); j++) {
                            if (j != credIndex) {
                                tableModel.setValueAt("Ready", j, 2);
                            }
                        }
                    });
                    loggedIn = true;
                    break;
                } else {
                    logMessage("✗ Login failed for username: " + cred.username);
                    SwingUtilities.invokeLater(() -> {
                        tableModel.setValueAt("Failed", credIndex, 2);
                    });
                }
            }

            if (!loggedIn) {
                currentLoggedInUsername = ""; 
                logMessage("All credentials exhausted. Could not log in.");
                displayNotification("All credentials failed", MessageType.ERROR);
                SwingUtilities.invokeLater(() -> updateStatus("Login failed", ModernColors.ERROR));
            }

            SwingUtilities.invokeLater(() -> {
                loginButton.setEnabled(true);
                isLoginInProgress = false;
            });
        }).start();
    });
}

private boolean attemptLogout() {
    HttpURLConnection connection = null;
    try {
        logMessage("Attempting logout for user: " + currentLoggedInUsername);

        if (!NetworkUtils.isWiFiPortalAvailable()) {
    logMessage("No WiFi portal connection available - ensure you're connected to WiFi network");
    return false;
}

        URI uri = new URI(LOGOUT_URL);
        URL url = uri.toURL();
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("User-Agent", "WiFi-Authenticator/2.0");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        String currentMillis = String.valueOf(System.currentTimeMillis());

        StringBuilder urlParameters = new StringBuilder();
        urlParameters.append("mode=").append(LOGOUT_MODE)
                     .append("&username=").append(currentLoggedInUsername) 
                     .append("&a=").append(currentMillis)
                     .append("&producttype=").append(PRODUCTTYPE);

        logMessage("Logout URL: " + LOGOUT_URL);
        logMessage("Logout Parameters: " + urlParameters.toString());

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = urlParameters.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        logMessage("Logout Response Code: " + responseCode);

        if (responseCode == 200) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                StringBuilder response = new StringBuilder();
                boolean logoutSuccess = false;

                while ((line = reader.readLine()) != null) {
                    response.append(line).append("\n");

                    String lowerLine = line.toLowerCase();
                    if (lowerLine.contains("you are signed out") || 
                        lowerLine.contains("logout successful") ||
                        lowerLine.contains("logged out") ||
                        lowerLine.contains("signed out") ||
                        lowerLine.contains("success") ||
                        lowerLine.contains("bye") ||
                        lowerLine.contains("goodbye")) {
                        logoutSuccess = true;
                        logMessage("✓ Logout success detected: " + line.trim());
                    }

                    if (lowerLine.contains("not logged in") || 
                        lowerLine.contains("already logged out")) {
                        logMessage("✓ Already logged out: " + line.trim());
                        logoutSuccess = true;
                    }
                }

                String responseStr = response.toString();
                if (responseStr.length() > 300) {
                    logMessage("Logout response preview: " + responseStr.substring(0, 300) + "...");
                } else {
                    logMessage("Full logout response: " + responseStr);
                }

                if (logoutSuccess) {
                    currentLoggedInUsername = ""; 
                    return true;
                }

                if (!responseStr.toLowerCase().contains("error") && 
                    !responseStr.toLowerCase().contains("failed")) {
                    logMessage("✓ No errors detected - assuming logout successful");
                    currentLoggedInUsername = ""; 
                    return true;
                }

                return false;
            }
        } else if (responseCode == 302 || responseCode == 301) {
            logMessage("✓ Logout redirect response - successful");
            currentLoggedInUsername = ""; 
            return true;
        } else {
            logMessage("HTTP Error " + responseCode + " during logout");
            return false;
        }
    } catch (java.net.SocketTimeoutException e) {
        logMessage("✓ Logout timed out - this is often successful for WiFi systems");
        currentLoggedInUsername = ""; 
        return true;
    } catch (Exception e) {
        logMessage("Logout error: " + e.getMessage());
        return false;
    } finally {
        if (connection != null) {
            connection.disconnect();
        }
    }
}
    private void performLogout() {
        SwingUtilities.invokeLater(() -> {
            logoutButton.setEnabled(false);
            loginButton.setEnabled(false);
            updateStatus("Logging out...", ModernColors.ACCENT);
            logMessage("Attempting to logout from WiFi network...");

            new Thread(() -> {
                boolean success = attemptLogout();

                SwingUtilities.invokeLater(() -> {
                    if (success) {
                        logMessage("✓ Successfully logged out from WiFi network");
                        updateStatus("Logged out", ModernColors.SUCCESS);
                        displayNotification("Successfully logged out from WiFi", MessageType.INFO);

                        for (int i = 0; i < tableModel.getRowCount(); i++) {
                            tableModel.setValueAt("Ready", i, 2);
                        }
                    } else {
                        logMessage("✗ Logout failed or already logged out");
                        updateStatus("Logout failed", ModernColors.ERROR);
                        displayNotification("Logout failed", MessageType.WARNING);
                    }

                    logoutButton.setEnabled(true);
                    loginButton.setEnabled(true);
                });
            }).start();
        });
    }

    private void toggleScheduler() {
        if (!isScheduled) {
            startScheduler();
        } else {
            stopScheduler();
        }
    }

    private void startScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }

        scheduler = Executors.newScheduledThreadPool(1);

        try {
            if (intervalRadio.isSelected()) {
                int interval = Integer.parseInt(intervalField.getText());
                if (interval <= 0) {
                    JOptionPane.showMessageDialog(this, "Interval must be greater than 0", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String unit = (String) timeUnitCombo.getSelectedItem();

                TimeUnit timeUnit;
                switch (unit) {
                    case "Minutes": timeUnit = TimeUnit.MINUTES; break;
                    case "Hours": timeUnit = TimeUnit.HOURS; break;
                    default: timeUnit = TimeUnit.SECONDS; break;
                }

                scheduler.scheduleAtFixedRate(() -> {
                    if (!isLoginInProgress) {
                        SwingUtilities.invokeLater(this::performLogin);
                    } else {
                        logMessage("Skipping scheduled login - login already in progress");
                    }
                }, interval, interval, timeUnit);

                logMessage("Scheduler started - every " + interval + " " + unit.toLowerCase());
                displayNotification("Scheduler started - every " + interval + " " + unit.toLowerCase(), MessageType.INFO);

            } else {
                String timeStr = specificTimeField.getText().trim();

                if (timeStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a time", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    DateTimeFormatter formatter = timeStr.length() == 5 ? 
                        DateTimeFormatter.ofPattern("HH:mm") : 
                        DateTimeFormatter.ofPattern("HH:mm:ss");
                    LocalTime.parse(timeStr, formatter);
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Invalid time format. Use HH:MM or HH:MM:SS", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                scheduler.scheduleAtFixedRate(() -> {
                    LocalTime now = LocalTime.now();
                    String currentTimeStr;

                    if (timeStr.length() == 5) {
                        currentTimeStr = now.format(DateTimeFormatter.ofPattern("HH:mm"));
                    } else {
                        currentTimeStr = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    }

                    if (currentTimeStr.equals(timeStr)) {
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastScheduledRun > 30000) {
                            lastScheduledRun = currentTime;
                            if (!isLoginInProgress) {
                                SwingUtilities.invokeLater(() -> {
                                    logMessage("Scheduled login triggered at: " + currentTimeStr);
                                    performLogin();
                                });
                            } else {
                                logMessage("Skipping scheduled login at " + currentTimeStr + " - login already in progress");
                            }
                        }
                    }
                }, 0, 5, TimeUnit.SECONDS);

                logMessage("Scheduler started for daily login at: " + timeStr);
                displayNotification("Scheduler started for daily login at " + timeStr, MessageType.INFO);
            }

            isScheduled = true;
            scheduleButton.setText("Stop Scheduler");
            scheduleButton.setButtonColor(ModernColors.ERROR, new Color(220, 67, 54));
            stopButton.setEnabled(true);
            updateStatus("Scheduler active", ModernColors.ACCENT);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid interval value", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logMessage("Error starting scheduler: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error starting scheduler: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stopScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        isScheduled = false;
        scheduleButton.setText("Start Scheduler");
        scheduleButton.setButtonColor(ModernColors.SUCCESS, new Color(96, 195, 100));
        scheduleButton.setEnabled(true);
        stopButton.setEnabled(false);
        updateStatus("Ready", ModernColors.SUCCESS);
        logMessage("Scheduler stopped");
        displayNotification("Scheduler stopped", MessageType.INFO);
        lastScheduledRun = 0;
    }

    private boolean attemptLogin(String username, String password) {
        HttpURLConnection connection = null;
        try {
            if (!NetworkUtils.isWiFiPortalAvailable()) {
    logMessage("No WiFi portal connection available - ensure you're connected to WiFi network");
    return false;
}

            URI uri = new URI(LOGIN_URL);
            URL url = uri.toURL();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("User-Agent", "WiFi-Authenticator/2.0");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            String currentMillis = String.valueOf(System.currentTimeMillis());

            StringBuilder urlParameters = new StringBuilder();
            urlParameters.append("mode=").append(LOGIN_MODE)
                         .append("&username=").append(username)
                         .append("&a=").append(currentMillis)
                         .append("&producttype=").append(PRODUCTTYPE)
                         .append("&password=").append(password);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = urlParameters.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            logMessage("Response Code: " + responseCode + " for " + username);

            if (responseCode == 200) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line).append("\n");

                        if (line.contains("You are signed in as") || line.contains("already logged in")) {
                            return true;
                        }

                        if (line.contains("Your data transfer has been exceeded") || 
                            line.contains("Login failed") ||
                            line.contains("Invalid username") ||
                            line.contains("Invalid password")) {
                            logMessage("Login rejected: " + line.trim());
                            return false;
                        }
                    }

                    String responseStr = response.toString();
                    if (responseStr.length() > 200) {
                        logMessage("Response preview: " + responseStr.substring(0, 200) + "...");
                    } else {
                        logMessage("Response: " + responseStr);
                    }
                }
            } else {
                logMessage("HTTP Error " + responseCode + " for " + username);
                return false;
            }
        } catch (Exception e) {
            logMessage("Login error for " + username + ": " + e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return false;
    }

    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String logEntry = "[" + timestamp + "] " + message + "\n";
            logArea.append(logEntry);
            logArea.setCaretPosition(logArea.getDocument().getLength());

            if (logArea.getDocument().getLength() > 50000) {
                try {
                    logArea.getDocument().remove(0, 10000);
                } catch (Exception e) {

                }
            }
        });
    }

    private void updateStatus(String status, Color color) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(status);
            statusLabel.setForeground(color);
        });
    }

    private void displayNotification(String message, MessageType type) {
        try {
            if (SystemTray.isSupported() && trayIcon != null) {
                if (tray.getTrayIcons().length == 0) {
                    tray.add(trayIcon);
                }
                trayIcon.displayMessage("WiFi AutoLogger", message, type);
            }
        } catch (Exception e) {
            logMessage("Notification error: " + e.getMessage());
        }
    }

    private void exitApplication() {
        logMessage("Application shutting down...");

        saveCurrentSettings();

        stopScheduler();

        if (tray != null && trayIcon != null) {
            tray.remove(trayIcon);
        }

        logMessage("All data saved. Goodbye!");

        System.exit(0);
    }

    private void saveCurrentSettings() {
        try {
            CredentialManager.saveSettings(
                autoStartCheckBox.isSelected(),
                autoLoginOnStartCheckBox.isSelected(),
                startOnBootCheckBox.isSelected(),
                minimizeToTrayCheckBox.isSelected(),
                intervalRadio.isSelected(),
                intervalField.getText(),
                (String) timeUnitCombo.getSelectedItem(),
                specificTimeField.getText()
            );

            logMessage("Settings saved to disk");

        } catch (Exception e) {
            logMessage("Error saving settings: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "gasp");
        System.setProperty("swing.aatext", "true");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {

        }

        SwingUtilities.invokeLater(() -> {
            try {
                new ImprovedWifiAuthenticator().setVisible(true);
            } catch (Exception e) {
                System.err.println("Error starting application: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}