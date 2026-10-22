package gui;

import model.Donor;
import service.DonorService;
import util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * SearchDonorFrame – multi-criteria donor search panel.
 * Demonstrates: JTable, dynamic SQL via DAO, FlowLayout/GridBagLayout,
 *               event handling (Search button + Enter key).
 */
public class SearchDonorFrame extends JPanel {

    // ── Search fields ─────────────────────────────────────────────────────────
    private JTextField    txtDonorId;
    private JTextField    txtDonorName;
    private JComboBox<String> cmbBloodGroup;
    private JTextField    txtCity;
    private JTextField    txtPhone;
    private JComboBox<String> cmbStatus;

    // ── Results table ─────────────────────────────────────────────────────────
    private JTable            table;
    private DefaultTableModel tableModel;
    private JLabel            lblResults;

    // ── Buttons ───────────────────────────────────────────────────────────────
    private JButton btnSearch;
    private JButton btnClear;
    private JButton btnViewDetails;
    private JButton btnUpdate;
    private JButton btnDelete;

    private final DonorService   donorService = new DonorService();
    private final DashboardFrame dashboard;

    private static final String[] COLUMNS = {
        "Donor ID", "Name", "Age", "Blood Group", "Phone", "City", "Status"
    };

    // ── Constructor ───────────────────────────────────────────────────────────

