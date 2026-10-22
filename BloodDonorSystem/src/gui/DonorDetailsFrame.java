package gui;

import model.Donor;
import util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * DonorDetailsFrame – Modal dialog displaying complete donor record in clean, professional sections.
 * Strictly adheres to Requirement 8, 10, 21:
 * - Shows all fields from actual Oracle/database schema
 * - Pure #000000 (Black) text for all normal text and details
 * - Uses centralized ThemeManager styling
 */
public class DonorDetailsFrame extends JDialog {

    private final Donor donor;

    public DonorDetailsFrame(Donor donor) {
        this(null, donor);
    }

    public DonorDetailsFrame(Window owner, Donor donor) {
        super(owner, "Donor Details – " + (donor != null ? donor.getDonorId() : ""), ModalityType.APPLICATION_MODAL);
        this.donor = donor;
        setSize(540, 640);
        setLocationRelativeTo(owner);
        setResizable(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        buildUI();
    }

    private void buildUI() {
        if (donor == null) return;

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(ThemeManager.panel());

        // ── Top Header Strip ──────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setName("HEADER");
        header.setBackground(ThemeManager.header());
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titlePanel.setOpaque(false);

        JLabel logoIcon = new JLabel("🩸");
        logoIcon.putClientProperty("preserveColor", Boolean.TRUE);
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));

        JLabel titleLbl = new JLabel("DONOR DETAILS");
        titleLbl.putClientProperty("preserveColor", Boolean.TRUE);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(Color.WHITE);

        titlePanel.add(logoIcon);
        titlePanel.add(titleLbl);

        JLabel idBadge = new JLabel("ID: " + donor.getDonorId());
        idBadge.putClientProperty("preserveColor", Boolean.TRUE);
        idBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        idBadge.setForeground(ThemeManager.SIDEBAR_BRAND_TITLE);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(idBadge,    BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // ── Scrollable Body Content ───────────────────────────────────────────
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(ThemeManager.panel());
        body.setBorder(new EmptyBorder(16, 24, 16, 24));

        // Format dates
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String donationDateStr = donor.getLastDonationDate() != null
                ? sdf.format(donor.getLastDonationDate())
                : "Not recorded";

        // Section 1: Personal Information
        body.add(buildSectionHeader("PERSONAL INFORMATION"));
        body.add(Box.createVerticalStrut(6));
        body.add(buildFieldRow("Donor ID", donor.getDonorId()));
        body.add(buildFieldRow("Donor Name", donor.getDonorName()));
        body.add(buildFieldRow("Gender", donor.getGender()));
        body.add(buildFieldRow("Age", donor.getAge() + " years"));
        body.add(Box.createVerticalStrut(14));

        // Section 2: Blood Information
        body.add(buildSectionHeader("BLOOD INFORMATION"));
        body.add(Box.createVerticalStrut(6));
        body.add(buildBloodGroupRow(donor.getBloodGroup()));
        body.add(Box.createVerticalStrut(14));

        // Section 3: Contact Information
        body.add(buildSectionHeader("CONTACT INFORMATION"));
        body.add(Box.createVerticalStrut(6));
        body.add(buildFieldRow("Phone", donor.getPhone()));
        body.add(buildFieldRow("Email", (donor.getEmail() != null && !donor.getEmail().isEmpty()) ? donor.getEmail() : "—"));
        body.add(Box.createVerticalStrut(14));

        // Section 4: Address Information
        body.add(buildSectionHeader("ADDRESS INFORMATION"));
        body.add(Box.createVerticalStrut(6));
        body.add(buildFieldRow("Address", (donor.getAddress() != null && !donor.getAddress().isEmpty()) ? donor.getAddress() : "—"));
        body.add(buildFieldRow("City", donor.getCity()));
        body.add(Box.createVerticalStrut(14));

        // Section 5: Donation & Status Information
        body.add(buildSectionHeader("DONATION & STATUS INFORMATION"));
        body.add(Box.createVerticalStrut(6));
        body.add(buildFieldRow("Last Donation", donationDateStr));
        body.add(buildStatusRow(donor.getStatus()));

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.setBackground(ThemeManager.panel());
        scroll.getViewport().setBackground(ThemeManager.panel());
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        root.add(scroll, BorderLayout.CENTER);

        // ── Footer Button Bar ─────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 14));
        footer.setBackground(ThemeManager.panel());
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeManager.border()));

        JButton btnClose = ThemeManager.createSecondaryButton("Close");
        btnClose.setPreferredSize(new Dimension(100, 36));
        btnClose.addActionListener(e -> dispose());
        footer.add(btnClose);

        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel buildSectionHeader(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(ThemeManager.panel());
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLbl.setForeground(ThemeManager.TEXT_COLOR); // Strict #000000

        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeManager.border());

        p.add(titleLbl, BorderLayout.NORTH);
        p.add(sep,      BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildFieldRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(ThemeManager.panel());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(ThemeManager.TEXT_COLOR); // Strict #000000
        lbl.setPreferredSize(new Dimension(140, 24));

        JLabel val = new JLabel(value != null ? value : "—");
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(ThemeManager.TEXT_COLOR); // Strict #000000

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        return row;
    }

    private JPanel buildBloodGroupRow(String bloodGroup) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(ThemeManager.panel());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lbl = new JLabel("Blood Group:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(ThemeManager.TEXT_COLOR); // Strict #000000
        lbl.setPreferredSize(new Dimension(140, 26));

        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgePanel.setOpaque(false);

        JLabel bgBadge = new JLabel(bloodGroup != null ? bloodGroup : "—");
        bgBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bgBadge.setForeground(ThemeManager.TEXT_COLOR); // Strict #000000
        bgBadge.setOpaque(true);
        bgBadge.setBackground(ThemeManager.ACCENT_LIGHT_COLOR);
        bgBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xBFDBFE), 1),
            new EmptyBorder(3, 12, 3, 12)
        ));

        badgePanel.add(bgBadge);
        row.add(lbl, BorderLayout.WEST);
        row.add(badgePanel, BorderLayout.CENTER);
        return row;
    }

    private JPanel buildStatusRow(String status) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(ThemeManager.panel());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lbl = new JLabel("Status:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(ThemeManager.TEXT_COLOR); // Strict #000000
        lbl.setPreferredSize(new Dimension(140, 26));

        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgePanel.setOpaque(false);

        boolean isAvail = "Available".equalsIgnoreCase(status);
        JLabel statusBadge = new JLabel(status != null ? status : "—");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusBadge.setForeground(isAvail ? ThemeManager.SUCCESS_COLOR : ThemeManager.ERROR_COLOR);
        statusBadge.setOpaque(true);
        statusBadge.setBackground(isAvail ? ThemeManager.SUCCESS_BG_COLOR : ThemeManager.ERROR_BG_COLOR);
        statusBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(isAvail ? new Color(0x86EFAC) : new Color(0xFCA5A5), 1),
            new EmptyBorder(3, 10, 3, 10)
        ));

        badgePanel.add(statusBadge);
        row.add(lbl, BorderLayout.WEST);
        row.add(badgePanel, BorderLayout.CENTER);
        return row;
    }
}
