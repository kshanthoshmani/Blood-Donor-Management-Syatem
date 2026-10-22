package gui;

import model.Donor;
import service.DonorService;
import util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * DonorListFrame – JTable view of all donors.
 * Provides: Refresh, View Details, Update, Delete buttons.
 * Demonstrates: JTable, DefaultTableModel, JDBC SELECT, event handling.
 */
public class DonorListFrame extends JPanel {

    // ── Table ─────────────────────────────────────────────────────────────────
    private JTable           table;
    private DefaultTableModel tableModel;

    // ── Buttons ───────────────────────────────────────────────────────────────
    private JButton btnRefresh;
    private JButton btnDetails;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JLabel  lblCount;

    private final DonorService    donorService = new DonorService();
    private final DashboardFrame  dashboard;

    private static final String[] COLUMNS = {
        "Donor ID", "Name", "Age", "Blood Group", "Phone", "City", "Status"
    };

    // ── Constructor ───────────────────────────────────────────────────────────

    public DonorListFrame(DashboardFrame dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.bg());
        buildUI();
        refresh();
    }

    // ── UI build ──────────────────────────────────────────────────────────────

    private void buildUI() {
        // ── Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(ThemeManager.bg());
        titleBar.setBorder(new EmptyBorder(18, 24, 10, 24));

        JLabel title = new JLabel("Donor List");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ThemeManager.text());

        lblCount = new JLabel("Loading…");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCount.setForeground(ThemeManager.textMuted());

        titleBar.add(title,    BorderLayout.WEST);
        titleBar.add(lblCount, BorderLayout.EAST);
        add(titleBar, BorderLayout.NORTH);

        // ── Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        styleTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(ThemeManager.bg());
        scroll.getViewport().setBackground(ThemeManager.tableRow());
        scroll.setBorder(BorderFactory.createMatteBorder(0, 24, 0, 24, ThemeManager.bg()));
        add(scroll, BorderLayout.CENTER);

        // ── Button bar
        add(buildButtonBar(), BorderLayout.SOUTH);
    }

    private void styleTable() {
        table.setRowHeight(36);
        table.setFont(ThemeManager.F_TABLE);
        table.setShowGrid(true);
        table.setGridColor(ThemeManager.border());
        table.setBackground(ThemeManager.tableRow());
        table.setForeground(ThemeManager.TEXT_COLOR);
        table.setSelectionBackground(ThemeManager.tableSel());
        table.setSelectionForeground(ThemeManager.TEXT_COLOR);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(ThemeManager.tableHeader());
        header.setForeground(ThemeManager.TEXT_COLOR);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 36));

        // Column widths
        int[] widths = {80, 160, 50, 90, 110, 110, 100};
        for (int i = 0; i < widths.length && i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Status column: colour cells
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean selected, boolean focused, int row, int col) {
                super.getTableCellRendererComponent(t, val, selected, focused, row, col);
                setHorizontalAlignment(CENTER);
                String status = val == null ? "" : val.toString();
                boolean isAvail = "Available".equalsIgnoreCase(status);
                setForeground(isAvail ? ThemeManager.SUCCESS_COLOR : ThemeManager.ERROR_COLOR);
                if (!selected) {
                    setBackground(isAvail ? ThemeManager.SUCCESS_BG_COLOR : ThemeManager.ERROR_BG_COLOR);
                }
                return this;
            }
        });

        // Alternate row colours with black text
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean selected, boolean focused, int row, int col) {
                super.getTableCellRendererComponent(t, val, selected, focused, row, col);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                if (!selected) {
                    setBackground(row % 2 == 0 ? ThemeManager.tableRow() : ThemeManager.tableAlt());
                    setForeground(ThemeManager.TEXT_COLOR);
                }
                // Re-apply status colour if this is column 6
                if (col == 6) {
                    String status = val == null ? "" : val.toString();
                    boolean isAvail = "Available".equalsIgnoreCase(status);
                    setForeground(isAvail ? ThemeManager.SUCCESS_COLOR : ThemeManager.ERROR_COLOR);
                    if (!selected) {
                        setBackground(isAvail ? ThemeManager.SUCCESS_BG_COLOR : ThemeManager.ERROR_BG_COLOR);
                    }
                }
                return this;
            }
        });

        // Double-click row shortcut to view details
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) doViewDetails();
            }
        });
    }

    private JPanel buildButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bar.setBackground(ThemeManager.bg());
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeManager.border()));

        btnRefresh = ThemeManager.createSecondaryButton("⟳  Refresh");
        btnDetails = ThemeManager.createSecondaryButton("👁  View Details");
        btnUpdate  = ThemeManager.createAccentButton("✏  Update");
        btnDelete  = ThemeManager.createDangerButton("🗑  Delete");

        btnRefresh.addActionListener(e -> refresh());
        btnDetails.addActionListener(e -> doViewDetails());
        btnUpdate .addActionListener(e -> doUpdate());
        btnDelete .addActionListener(e -> doDelete());

        bar.add(btnRefresh);
        bar.add(btnDetails);
        bar.add(btnUpdate);
        bar.add(btnDelete);
        return bar;
    }

    // ── Data operations ───────────────────────────────────────────────────────

    public void refresh() {
        btnRefresh.setEnabled(false);
        lblCount.setText("Loading…");

        SwingWorker<List<Donor>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Donor> doInBackground() throws Exception {
                return donorService.getAllDonors();
            }

            @Override
            protected void done() {
                try {
                    List<Donor> donors = get();
                    tableModel.setRowCount(0);
                    for (Donor d : donors) {
                        tableModel.addRow(new Object[]{
                            d.getDonorId(),
                            d.getDonorName(),
                            d.getAge(),
                            d.getBloodGroup(),
                            d.getPhone(),
                            d.getCity(),
                            d.getStatus()
                        });
                    }
                    lblCount.setText(donors.size() + " donor" + (donors.size() == 1 ? "" : "s"));
                } catch (Exception e) {
                    lblCount.setText("Failed to load donors.");
                    JOptionPane.showMessageDialog(DonorListFrame.this,
                        "Error loading donors: " + e.getMessage(),
                        "Load Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnRefresh.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void doViewDetails() {
        Donor donor = getSelectedDonor();
        if (donor != null) {
            new DonorDetailsFrame(donor).setVisible(true);
        }
    }

    private void doUpdate() {
        Donor donor = getSelectedDonor();
        if (donor != null && dashboard != null) {
            dashboard.openUpdateForm(donor);
        }
    }

    private void doDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a donor to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String donorId   = (String) tableModel.getValueAt(row, 0);
        String donorName = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete donor '" + donorName + "' (" + donorId + ")?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                donorService.deleteDonor(donorId);
                tableModel.removeRow(row);
                lblCount.setText(tableModel.getRowCount() + " donor" + (tableModel.getRowCount() == 1 ? "" : "s"));
                if (dashboard != null) dashboard.loadStats();

                JOptionPane.showMessageDialog(this,
                    "Donor '" + donorName + "' deleted successfully.",
                    "Deleted", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Failed to delete donor: " + ex.getMessage(),
                    "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Donor getSelectedDonor() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a donor from the table.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String donorId = (String) tableModel.getValueAt(row, 0);
        try {
            return donorService.getDonorById(donorId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Failed to get donor: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
