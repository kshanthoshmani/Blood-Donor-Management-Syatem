package util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * ThemeManager – Centralized Design System and Dynamic Theme Engine for Blood Donor System.
 * Ensures consistent colors, typography, borders, and controls across all screens.
 * STRICTLY preserves #000000 (pure black) for all normal text across both Light and Dark modes.
 * Strictly protects branding, logos, icons, blood groups, and donor data from being recolored.
 */
public class ThemeManager {

    // ── Theme State ───────────────────────────────────────────────────────────
    private static boolean darkMode = false;

    // ── Mandatory Centralized Palette Tokens (Requirement 5) ───────────────────
    public static Color BACKGROUND_COLOR   = new Color(0xF5F7FA); // Main background
    public static Color CARD_COLOR         = new Color(0xFFFFFF); // Elevated card background
    public static Color PANEL_COLOR        = new Color(0xFFFFFF); // Content panel background
    public static Color PRIMARY_COLOR      = new Color(0x2563EB); // Royal Blue 600 accent
    public static Color SECONDARY_COLOR    = new Color(0x64748B); // Slate secondary
    public static Color BORDER_COLOR       = new Color(0xE2E8F0); // Light Gray border
    public static final Color TEXT_COLOR   = new Color(0x000000); // STRICT #000000 Pure Black normal text
    public static Color INPUT_COLOR        = new Color(0xFFFFFF); // Textfield / Combo background
    public static Color TABLE_HEADER_COLOR = new Color(0xF1F5F9); // Table header background
    public static Color SUCCESS_COLOR      = new Color(0x16A34A); // Emerald 600
    public static Color ERROR_COLOR        = new Color(0xDC2626); // Crimson 600

    // ── Additional Palette Tokens ─────────────────────────────────────────────
    public static Color WARNING_COLOR      = new Color(0xD97706); // Amber 600
    public static Color TABLE_ROW_COLOR    = new Color(0xFFFFFF); // Table row primary
    public static Color TABLE_ALT_COLOR    = new Color(0xF8FAFC); // Table alternate row
    public static Color TABLE_SEL_COLOR    = new Color(0xDBEAFE); // Table selection
    public static final Color TABLE_SEL_FG = new Color(0x000000); // Table selected text (pure black)
    public static Color BORDER_FOCUS_COLOR = new Color(0x3B82F6); // Blue 500 focus ring
    public static Color SUCCESS_BG_COLOR   = new Color(0xDCFCE7); // Emerald 100 bg
    public static Color ERROR_BG_COLOR     = new Color(0xFEE2E2); // Crimson 100 bg
    public static Color WARNING_BG_COLOR   = new Color(0xFEF3C7); // Amber 100 bg
    public static Color ACCENT_LIGHT_COLOR = new Color(0xEFF6FF); // Blue 50 badge bg
    public static Color ACCENT_HOVER_COLOR = new Color(0x1D4ED8); // Blue 700

    // ── Permanent Branding & Navigation Colors (NEVER recolored across modes) ─
    public static final Color HEADER_COLOR        = new Color(0x0F172A); // Slate 900 Top bar
    public static final Color HEADER_TEXT_COLOR   = new Color(0xFFFFFF); // White branding text
    public static final Color HEADER_USER_COLOR   = new Color(0xBFDBFE); // Blue tint user text
    public static final Color SIDEBAR_COLOR       = new Color(0x0F172A); // Slate 900 Sidebar
    public static final Color SIDEBAR_BRAND_COLOR = new Color(0x070B12); // Branding container
    public static final Color SIDEBAR_BRAND_TITLE = new Color(0x93C5FD); // Logo title
    public static final Color SIDEBAR_BRAND_SUB   = new Color(0x94A3B8); // Subtitle
    public static final Color SIDEBAR_HOVER_COLOR = new Color(0x1E293B); // Item hover
    public static final Color SIDEBAR_SEL_COLOR   = new Color(0x2563EB); // Active nav pill
    public static final Color SIDEBAR_TEXT_COLOR  = new Color(0x94A3B8); // Inactive nav text

