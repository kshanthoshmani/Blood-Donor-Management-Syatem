package gui;

import database.DatabaseConnection;
import exception.DatabaseException;
import exception.ValidationException;
import model.User;
import service.AuthenticationService;
import util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * LoginFrame – Professional login screen for the Blood Donor System.
 * Demonstrates: Swing layout, event handling, SwingWorker for async DB ops,
 *               input validation, exception handling.
 */
public class LoginFrame extends JFrame {

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox      chkShowPass;
    private JButton        btnLogin;
    private JButton        btnClear;
    private JLabel         lblStatus;
    private JLabel         lblDbStatus;

    private final AuthenticationService authService = new AuthenticationService();

    // ── Constructor ───────────────────────────────────────────────────────────

    public LoginFrame() {
        ThemeManager.applyLightTheme();
        initUI();
        attachListeners();
        ThemeManager.refreshWindow(this);
        checkDatabaseOnStartup();
    }

    // ── UI ────────────────────────────────────────────────────────────────────

    private void initUI() {
        setTitle("Blood Donor Information System – Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 620);
        setLocationRelativeTo(null);
        setResizable(false);
        setMinimumSize(new Dimension(420, 560));

        // Root panel – light gray page bg
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ThemeManager.C_BG);
        root.add(buildLeftAccentBar(), BorderLayout.WEST);
        root.add(buildCenter(), BorderLayout.CENTER);
        setContentPane(root);
    }

    /** Thin colored accent bar on the left edge. */
    private JPanel buildLeftAccentBar() {
        JPanel bar = new JPanel();
        bar.setBackground(ThemeManager.C_ACCENT);
        bar.setPreferredSize(new Dimension(6, 0));
        return bar;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(ThemeManager.C_BG);
        center.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel card = buildLoginCard();
        center.add(card, new GridBagConstraints());
        return center;
    }

