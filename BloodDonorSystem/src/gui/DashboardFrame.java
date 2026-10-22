package gui;

import model.Donor;
import model.User;
import service.DonorService;
import util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DashboardFrame – Main application dashboard window.
 * Strictly satisfies all user requirements:
 * - Centralized theme configuration without random colors or white text conversion
 * - Strictly #000000 (Pure Black) for all normal text
 * - Permanent application branding, logo, and icons
 * - Full-width Recent Donor Registrations table that fills the available area
 * - Removed large empty gray void completely
 * - View Details button on every donor row opening complete donor details dialog
 * - Up to 10 latest donors ordered by newest registration first
 * - Dynamic statistics computed directly from the database
 */
public class DashboardFrame extends JFrame {

    // ── Navigation ────────────────────────────────────────────────────────────
    private static final String[] NAV_LABELS = {
        "Dashboard", "Register Donor", "View Donors", "Search Donor", "Update Donor", "Delete Donor"
    };
    private static final String[] NAV_ICONS = {
        "🏠", "➕", "📋", "🔍", "✏️", "🗑️"
    };
    private static final String[] CARD_NAMES = {
        "HOME", "REGISTER", "VIEW", "SEARCH", "UPDATE", "DELETE"
    };

    private final User         loggedInUser;
    private final DonorService donorService = new DonorService();

    // ── Layout ────────────────────────────────────────────────────────────────
    private JPanel     contentPanel;
    private CardLayout cardLayout;
    private JLabel[]   navItems;
    private int        currentNavIndex = 0;

    // ── Dashboard Overview Components ─────────────────────────────────────────
    private JPanel            statCardsPanel;
    private JButton           btnTheme;
    private JTable            recentTable;
    private DefaultTableModel recentTableModel;
    private JScrollPane       recentScrollPane;
    private JLabel            lblNoDonors;
    private final List<Donor> recentDonorsList = new ArrayList<>();

    // ── Child Panels ──────────────────────────────────────────────────────────
    private DonorRegistrationFrame regPanel;
    private DonorListFrame         listPanel;
    private SearchDonorFrame       searchPanel;

    // ── Table Column Definitions ──────────────────────────────────────────────
    private static final String[] TABLE_COLUMNS = {
        "Donor ID", "Donor Name", "Blood Group", "Gender", "City", "Phone", "Status", "Details"
    };

    // ── Constructor ───────────────────────────────────────────────────────────

    public DashboardFrame(User user) {
        this.loggedInUser = user;
        initUI();
        loadStats();
    }

    public DashboardFrame() {
        this(new User(0, "admin", ""));
    }

    // ── UI Initialization ─────────────────────────────────────────────────────