    // ── Metric Stat Card Accent Colors ────────────────────────────────────────
    public static final Color C_CARD_BLUE   = new Color(0x2563EB); // Blue stat strip
    public static final Color C_CARD_GREEN  = new Color(0x16A34A); // Green stat strip
    public static final Color C_CARD_RED    = new Color(0xDC2626); // Red stat strip
    public static final Color C_CARD_PURPLE = new Color(0x7C3AED); // Purple stat strip

    // ── Legacy Aliases (maintains backward compatibility with all files) ──────
    public static Color C_BG             = BACKGROUND_COLOR;
    public static Color C_PANEL          = PANEL_COLOR;
    public static Color C_HEADER         = HEADER_COLOR;
    public static Color C_SIDEBAR        = SIDEBAR_COLOR;
    public static Color C_SIDEBAR_HOVER  = SIDEBAR_HOVER_COLOR;
    public static Color C_SIDEBAR_SEL    = SIDEBAR_SEL_COLOR;
    public static Color C_SIDEBAR_TEXT   = SIDEBAR_TEXT_COLOR;
    public static Color C_SIDEBAR_BRAND  = SIDEBAR_BRAND_COLOR;
    public static Color C_ACCENT         = PRIMARY_COLOR;
    public static Color C_ACCENT_HOVER   = ACCENT_HOVER_COLOR;
    public static Color C_ACCENT_LIGHT   = ACCENT_LIGHT_COLOR;
    public static Color C_ACCENT_TEXT    = PRIMARY_COLOR;
    public static Color C_TEXT           = TEXT_COLOR;
    public static Color C_TEXT_MUTED     = TEXT_COLOR;
    public static Color C_TEXT_WHITE     = Color.WHITE;
    public static Color C_INPUT_BG       = INPUT_COLOR;
    public static Color C_BORDER         = BORDER_COLOR;
    public static Color C_BORDER_FOCUS   = BORDER_FOCUS_COLOR;
    public static Color C_TABLE_HEADER   = TABLE_HEADER_COLOR;
    public static Color C_TABLE_ROW      = TABLE_ROW_COLOR;
    public static Color C_TABLE_ALT      = TABLE_ALT_COLOR;
    public static Color C_TABLE_SEL      = TABLE_SEL_COLOR;
    public static Color C_TABLE_SEL_FG   = TABLE_SEL_FG;
    public static Color C_SUCCESS        = SUCCESS_COLOR;
    public static Color C_SUCCESS_BG     = SUCCESS_BG_COLOR;
    public static Color C_DANGER         = ERROR_COLOR;
    public static Color C_DANGER_BG      = ERROR_BG_COLOR;
    public static Color C_WARNING        = WARNING_COLOR;
    public static Color C_WARNING_BG     = WARNING_BG_COLOR;

    // ── Unified Typography Hierarchy ──────────────────────────────────────────
    public static final Font F_TITLE     = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font F_HEADING   = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font F_SUB       = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font F_LABEL     = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font F_INPUT     = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font F_BTN       = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font F_TABLE     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font F_SMALL     = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font F_TINY      = new Font("Segoe UI", Font.PLAIN, 11);

    private ThemeManager() {}