    private JPanel buildLoginCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(ThemeManager.C_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.C_BORDER, 1),
            new EmptyBorder(36, 40, 32, 40)
        ));

        // ── Logo / Icon ────────────────────────────────────────────────────
        JLabel iconLbl = new JLabel("\uD83E\uDE78", SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── App title ──────────────────────────────────────────────────────
        JLabel title = new JLabel("Blood Donor System");
        title.setFont(ThemeManager.F_TITLE);
        title.setForeground(ThemeManager.C_TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to your account");
        subtitle.setFont(ThemeManager.F_SMALL);
        subtitle.setForeground(ThemeManager.C_TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Divider ────────────────────────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeManager.C_BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // ── Username field ─────────────────────────────────────────────────
        JLabel lblUser = formLabel("Username");
        txtUsername = ThemeManager.createStyledTextField(22);
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        // ── Password field ─────────────────────────────────────────────────
        JLabel lblPass = formLabel("Password");
        txtPassword = ThemeManager.createStyledPasswordField(22);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        // ── Show password ──────────────────────────────────────────────────
        chkShowPass = new JCheckBox("Show Password");
        chkShowPass.setBackground(ThemeManager.C_PANEL);
        chkShowPass.setForeground(ThemeManager.C_TEXT_MUTED);
        chkShowPass.setFont(ThemeManager.F_SMALL);
        chkShowPass.setFocusPainted(false);
        chkShowPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Status label ───────────────────────────────────────────────────
        lblStatus = new JLabel(" ");
        lblStatus.setFont(ThemeManager.F_SMALL);
        lblStatus.setForeground(ThemeManager.C_DANGER);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        lblStatus.setOpaque(true);

        // ── Login button ───────────────────────────────────────────────────
        btnLogin = ThemeManager.createAccentButton("  LOGIN  ");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Clear button ───────────────────────────────────────────────────
        btnClear = ThemeManager.createSecondaryButton("Clear");
        btnClear.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnClear.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── DB Status ──────────────────────────────────────────────────────
        lblDbStatus = new JLabel("● Checking database…");
        lblDbStatus.setFont(ThemeManager.F_SMALL);
        lblDbStatus.setForeground(ThemeManager.C_TEXT_MUTED);
        lblDbStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Hint ───────────────────────────────────────────────────────────
        JLabel hint = new JLabel("Default: admin / admin123");
        hint.setFont(ThemeManager.F_TINY);
        hint.setForeground(ThemeManager.C_TEXT_MUTED);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Assemble ───────────────────────────────────────────────────────
        card.add(iconLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));
        card.add(sep);
        card.add(Box.createVerticalStrut(20));

        card.add(lblUser);
        card.add(Box.createVerticalStrut(4));
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(14));

        card.add(lblPass);
        card.add(Box.createVerticalStrut(4));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(6));
        card.add(chkShowPass);
        card.add(Box.createVerticalStrut(16));

        card.add(lblStatus);
        card.add(Box.createVerticalStrut(4));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(8));
        card.add(btnClear);
        card.add(Box.createVerticalStrut(20));

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(ThemeManager.C_BORDER);
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(sep2);
        card.add(Box.createVerticalStrut(10));
        card.add(lblDbStatus);
        card.add(Box.createVerticalStrut(4));
        card.add(hint);

        return card;
    }

    private JLabel formLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(ThemeManager.C_TEXT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // ── Listeners ─────────────────────────────────────────────────────────────

    private void attachListeners() {
        btnLogin.addActionListener(e -> doLogin());
        btnClear.addActionListener(e -> doClear());

        chkShowPass.addActionListener(e ->
            txtPassword.setEchoChar(chkShowPass.isSelected() ? (char) 0 : '\u2022')
        );

        // Enter key in either field triggers login
        KeyAdapter enter = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        };
        txtUsername.addKeyListener(enter);
        txtPassword.addKeyListener(enter);
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        btnLogin.setEnabled(false);
        clearStatus();

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    dispose();
                    SwingUtilities.invokeLater(() -> new DashboardFrame(user).setVisible(true));
                } catch (java.util.concurrent.ExecutionException ex) {
                    Throwable cause = ex.getCause();
                    if (cause instanceof ValidationException ve) {
                        showError(ve.getMessage());
                    } else if (cause instanceof SecurityException se) {
                        showError(se.getMessage());
                    } else if (cause instanceof DatabaseException de) {
                        showDbError(de.getMessage());
                    } else {
                        showError("Unexpected error: " + cause.getMessage());
                    }
                } catch (Exception ex) {
                    showError("Login error: " + ex.getMessage());
                } finally {
                    btnLogin.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void doClear() {
        txtUsername.setText("");
        txtPassword.setText("");
        clearStatus();
        txtUsername.requestFocus();
    }

    private void showError(String msg) {
        lblStatus.setBackground(ThemeManager.C_DANGER_BG);
        lblStatus.setForeground(ThemeManager.C_DANGER);
        lblStatus.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.C_DANGER, 1),
            new EmptyBorder(4, 8, 4, 8)
        ));
        lblStatus.setText("<html><center>" + msg.replace("\n", "<br>") + "</center></html>");
    }

    private void clearStatus() {
        lblStatus.setText(" ");
        lblStatus.setBackground(null);
        lblStatus.setOpaque(false);
        lblStatus.setBorder(null);
    }

    private void showDbError(String msg) {
        Object[] options = {"Retry", "Exit"};
        int choice = JOptionPane.showOptionDialog(this,
            "Database Connection Failed\n\n"
            + "Unable to connect to the database.\n"
            + "Please verify the database is running.\n\n"
            + "Technical detail:\n" + msg,
            "Database Error",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null, options, options[0]);

        if (choice == 0) doLogin();
        else if (choice == 1) System.exit(1);
    }

    // ── Startup database check ────────────────────────────────────────────────

    private void checkDatabaseOnStartup() {
        lblDbStatus.setText("● Connecting to database…");
        lblDbStatus.setForeground(ThemeManager.C_TEXT_MUTED);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override protected Boolean doInBackground() {
                try { return DatabaseConnection.getInstance().testConnection(); }
                catch (Exception e) { return false; }
            }

            @Override protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        lblDbStatus.setText("● Database Connected");
                        lblDbStatus.setForeground(ThemeManager.C_SUCCESS);
                    } else {
                        lblDbStatus.setText("● Database Unavailable");
                        lblDbStatus.setForeground(ThemeManager.C_DANGER);
                    }
                } catch (Exception ex) {
                    lblDbStatus.setText("● Database Error");
                    lblDbStatus.setForeground(ThemeManager.C_DANGER);
                }
            }
        };
        worker.execute();
    }

    // ── Entry point ───────────────────────────────────────────────────────────

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