    private void initUI() {
        setTitle("Blood Donor Information System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1260, 780);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1020, 660));
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(ThemeManager.bg());
        root.add(buildTopBar(),  BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContent(), BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── Top Bar (Protected Branding) ──────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setName("HEADER");
        bar.setBackground(ThemeManager.header());
        bar.setPreferredSize(new Dimension(0, 56));
        bar.setBorder(new EmptyBorder(0, 22, 0, 18));

        // Left Branding: Logo + Name (NEVER modified by theme)
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 14));
        brandPanel.setOpaque(false);

        JLabel logoIcon = new JLabel("🩸");
        logoIcon.putClientProperty("preserveColor", Boolean.TRUE);
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));

        JLabel appName = new JLabel("Blood Donor Information System");
        appName.putClientProperty("preserveColor", Boolean.TRUE);
        appName.setFont(new Font("Segoe UI", Font.BOLD, 17));
        appName.setForeground(ThemeManager.HEADER_TEXT_COLOR);

        brandPanel.add(logoIcon);
        brandPanel.add(appName);
        bar.add(brandPanel, BorderLayout.WEST);

        // Right: User Profile + Theme Toggle + Logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        right.setOpaque(false);

        JLabel userLbl = new JLabel("👤 " + loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ")");
        userLbl.putClientProperty("preserveColor", Boolean.TRUE);
        userLbl.setFont(ThemeManager.F_LABEL);
        userLbl.setForeground(ThemeManager.HEADER_USER_COLOR);

        btnTheme = new JButton(ThemeManager.isDarkMode() ? "☀️ Light" : "🌙 Dark");
        btnTheme.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTheme.setBackground(ThemeManager.primary());
        btnTheme.setForeground(Color.WHITE);
        btnTheme.setFocusPainted(false);
        btnTheme.setBorderPainted(false);
        btnTheme.setOpaque(true);
        btnTheme.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTheme.setBorder(new EmptyBorder(7, 16, 7, 16));
        btnTheme.setToolTipText("Toggle Light / Dark Mode");
        btnTheme.addActionListener(e -> {
            ThemeManager.toggleTheme();
            btnTheme.setText(ThemeManager.isDarkMode() ? "☀️ Light" : "🌙 Dark");
            ThemeManager.refreshWindow(this);
            loadStats();
            updateNavHighlights();
        });

        JButton btnLogout = ThemeManager.createDangerButton("Logout");
        btnLogout.setFont(ThemeManager.F_BTN);
        btnLogout.setBorder(new EmptyBorder(7, 18, 7, 18));
        btnLogout.addActionListener(e -> doLogout());

        right.add(userLbl);
        right.add(btnTheme);
        right.add(btnLogout);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Sidebar (Protected Branding & Stable Icons) ───────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setName("SIDEBAR");
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(ThemeManager.sidebar());
        sidebar.setPreferredSize(new Dimension(220, 0));

        // Brand Container
        JPanel logo = new JPanel();
        logo.setName("BRAND");
        logo.setLayout(new BoxLayout(logo, BoxLayout.Y_AXIS));
        logo.setBackground(ThemeManager.sidebarBrand());
        logo.setBorder(new EmptyBorder(16, 18, 16, 18));
        logo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JPanel brandTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        brandTitle.setOpaque(false);
        brandTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logoIcon = new JLabel("🩸");
        logoIcon.putClientProperty("preserveColor", Boolean.TRUE);
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JLabel logoDrop = new JLabel("Donor System");
        logoDrop.putClientProperty("preserveColor", Boolean.TRUE);
        logoDrop.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logoDrop.setForeground(ThemeManager.SIDEBAR_BRAND_TITLE);

        brandTitle.add(logoIcon);
        brandTitle.add(logoDrop);

        JLabel logoSub = new JLabel("Admin Dashboard");
        logoSub.putClientProperty("preserveColor", Boolean.TRUE);
        logoSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        logoSub.setForeground(ThemeManager.SIDEBAR_BRAND_SUB);
        logoSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        logo.add(brandTitle);
        logo.add(Box.createVerticalStrut(2));
        logo.add(logoSub);
        sidebar.add(logo);

        sidebar.add(Box.createVerticalStrut(10));

        // Navigation Items
        navItems = new JLabel[NAV_LABELS.length];
        for (int i = 0; i < NAV_LABELS.length; i++) {
            navItems[i] = buildNavItem(NAV_ICONS[i], NAV_LABELS[i], i);
            sidebar.add(navItems[i]);
            sidebar.add(Box.createVerticalStrut(3));
        }

        sidebar.add(Box.createVerticalGlue());

        // Footer version label
        JLabel ver = new JLabel("v2.0 • Donor Portal");
        ver.putClientProperty("preserveColor", Boolean.TRUE);
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ver.setForeground(ThemeManager.sidebarText());
        ver.setBorder(new EmptyBorder(8, 18, 12, 18));
        sidebar.add(ver);

        return sidebar;
    }

    private JLabel buildNavItem(String icon, String label, int index) {
        JLabel item = new JLabel(icon + "  " + label);
        item.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        item.setOpaque(true);
        item.setBorder(new EmptyBorder(10, 18, 10, 18));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (index == 0) {
            item.setBackground(ThemeManager.sidebarSel());
            item.setForeground(Color.WHITE);
            item.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        } else {
            item.setBackground(ThemeManager.sidebar());
            item.setForeground(ThemeManager.sidebarText());
        }

        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e)  { showCard(CARD_NAMES[index], index); }
            @Override public void mouseEntered(MouseEvent e)  {
                if (index != currentNavIndex) {
                    item.setBackground(ThemeManager.sidebarHover());
                    item.setForeground(Color.WHITE);
                }
            }
            @Override public void mouseExited(MouseEvent e)   {
                if (index != currentNavIndex) {
                    item.setBackground(ThemeManager.sidebar());
                    item.setForeground(ThemeManager.sidebarText());
                }
            }
        });

        return item;
    }

    // ── Content Area ──────────────────────────────────────────────────────────

    private JPanel buildContent() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(ThemeManager.bg());

        // HOME card
        contentPanel.add(buildHomeCard(), "HOME");

        // REGISTER card
        regPanel = new DonorRegistrationFrame(() -> {
            listPanel.refresh();
            loadStats();
            showCard("HOME", 0);
        });
        contentPanel.add(regPanel, "REGISTER");

        // VIEW card
        listPanel = new DonorListFrame(this);
        contentPanel.add(listPanel, "VIEW");

        // SEARCH card
        searchPanel = new SearchDonorFrame(this);
        contentPanel.add(searchPanel, "SEARCH");

        // Placeholders
        contentPanel.add(new JLabel("Select a donor to update."), "UPDATE");
        contentPanel.add(new JLabel("Select a donor to delete."), "DELETE");

        return contentPanel;
    }

    // ── Home Card Layout (Responsive, Full-Width, No Empty Void) ──────────────

    private JPanel buildHomeCard() {
        JPanel home = new JPanel(new BorderLayout(0, 0));
        home.setBackground(ThemeManager.bg());

        // 1. Page Title Strip (North)
        JPanel titleStrip = new JPanel(new BorderLayout());
        titleStrip.setBackground(ThemeManager.bg());
        titleStrip.setBorder(new EmptyBorder(20, 26, 12, 26));

        JLabel pageTitle = new JLabel("Dashboard Overview");
        pageTitle.setFont(ThemeManager.F_TITLE);
        pageTitle.setForeground(ThemeManager.TEXT_COLOR); // Strict #000000

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        JLabel pageSub = new JLabel("Welcome back, " + loggedInUser.getUsername() + "!  •  " + sdf.format(new Date()));
        pageSub.setFont(ThemeManager.F_LABEL);
        pageSub.setForeground(ThemeManager.TEXT_COLOR); // Strict #000000

        titleStrip.add(pageTitle, BorderLayout.WEST);
        titleStrip.add(pageSub,   BorderLayout.EAST);
        home.add(titleStrip, BorderLayout.NORTH);

        // 2. Main Content Container (Center) - stretches to fill available width & height
        JPanel mainContent = new JPanel(new BorderLayout(0, 16));
        mainContent.setBackground(ThemeManager.bg());
        mainContent.setBorder(new EmptyBorder(0, 26, 20, 26));

        // Stat Cards Row (North of mainContent)
        statCardsPanel = new JPanel(new GridLayout(1, 3, 18, 0));
        statCardsPanel.setBackground(ThemeManager.bg());
        statCardsPanel.setPreferredSize(new Dimension(0, 105));

        statCardsPanel.add(ThemeManager.createStatCard(
            "👥", "Total Registered Donors", "–", ThemeManager.C_CARD_BLUE));
        statCardsPanel.add(ThemeManager.createStatCard(
            "✅", "Available Donors", "–", ThemeManager.C_CARD_GREEN));
        statCardsPanel.add(ThemeManager.createStatCard(
            "🩸", "Blood Groups", "–", ThemeManager.C_CARD_RED));

        mainContent.add(statCardsPanel, BorderLayout.NORTH);

        // Recent Donors Panel (Center of mainContent) - Expands to fill 100% available area
        JPanel recentPanel = new JPanel(new BorderLayout(0, 12));
        recentPanel.setName("CARD");
        recentPanel.setBackground(ThemeManager.card());
        recentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.border(), 1),
            new EmptyBorder(16, 20, 16, 20)
        ));

        // Header with Title + Subtitle + Refresh Button
        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);

        JPanel headerTextPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerTextPanel.setOpaque(false);

        JLabel recentTitle = new JLabel("📋 Recent Donor Registrations");
        recentTitle.setFont(ThemeManager.F_HEADING);
        recentTitle.setForeground(ThemeManager.TEXT_COLOR); // Strict #000000

        JLabel recentSub = new JLabel("(Last 10 registered donors in the system)");
        recentSub.setFont(ThemeManager.F_SMALL);
        recentSub.setForeground(ThemeManager.TEXT_COLOR); // Strict #000000

        headerTextPanel.add(recentTitle);
        headerTextPanel.add(recentSub);

        JButton btnRefreshRecent = ThemeManager.createSecondaryButton("⟳  Refresh");
        btnRefreshRecent.setPreferredSize(new Dimension(105, 32));
        btnRefreshRecent.addActionListener(e -> loadStats());

        recentHeader.add(headerTextPanel,    BorderLayout.WEST);
        recentHeader.add(btnRefreshRecent, BorderLayout.EAST);
        recentPanel.add(recentHeader, BorderLayout.NORTH);

        // ── Full-Width JTable Setup ───────────────────────────────────────────
        recentTableModel = new DefaultTableModel(TABLE_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 7; // Only the 'Details' button column is interactive
            }
        };

        recentTable = new JTable(recentTableModel);
        ThemeManager.styleTable(recentTable);
        recentTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Setup custom column widths
        recentTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Donor ID
        recentTable.getColumnModel().getColumn(1).setPreferredWidth(160); // Name
        recentTable.getColumnModel().getColumn(2).setPreferredWidth(95);  // Blood Group
        recentTable.getColumnModel().getColumn(3).setPreferredWidth(85);  // Gender
        recentTable.getColumnModel().getColumn(4).setPreferredWidth(120); // City
        recentTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Phone
        recentTable.getColumnModel().getColumn(6).setPreferredWidth(110); // Status
        recentTable.getColumnModel().getColumn(7).setPreferredWidth(130); // Details Button

        // Centered alignment for ID, Gender, Phone
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                setForeground(ThemeManager.TEXT_COLOR); // Strict #000000
                if (!sel) {
                    setBackground(row % 2 == 0 ? ThemeManager.tableRow() : ThemeManager.tableAlt());
                }
                return this;
            }
        };
        recentTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        recentTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        recentTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        // Blood Group Badge Renderer (Column 2)
        recentTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                lbl.setHorizontalAlignment(CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lbl.setForeground(ThemeManager.TEXT_COLOR); // Strict #000000
                if (!sel) {
                    lbl.setBackground(ThemeManager.ACCENT_LIGHT_COLOR);
                }
                lbl.setOpaque(true);
                return lbl;
            }
        });

        // Status Badge Renderer (Column 6)
        recentTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                lbl.setHorizontalAlignment(CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String status = val != null ? val.toString() : "";
                boolean isAvail = "Available".equalsIgnoreCase(status);
                lbl.setForeground(isAvail ? ThemeManager.SUCCESS_COLOR : ThemeManager.ERROR_COLOR);
                if (!sel) {
                    lbl.setBackground(isAvail ? ThemeManager.SUCCESS_BG_COLOR : ThemeManager.ERROR_BG_COLOR);
                }
                lbl.setOpaque(true);
                return lbl;
            }
        });

        // View Details Button Column (Column 7)
        recentTable.getColumnModel().getColumn(7).setCellRenderer(new TableCellRenderer() {
            private final JButton btn = new JButton("VIEW DETAILS");
            {
                btn.setOpaque(true);
                btn.setFont(ThemeManager.F_BTN);
                btn.setBackground(ThemeManager.primary());
                btn.setForeground(Color.WHITE);
                btn.setFocusPainted(false);
                btn.setBorder(new EmptyBorder(4, 10, 4, 10));
            }
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                return btn;
            }
        });

        recentTable.getColumnModel().getColumn(7).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private final JButton btn = new JButton("VIEW DETAILS");
            private int currentRow = -1;
            {
                btn.setOpaque(true);
                btn.setFont(ThemeManager.F_BTN);
                btn.setBackground(ThemeManager.primary());
                btn.setForeground(Color.WHITE);
                btn.setFocusPainted(false);
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn.setBorder(new EmptyBorder(4, 10, 4, 10));
                btn.addActionListener(e -> {
                    fireEditingStopped();
                    openDonorDetailsAt(currentRow);
                });
            }
            @Override
            public Component getTableCellEditorComponent(JTable t, Object val,
                    boolean sel, int row, int col) {
                currentRow = row;
                return btn;
            }
            @Override
            public Object getCellEditorValue() {
                return "VIEW DETAILS";
            }
        });

        // Double-click row shortcut to view details
        recentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = recentTable.getSelectedRow();
                    if (row >= 0) openDonorDetailsAt(row);
                }
            }
        });

        // Place table in JScrollPane occupying full center of recentPanel
        recentScrollPane = new JScrollPane(recentTable);
        recentScrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.border(), 1));
        recentScrollPane.setBackground(ThemeManager.card());
        recentScrollPane.getViewport().setBackground(ThemeManager.card());

        // Empty message placeholder
        lblNoDonors = new JLabel("No donor records found. Click 'Register Donor' to add one.", SwingConstants.CENTER);
        lblNoDonors.setFont(ThemeManager.F_LABEL);
        lblNoDonors.setForeground(ThemeManager.TEXT_COLOR); // Strict #000000

        recentPanel.add(recentScrollPane, BorderLayout.CENTER);
        mainContent.add(recentPanel,      BorderLayout.CENTER);
        home.add(mainContent,             BorderLayout.CENTER);

        return home;
    }

    private void openDonorDetailsAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < recentDonorsList.size()) {
            Donor donor = recentDonorsList.get(rowIndex);
            new DonorDetailsFrame(this, donor).setVisible(true);
        } else if (rowIndex >= 0 && rowIndex < recentTableModel.getRowCount()) {
            String donorId = (String) recentTableModel.getValueAt(rowIndex, 0);
            try {
                Donor donor = donorService.getDonorById(donorId);
                if (donor != null) {
                    new DonorDetailsFrame(this, donor).setVisible(true);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Failed to load donor details: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Stats & Recent Donors Loading ─────────────────────────────────────────

    public void loadStats() {
        SwingWorker<int[], Void> worker = new SwingWorker<>() {
            private List<Donor> recent;
            private int bloodGroupCount;

            @Override
            protected int[] doInBackground() throws Exception {
                int total       = donorService.getTotalDonors();
                int available   = donorService.getAvailableDonors();
                recent          = donorService.getRecentDonors();
                bloodGroupCount = donorService.getBloodGroupCount();
                return new int[]{total, available, bloodGroupCount};
            }

            @Override
            protected void done() {
                try {
                    int[] stats = get();
                    updateStatCard(statCardsPanel, 0, String.valueOf(stats[0]));
                    updateStatCard(statCardsPanel, 1, String.valueOf(stats[1]));
                    updateStatCard(statCardsPanel, 2, String.valueOf(stats[2]));

                    recentDonorsList.clear();
                    recentTableModel.setRowCount(0);

                    if (recent != null && !recent.isEmpty()) {
                        recentDonorsList.addAll(recent);
                        for (Donor d : recent) {
                            recentTableModel.addRow(new Object[]{
                                d.getDonorId(),
                                d.getDonorName(),
                                d.getBloodGroup(),
                                d.getGender(),
                                d.getCity(),
                                d.getPhone(),
                                d.getStatus(),
                                "VIEW DETAILS"
                            });
                        }
                        recentScrollPane.setViewportView(recentTable);
                    } else {
                        recentScrollPane.setViewportView(lblNoDonors);
                    }
                } catch (Exception ex) {
                    recentTableModel.setRowCount(0);
                    lblNoDonors.setText("Error loading data: " + ex.getMessage());
                    recentScrollPane.setViewportView(lblNoDonors);
                }
            }
        };
        worker.execute();
    }

    private void updateStatCard(JPanel cardsPanel, int index, String value) {
        if (cardsPanel == null || index >= cardsPanel.getComponentCount()) return;
        Component card = cardsPanel.getComponent(index);
        if (card instanceof JPanel p) {
            for (Component child : p.getComponents()) {
                if (child instanceof JPanel content) {
                    for (Component lbl : content.getComponents()) {
                        if (lbl instanceof JLabel jl && jl.getFont().getSize() == 28) {
                            jl.setText(value);
                            jl.setForeground(ThemeManager.TEXT_COLOR); // Strict #000000
                            break;
                        }
                    }
                }
            }
        }
    }

    // ── Navigation Control ────────────────────────────────────────────────────

    private void showCard(String name, int navIndex) {
        currentNavIndex = navIndex;
        cardLayout.show(contentPanel, name);
        updateNavHighlights();
    }

    private void updateNavHighlights() {
        for (int i = 0; i < navItems.length; i++) {
            if (i == currentNavIndex) {
                navItems[i].setBackground(ThemeManager.sidebarSel());
                navItems[i].setForeground(Color.WHITE);
                navItems[i].setFont(new Font("Segoe UI", Font.BOLD, 14));
            } else {
                navItems[i].setBackground(ThemeManager.sidebar());
                navItems[i].setForeground(ThemeManager.sidebarText());
                navItems[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }
        }
    }

    public void openUpdateForm(Donor donor) {
        DonorUpdateFrame updateFrame = new DonorUpdateFrame(donor, () -> {
            listPanel.refresh();
            loadStats();
            showCard("VIEW", 2);
        });
        updateFrame.setName("UPDATE");
        Component old = getCardPanel("UPDATE");
        if (old != null) contentPanel.remove(old);
        contentPanel.add(updateFrame, "UPDATE");
        contentPanel.revalidate();
        showCard("UPDATE", 4);
    }

    private Component getCardPanel(String name) {
        for (Component comp : contentPanel.getComponents()) {
            if (name.equals(comp.getName())) return comp;
        }
        return null;
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    private void doLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }

    // ── Entry Point ───────────────────────────────────────────────────────────

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new DashboardFrame().setVisible(true));
    }
}