    // ── Centralized Getters ───────────────────────────────────────────────────
    public static boolean isDarkMode()     { return darkMode; }
    public static Color bg()               { return BACKGROUND_COLOR; }
    public static Color panel()            { return PANEL_COLOR; }
    public static Color header()           { return HEADER_COLOR; }
    public static Color sidebar()          { return SIDEBAR_COLOR; }
    public static Color sidebarHover()     { return SIDEBAR_HOVER_COLOR; }
    public static Color sidebarSel()       { return SIDEBAR_SEL_COLOR; }
    public static Color sidebarText()      { return SIDEBAR_TEXT_COLOR; }
    public static Color sidebarBrand()     { return SIDEBAR_BRAND_COLOR; }
    public static Color card()             { return CARD_COLOR; }
    public static Color accent()           { return PRIMARY_COLOR; }
    public static Color primary()          { return PRIMARY_COLOR; }
    public static Color secondary()        { return SECONDARY_COLOR; }
    public static Color accentHover()      { return ACCENT_HOVER_COLOR; }
    public static Color accentLight()      { return ACCENT_LIGHT_COLOR; }
    public static Color accentText()       { return PRIMARY_COLOR; }
    public static Color accent2()          { return C_CARD_PURPLE; }
    public static Color text()             { return TEXT_COLOR; } // Pure Black #000000
    public static Color textMuted()        { return TEXT_COLOR; } // Pure Black #000000
    public static Color textWhite()        { return Color.WHITE; }
    public static Color border()           { return BORDER_COLOR; }
    public static Color borderFocus()      { return BORDER_FOCUS_COLOR; }
    public static Color success()          { return SUCCESS_COLOR; }
    public static Color successBg()        { return SUCCESS_BG_COLOR; }
    public static Color danger()           { return ERROR_COLOR; }
    public static Color dangerBg()         { return ERROR_BG_COLOR; }
    public static Color warning()          { return WARNING_COLOR; }
    public static Color warningBg()        { return WARNING_BG_COLOR; }
    public static Color tableRow()         { return TABLE_ROW_COLOR; }
    public static Color tableAlt()         { return TABLE_ALT_COLOR; }
    public static Color tableSel()         { return TABLE_SEL_COLOR; }
    public static Color tableSelFg()       { return TABLE_SEL_FG; }
    public static Color tableHeader()      { return TABLE_HEADER_COLOR; }
    public static Color inputBg()          { return INPUT_COLOR; }

    // ── Apply Themes ──────────────────────────────────────────────────────────

    /**
     * Sets the application to Constant Modern Light Mode.
     * Normal text is strictly #000000 (Pure Black).
     */
    public static void applyLightTheme() {
        darkMode = false;

        BACKGROUND_COLOR   = new Color(0xF5F7FA); // Main Background -> #F5F7FA
        CARD_COLOR         = new Color(0xFFFFFF); // Card Background -> #FFFFFF
        PANEL_COLOR        = new Color(0xFFFFFF); // Panel Background -> #FFFFFF
        INPUT_COLOR        = new Color(0xFFFFFF); // Input Background -> #FFFFFF
        BORDER_COLOR       = new Color(0xE2E8F0); // Border -> Light Gray
        PRIMARY_COLOR      = new Color(0x2563EB); // Primary Accent -> Blue
        SECONDARY_COLOR    = new Color(0x64748B);

        TABLE_HEADER_COLOR = new Color(0xF1F5F9);
        TABLE_ROW_COLOR    = new Color(0xFFFFFF);
        TABLE_ALT_COLOR    = new Color(0xF8FAFC);
        TABLE_SEL_COLOR    = new Color(0xDBEAFE);
        BORDER_FOCUS_COLOR = new Color(0x3B82F6);

        SUCCESS_COLOR      = new Color(0x16A34A);
        ERROR_COLOR        = new Color(0xDC2626);
        WARNING_COLOR      = new Color(0xD97706);
        SUCCESS_BG_COLOR   = new Color(0xDCFCE7);
        ERROR_BG_COLOR     = new Color(0xFEE2E2);
        WARNING_BG_COLOR   = new Color(0xFEF3C7);
        ACCENT_LIGHT_COLOR = new Color(0xEFF6FF);
        ACCENT_HOVER_COLOR = new Color(0x1D4ED8);

        syncLegacyTokens();
        updateUIManager();
    }