    public SearchDonorFrame(DashboardFrame dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.bg());
        buildUI();
    }

    // ── UI ────────────────────────────────────────────────────────────────────

    private void buildUI() {
        add(buildTitleBar(),  BorderLayout.NORTH);
        add(buildSearchForm(), BorderLayout.CENTER);
    }

    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(ThemeManager.bg());
        bar.setBorder(new EmptyBorder(18, 24, 8, 24));

        JLabel title = new JLabel("Search Donors");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ThemeManager.text());
        bar.add(title, BorderLayout.WEST);
        return bar;
    }

    private JPanel buildSearchForm() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 0));
        wrapper.setBackground(ThemeManager.bg());

        // ── Criteria panel ────────────────────────────────────────────────────
        JPanel criteria = new JPanel(new GridBagLayout());
        criteria.setBackground(ThemeManager.panel());
        criteria.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 24, 0, 24, ThemeManager.bg()),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border(), 1),
                new EmptyBorder(16, 20, 12, 20))));

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor  = GridBagConstraints.WEST;
        lc.insets  = new Insets(5, 0, 5, 10);
        lc.weightx = 0;

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill    = GridBagConstraints.HORIZONTAL;
        fc.insets  = new Insets(5, 0, 5, 18);
        fc.weightx = 1.0;

        // Row 0: Donor ID | Donor Name
        addCriteriaField(criteria, lc, fc, 0, 0, "Donor ID",   txtDonorId   = smallField());
        addCriteriaField(criteria, lc, fc, 0, 2, "Donor Name", txtDonorName = smallField());

        // Row 1: Blood Group | City
        cmbBloodGroup = ThemeManager.createStyledComboBox(
            new String[]{"(Any)","A+","A-","B+","B-","AB+","AB-","O+","O-"});
        addCriteriaField(criteria, lc, fc, 1, 0, "Blood Group", cmbBloodGroup);
        addCriteriaField(criteria, lc, fc, 1, 2, "City",        txtCity = smallField());

        // Row 2: Phone | Status
        cmbStatus = ThemeManager.createStyledComboBox(
            new String[]{"(Any)", "Available", "Not Available"});
        txtPhone = smallField();
        ThemeManager.applyDigitOnlyFilter(txtPhone, 10);
        addCriteriaField(criteria, lc, fc, 2, 0, "Phone",  txtPhone);
        addCriteriaField(criteria, lc, fc, 2, 2, "Status", cmbStatus);

        // Search / Clear buttons (row 3, span)
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        btnRow.setBackground(ThemeManager.panel());
        btnSearch = ThemeManager.createAccentButton("🔍  Search");
        btnClear  = ThemeManager.createSecondaryButton("Clear");
        btnSearch.setPreferredSize(new Dimension(130, 34));
        btnClear.setPreferredSize(new Dimension(90, 34));
        btnRow.add(btnSearch);
        btnRow.add(btnClear);

        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx      = 0; bc.gridy = 3;
        bc.gridwidth  = 4;
        bc.fill       = GridBagConstraints.HORIZONTAL;
        bc.insets     = new Insets(8, 0, 4, 0);
        criteria.add(btnRow, bc);

        wrapper.add(criteria, BorderLayout.NORTH);

        // ── Results area ──────────────────────────────────────────────────────
        JPanel resultsArea = new JPanel(new BorderLayout());
        resultsArea.setBackground(ThemeManager.bg());
        resultsArea.setBorder(new EmptyBorder(14, 24, 8, 24));

        // Results header
        JPanel resHeader = new JPanel(new BorderLayout());
        resHeader.setBackground(ThemeManager.bg());

        JLabel resTitle = new JLabel("Search Results");
        resTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        resTitle.setForeground(ThemeManager.text());

        lblResults = new JLabel("Enter criteria above and click Search.");
        lblResults.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblResults.setForeground(ThemeManager.textMuted());

        resHeader.add(resTitle,   BorderLayout.WEST);
        resHeader.add(lblResults, BorderLayout.EAST);
        resultsArea.add(resHeader, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(ThemeManager.bg());
        scroll.getViewport().setBackground(ThemeManager.tableRow());
        scroll.setBorder(BorderFactory.createLineBorder(ThemeManager.border(), 1));
        resultsArea.add(scroll, BorderLayout.CENTER);

        // Results buttons
        JPanel resButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        resButtons.setBackground(ThemeManager.bg());

        btnViewDetails = ThemeManager.createSecondaryButton("👁  View Details");
        btnUpdate      = ThemeManager.createAccentButton("✏  Update");
        btnDelete      = ThemeManager.createDangerButton("🗑  Delete");

        resButtons.add(btnViewDetails);
        resButtons.add(btnUpdate);
        resButtons.add(btnDelete);
        resultsArea.add(resButtons, BorderLayout.SOUTH);

        wrapper.add(resultsArea, BorderLayout.CENTER);

        attachListeners();
        return wrapper;
    }

    private void addCriteriaField(JPanel panel, GridBagConstraints lc, GridBagConstraints fc,
                                  int row, int col, String label, Component comp) {
        lc.gridx = col;
        lc.gridy = row;
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(ThemeManager.text());
        panel.add(lbl, lc);

        fc.gridx = col + 1;
        fc.gridy = row;
        panel.add(comp, fc);
    }

    private JTextField smallField() {
        return ThemeManager.createStyledTextField(14);
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

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(ThemeManager.tableHeader());
        header.setForeground(ThemeManager.TEXT_COLOR);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 36));

        // Status renderer
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

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) doViewDetails();
            }
        });
    }

    private void attachListeners() {
        btnSearch.addActionListener(e -> doSearch());
        btnClear.addActionListener(e -> clearFields());

        btnViewDetails.addActionListener(e -> doViewDetails());
        btnUpdate.addActionListener(e -> doUpdate());
        btnDelete.addActionListener(e -> doDelete());

        java.awt.event.KeyAdapter enter = new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) doSearch();
            }
        };
        txtDonorId.addKeyListener(enter);
        txtDonorName.addKeyListener(enter);
        txtCity.addKeyListener(enter);
        txtPhone.addKeyListener(enter);
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void doSearch() {
        String id     = txtDonorId.getText().trim();
        String name   = txtDonorName.getText().trim();
        String bg     = (String) cmbBloodGroup.getSelectedItem();
        String city   = txtCity.getText().trim();
        String phone  = txtPhone.getText().trim();
        String status = (String) cmbStatus.getSelectedItem();

        if ("(Any)".equals(bg))     bg     = null;
        if ("(Any)".equals(status)) status = null;

        btnSearch.setEnabled(false);
        lblResults.setText("Searching…");

        final String fId = id, fName = name, fBg = bg, fCity = city, fPhone = phone, fStatus = status;

        SwingWorker<List<Donor>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Donor> doInBackground() throws Exception {
                return donorService.searchDonors(fId, fName, fBg, fCity, fPhone, fStatus);
            }

            @Override
            protected void done() {
                try {
                    List<Donor> results = get();
                    tableModel.setRowCount(0);
                    for (Donor d : results) {
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
                    lblResults.setText(results.size() + " donor" + (results.size() == 1 ? "" : "s") + " found");
                } catch (Exception e) {
                    lblResults.setText("Search failed.");
                    JOptionPane.showMessageDialog(SearchDonorFrame.this,
                        "Search error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnSearch.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void clearFields() {
        txtDonorId.setText("");
        txtDonorName.setText("");
        cmbBloodGroup.setSelectedIndex(0);
        txtCity.setText("");
        txtPhone.setText("");
        cmbStatus.setSelectedIndex(0);
        tableModel.setRowCount(0);
        lblResults.setText("Enter criteria above and click Search.");
        txtDonorId.requestFocus();
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
                lblResults.setText(tableModel.getRowCount() + " donor" + (tableModel.getRowCount() == 1 ? "" : "s") + " found");
                if (dashboard != null) dashboard.loadStats();

                JOptionPane.showMessageDialog(this,
                    "Donor deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Failed to delete donor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                "Failed to get donor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