    /**
     * Sets the application to Controlled Dark Mode.
     * Changes background/accent surfaces while strictly maintaining #000000 black text
     * on clean, high-contrast surfaces. Logo and branding remain 100% unchanged.
     */
    public static void applyDarkTheme() {
        darkMode = true;

        BACKGROUND_COLOR   = new Color(0x1E293B); // Controlled dark slate background
        CARD_COLOR         = new Color(0xF8FAFC); // High-contrast crisp card background
        PANEL_COLOR        = new Color(0xF8FAFC); // High-contrast content panel
        INPUT_COLOR        = new Color(0xFFFFFF); // Crisp white input for black text
        BORDER_COLOR       = new Color(0x475569); // Slate outline border
        PRIMARY_COLOR      = new Color(0x3B82F6); // Blue accent
        SECONDARY_COLOR    = new Color(0x64748B);

        TABLE_HEADER_COLOR = new Color(0xE2E8F0);
        TABLE_ROW_COLOR    = new Color(0xFFFFFF);
        TABLE_ALT_COLOR    = new Color(0xF1F5F9);
        TABLE_SEL_COLOR    = new Color(0xBFDBFE);
        BORDER_FOCUS_COLOR = new Color(0x60A5FA);

        SUCCESS_COLOR      = new Color(0x16A34A);
        ERROR_COLOR        = new Color(0xDC2626);
        WARNING_COLOR      = new Color(0xD97706);
        SUCCESS_BG_COLOR   = new Color(0xDCFCE7);
        ERROR_BG_COLOR     = new Color(0xFEE2E2);
        WARNING_BG_COLOR   = new Color(0xFEF3C7);
        ACCENT_LIGHT_COLOR = new Color(0xEFF6FF);
        ACCENT_HOVER_COLOR = new Color(0x2563EB);

        syncLegacyTokens();
        updateUIManager();
    }

    private static void syncLegacyTokens() {
        C_BG             = BACKGROUND_COLOR;
        C_PANEL          = PANEL_COLOR;
        C_HEADER         = HEADER_COLOR;
        C_SIDEBAR        = SIDEBAR_COLOR;
        C_SIDEBAR_HOVER  = SIDEBAR_HOVER_COLOR;
        C_SIDEBAR_SEL    = SIDEBAR_SEL_COLOR;
        C_SIDEBAR_TEXT   = SIDEBAR_TEXT_COLOR;
        C_SIDEBAR_BRAND  = SIDEBAR_BRAND_COLOR;
        C_ACCENT         = PRIMARY_COLOR;
        C_ACCENT_HOVER   = ACCENT_HOVER_COLOR;
        C_ACCENT_LIGHT   = ACCENT_LIGHT_COLOR;
        C_ACCENT_TEXT    = PRIMARY_COLOR;
        C_TEXT           = TEXT_COLOR;      // ALWAYS #000000
        C_TEXT_MUTED     = TEXT_COLOR;      // ALWAYS #000000
        C_INPUT_BG       = INPUT_COLOR;
        C_BORDER         = BORDER_COLOR;
        C_BORDER_FOCUS   = BORDER_FOCUS_COLOR;
        C_TABLE_HEADER   = TABLE_HEADER_COLOR;
        C_TABLE_ROW      = TABLE_ROW_COLOR;
        C_TABLE_ALT      = TABLE_ALT_COLOR;
        C_TABLE_SEL      = TABLE_SEL_COLOR;
        C_TABLE_SEL_FG   = TABLE_SEL_FG;
        C_SUCCESS        = SUCCESS_COLOR;
        C_SUCCESS_BG     = SUCCESS_BG_COLOR;
        C_DANGER         = ERROR_COLOR;
        C_DANGER_BG      = ERROR_BG_COLOR;
        C_WARNING        = WARNING_COLOR;
        C_WARNING_BG     = WARNING_BG_COLOR;
    }

    /** Toggles between Light and Dark mode */
    public static void toggleTheme() {
        if (isDarkMode()) {
            applyLightTheme();
        } else {
            applyDarkTheme();
        }
    }

    private static void updateUIManager() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("Panel.background",              BACKGROUND_COLOR);
        UIManager.put("OptionPane.background",         CARD_COLOR);
        UIManager.put("OptionPane.messageForeground",  TEXT_COLOR);
        UIManager.put("Dialog.background",             CARD_COLOR);
        UIManager.put("Label.foreground",              TEXT_COLOR);

        UIManager.put("TextField.background",          INPUT_COLOR);
        UIManager.put("TextField.foreground",          TEXT_COLOR);
        UIManager.put("TextField.caretForeground",     TEXT_COLOR);
        UIManager.put("TextField.selectionBackground", PRIMARY_COLOR);
        UIManager.put("TextField.selectionForeground", Color.WHITE);

        UIManager.put("PasswordField.background",      INPUT_COLOR);
        UIManager.put("PasswordField.foreground",      TEXT_COLOR);
        UIManager.put("PasswordField.caretForeground", TEXT_COLOR);

        UIManager.put("TextArea.background",           INPUT_COLOR);
        UIManager.put("TextArea.foreground",           TEXT_COLOR);

        UIManager.put("ComboBox.background",           INPUT_COLOR);
        UIManager.put("ComboBox.foreground",           TEXT_COLOR);
        UIManager.put("ComboBox.selectionBackground",  PRIMARY_COLOR);
        UIManager.put("ComboBox.selectionForeground",  Color.WHITE);

        UIManager.put("Table.background",              TABLE_ROW_COLOR);
        UIManager.put("Table.alternateRowColor",       TABLE_ALT_COLOR);
        UIManager.put("Table.foreground",              TEXT_COLOR);
        UIManager.put("Table.selectionBackground",     TABLE_SEL_COLOR);
        UIManager.put("Table.selectionForeground",     TABLE_SEL_FG);
        UIManager.put("Table.gridColor",               BORDER_COLOR);
        UIManager.put("TableHeader.background",        TABLE_HEADER_COLOR);
        UIManager.put("TableHeader.foreground",        TEXT_COLOR);

        UIManager.put("ScrollPane.background",         BACKGROUND_COLOR);
        UIManager.put("ScrollBar.thumb",               BORDER_COLOR);
        UIManager.put("ScrollBar.track",               BACKGROUND_COLOR);

        UIManager.put("CheckBox.background",           CARD_COLOR);
        UIManager.put("CheckBox.foreground",           TEXT_COLOR);
        UIManager.put("RadioButton.background",        CARD_COLOR);
        UIManager.put("RadioButton.foreground",        TEXT_COLOR);

        UIManager.put("Menu.background",               CARD_COLOR);
        UIManager.put("Menu.foreground",               TEXT_COLOR);
        UIManager.put("MenuItem.background",           CARD_COLOR);
        UIManager.put("MenuItem.foreground",           TEXT_COLOR);

        UIManager.put("ToolTip.background",            CARD_COLOR);
        UIManager.put("ToolTip.foreground",            TEXT_COLOR);
    }

    // ── Dynamic Window Refresh ────────────────────────────────────────────────

    /**
     * Refreshes the window UI without changing logo, branding, or converting text to white.
     */
    public static void refreshWindow(Window window) {
        if (window == null) return;
        applyToComponent(window);
        window.revalidate();
        window.repaint();
    }

    private static void applyToComponent(Component c) {
        if (c == null) return;

        // Never modify components explicitly marked to preserve their colors
        if (c instanceof JComponent jc && Boolean.TRUE.equals(jc.getClientProperty("preserveColor"))) {
            return;
        }

        if (c instanceof JPanel p) {
            String name = p.getName();
            if ("SIDEBAR".equalsIgnoreCase(name)) {
                p.setBackground(SIDEBAR_COLOR);
                return;
            } else if ("HEADER".equalsIgnoreCase(name)) {
                p.setBackground(HEADER_COLOR);
                return;
            } else if ("BRAND".equalsIgnoreCase(name)) {
                p.setBackground(SIDEBAR_BRAND_COLOR);
                return;
            } else if ("CARD".equalsIgnoreCase(name)) {
                p.setBackground(CARD_COLOR);
                updateBorders(p);
            } else if ("PANEL".equalsIgnoreCase(name)) {
                p.setBackground(PANEL_COLOR);
                updateBorders(p);
            } else if (p.isOpaque()) {
                p.setBackground(BACKGROUND_COLOR);
                updateBorders(p);
            }
        } else if (c instanceof JLabel lbl) {
            // Keep status colors and badges stable; update normal labels to pure black
            Color fg = lbl.getForeground();
            if (!SUCCESS_COLOR.equals(fg) && !ERROR_COLOR.equals(fg) && !WARNING_COLOR.equals(fg)
                    && !Color.WHITE.equals(fg) && !HEADER_USER_COLOR.equals(fg) && !SIDEBAR_BRAND_TITLE.equals(fg)) {
                lbl.setForeground(TEXT_COLOR); // Strict #000000
            }
        } else if (c instanceof JTextComponent tc) {
            tc.setBackground(tc.isEditable() ? INPUT_COLOR : CARD_COLOR);
            tc.setForeground(TEXT_COLOR);
            tc.setCaretColor(TEXT_COLOR);
            updateBorders(tc);
        } else if (c instanceof JComboBox<?> cb) {
            cb.setBackground(INPUT_COLOR);
            cb.setForeground(TEXT_COLOR);
            updateBorders(cb);
        } else if (c instanceof JTable t) {
            t.setBackground(TABLE_ROW_COLOR);
            t.setForeground(TEXT_COLOR);
            t.setSelectionBackground(TABLE_SEL_COLOR);
            t.setSelectionForeground(TABLE_SEL_FG);
            t.setGridColor(BORDER_COLOR);
            JTableHeader th = t.getTableHeader();
            if (th != null) {
                th.setBackground(TABLE_HEADER_COLOR);
                th.setForeground(TEXT_COLOR);
            }
        } else if (c instanceof JScrollPane sp) {
            sp.setBackground(BACKGROUND_COLOR);
            if (sp.getViewport() != null) {
                sp.getViewport().setBackground(PANEL_COLOR);
            }
            updateBorders(sp);
        } else if (c instanceof JButton btn) {
            String tag = (String) btn.getClientProperty("btnType");
            if ("secondary".equals(tag)) {
                btn.setBackground(CARD_COLOR);
                btn.setForeground(TEXT_COLOR);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    new EmptyBorder(7, 16, 7, 16)
                ));
            } else if ("accent".equals(tag)) {
                btn.setBackground(PRIMARY_COLOR);
            }
        }

        if (c instanceof Container container) {
            for (Component child : container.getComponents()) {
                applyToComponent(child);
            }
        }
    }

    private static void updateBorders(JComponent comp) {
        Border b = comp.getBorder();
        if (b instanceof TitledBorder tb) {
            tb.setTitleColor(TEXT_COLOR);
            tb.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        } else if (b instanceof LineBorder) {
            comp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        }
    }

    // ── Button Factories ──────────────────────────────────────────────────────

    public static JButton createAccentButton(String text) {
        return buildButton(text, PRIMARY_COLOR, ACCENT_HOVER_COLOR, Color.WHITE, "accent");
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.putClientProperty("btnType", "secondary");
        btn.setBackground(CARD_COLOR);
        btn.setForeground(TEXT_COLOR); // Strict #000000
        btn.setFont(F_BTN);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(7, 16, 7, 16)
        ));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(0xE2E8F0));
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(CARD_COLOR);
            }
        });
        return btn;
    }

    public static JButton createDangerButton(String text) {
        return buildButton(text, ERROR_COLOR, new Color(0xB91C1C), Color.WHITE, "danger");
    }

    public static JButton createSuccessButton(String text) {
        return buildButton(text, SUCCESS_COLOR, new Color(0x15803D), Color.WHITE, "success");
    }

    private static JButton buildButton(String text, Color bg, Color hover, Color fg, String type) {
        JButton btn = new JButton(text);
        btn.putClientProperty("btnType", type);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(F_BTN);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
            @Override public void mousePressed(MouseEvent e) { btn.setBackground(hover.darker()); }
            @Override public void mouseReleased(MouseEvent e){ btn.setBackground(hover); }
        });
        return btn;
    }

    // ── Input Fields ──────────────────────────────────────────────────────────

    public static JTextField createStyledTextField(int cols) {
        JTextField tf = new JTextField(cols);
        styleInputComponent(tf);
        return tf;
    }

    public static JPasswordField createStyledPasswordField(int cols) {
        JPasswordField pf = new JPasswordField(cols);
        styleInputComponent(pf);
        return pf;
    }

    public static <T> JComboBox<T> createStyledComboBox(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setBackground(INPUT_COLOR);
        cb.setForeground(TEXT_COLOR);
        cb.setFont(F_INPUT);
        cb.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(4, 8, 4, 8)
        ));
        return cb;
    }

    private static void styleInputComponent(JTextComponent tc) {
        tc.setBackground(INPUT_COLOR);
        tc.setForeground(TEXT_COLOR);
        tc.setCaretColor(TEXT_COLOR);
        tc.setFont(F_INPUT);
        tc.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(7, 10, 7, 10)
        ));

        tc.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                tc.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_FOCUS_COLOR, 2),
                    new EmptyBorder(6, 9, 6, 9)
                ));
            }
            @Override public void focusLost(FocusEvent e) {
                tc.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    new EmptyBorder(7, 10, 7, 10)
                ));
            }
        });
    }

    // ── Border Helpers ────────────────────────────────────────────────────────

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(14, 18, 14, 18)
        );
    }

    public static Border sectionBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            "  " + title + "  ",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            F_SUB,
            TEXT_COLOR
        );
    }

    // ── Stat Card Component ───────────────────────────────────────────────────

    public static JPanel createStatCard(String icon, String label, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setName("CARD");
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(0, 0, 0, 0)
        ));

        // Left accent strip
        JPanel strip = new JPanel();
        strip.putClientProperty("preserveColor", Boolean.TRUE);
        strip.setBackground(color);
        strip.setPreferredSize(new Dimension(6, 0));
        card.add(strip, BorderLayout.WEST);

        // Content
        JPanel content = new JPanel();
        content.setName("PANEL");
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(CARD_COLOR);
        content.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.putClientProperty("preserveColor", Boolean.TRUE);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(color);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(TEXT_COLOR); // Strict #000000

        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(F_SMALL);
        labelLbl.setForeground(TEXT_COLOR); // Strict #000000

        content.add(iconLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(valueLabel);
        content.add(labelLbl);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    // ── Table Styling Helper ──────────────────────────────────────────────────

    public static void styleTable(JTable table) {
        table.setRowHeight(38);
        table.setFont(F_TABLE);
        table.setShowGrid(true);
        table.setGridColor(BORDER_COLOR);
        table.setBackground(TABLE_ROW_COLOR);
        table.setForeground(TEXT_COLOR); // Strict #000000
        table.setSelectionBackground(TABLE_SEL_COLOR);
        table.setSelectionForeground(TABLE_SEL_FG);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(TABLE_HEADER_COLOR);
        header.setForeground(TEXT_COLOR); // Strict #000000
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 36));

        // Default cell renderer with #000000 text and alternating rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean selected, boolean focused, int row, int col) {
                super.getTableCellRendererComponent(t, val, selected, focused, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!selected) {
                    setBackground(row % 2 == 0 ? TABLE_ROW_COLOR : TABLE_ALT_COLOR);
                    setForeground(TEXT_COLOR); // Strict #000000
                }
                return this;
            }
        });
    }

    // ── Document filter for numeric-only fields ───────────────────────────────

    public static void applyDigitOnlyFilter(JTextField field, int maxDigits) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(
            new DigitOnlyFilter(maxDigits)
        );
    }

    public static class DigitOnlyFilter extends DocumentFilter {
        private final int max;

        public DigitOnlyFilter(int max) {
            this.max = max;
        }

        @Override
        public void insertString(FilterBypass fb, int offset,
                                 String string, AttributeSet attr)
                throws BadLocationException {
            if (string == null) return;
            String digits = string.replaceAll("[^0-9]", "");
            int current = fb.getDocument().getLength();
            if (current + digits.length() > max) {
                digits = digits.substring(0, Math.max(0, max - current));
            }
            if (!digits.isEmpty()) super.insertString(fb, offset, digits, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length,
                            String string, AttributeSet attr)
                throws BadLocationException {
            if (string == null) { super.replace(fb, offset, length, "", attr); return; }
            String digits = string.replaceAll("[^0-9]", "");
            int current = fb.getDocument().getLength() - length;
            if (current + digits.length() > max) {
                digits = digits.substring(0, Math.max(0, max - current));
            }
            super.replace(fb, offset, length, digits, attr);
        }
    }
}
