package gui;

import exception.DatabaseException;
import exception.ValidationException;
import model.Donor;
import service.DonorService;
import util.ThemeManager;
import util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DonorRegistrationFrame – embedded panel for registering a new donor.
 * Demonstrates: GridBagLayout, input validation, JDBC INSERT via service.
 */
public class DonorRegistrationFrame extends JPanel {

    // ── Form fields ───────────────────────────────────────────────────────────
    private JTextField  txtDonorId;
    private JTextField  txtDonorName;
    private JTextField  txtAge;
    private JComboBox<String> cmbGender;
    private JComboBox<String> cmbBloodGroup;
    private JTextField  txtPhone;
    private JTextField  txtEmail;
    private JTextField  txtAddress;
    private JTextField  txtCity;
    private JTextField  txtLastDonation;
    private JComboBox<String> cmbStatus;

    private final DonorService donorService = new DonorService();
    private final Runnable onSuccess;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * @param onSuccess callback executed after a successful registration
     *                  (dashboard reloads stats and refreshes table)
     */
    public DonorRegistrationFrame(Runnable onSuccess) {
        this.onSuccess = onSuccess;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.bg());
        buildUI();
    }

    // ── UI build ──────────────────────────────────────────────────────────────

    private void buildUI() {
        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(ThemeManager.bg());
        titleBar.setBorder(new EmptyBorder(20, 24, 8, 24));

        JLabel title = new JLabel("Register New Donor");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ThemeManager.text());

        JLabel sub = new JLabel("Fields marked with * are required");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(ThemeManager.textMuted());

        titleBar.add(title, BorderLayout.WEST);
        titleBar.add(sub,   BorderLayout.EAST);
        add(titleBar, BorderLayout.NORTH);

        // Scrollable form
        JScrollPane scroll = new JScrollPane(buildForm());
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(ThemeManager.bg());
        scroll.getViewport().setBackground(ThemeManager.bg());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Button bar
        add(buildButtonBar(), BorderLayout.SOUTH);
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(ThemeManager.bg());
        form.setBorder(new EmptyBorder(10, 24, 10, 24));

        GridBagConstraints lc = labelConstraints();
        GridBagConstraints fc = fieldConstraints();

        int row = 0;

        // Donor ID
        addRow(form, lc, fc, row++, "Donor ID *", txtDonorId = field());

        // Donor Name
        addRow(form, lc, fc, row++, "Donor Name *", txtDonorName = field());

        // Age
        addRow(form, lc, fc, row++, "Age * (18–65)", txtAge = field());

        // Gender
        cmbGender = ThemeManager.createStyledComboBox(new String[]{"Male", "Female", "Other"});
        addRow(form, lc, fc, row++, "Gender *", cmbGender);

        // Blood Group
        cmbBloodGroup = ThemeManager.createStyledComboBox(
            new String[]{"A+","A-","B+","B-","AB+","AB-","O+","O-"});
        addRow(form, lc, fc, row++, "Blood Group *", cmbBloodGroup);

        // Phone – 10-digit real-time filter
        txtPhone = field();
        ThemeManager.applyDigitOnlyFilter(txtPhone, 10);
        txtPhone.setToolTipText("Enter exactly 10 digits. Example: 9876543210");
        addRow(form, lc, fc, row++, "Phone * (10 digits)", txtPhone);

        // Email
        addRow(form, lc, fc, row++, "Email", txtEmail = field());

        // Address
        addRow(form, lc, fc, row++, "Address", txtAddress = field());

        // City (mandatory – marked with asterisk and hint)
        JPanel cityPanel = new JPanel(new BorderLayout(4, 0));
        cityPanel.setBackground(ThemeManager.bg());
        txtCity = field();
        JLabel cityHint = new JLabel("Required");
        cityHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        cityHint.setForeground(ThemeManager.danger());
        cityPanel.add(txtCity,  BorderLayout.CENTER);
        cityPanel.add(cityHint, BorderLayout.EAST);
        addRow(form, lc, fc, row++, "City *", cityPanel);

        // Last Donation Date
        JPanel datePanel = new JPanel(new BorderLayout(4, 0));
        datePanel.setBackground(ThemeManager.bg());
        txtLastDonation = field();
        txtLastDonation.setToolTipText("Format: yyyy-MM-dd");
        JButton btnToday = ThemeManager.createSecondaryButton("Today");
        btnToday.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnToday.addActionListener(e ->
            txtLastDonation.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
        datePanel.add(txtLastDonation, BorderLayout.CENTER);
        datePanel.add(btnToday,        BorderLayout.EAST);
        addRow(form, lc, fc, row++, "Last Donation (yyyy-MM-dd)", datePanel);

        // Status
        cmbStatus = ThemeManager.createStyledComboBox(new String[]{"Available", "Not Available"});
        addRow(form, lc, fc, row, "Status *", cmbStatus);

        return form;
    }

    private JPanel buildButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bar.setBackground(ThemeManager.bg());
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeManager.border()));

        JButton btnClear    = ThemeManager.createSecondaryButton("Clear Form");
        JButton btnRegister = ThemeManager.createAccentButton("Register Donor");

        btnClear   .addActionListener(e -> clearForm());
        btnRegister.addActionListener(e -> doRegister());

        bar.add(btnClear);
        bar.add(btnRegister);
        return bar;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JTextField field() {
        return ThemeManager.createStyledTextField(22);
    }

    private GridBagConstraints labelConstraints() {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx   = 0;
        gc.anchor  = GridBagConstraints.WEST;
        gc.insets  = new Insets(6, 0, 6, 14);
        gc.fill    = GridBagConstraints.NONE;
        gc.weightx = 0.0;
        return gc;
    }

    private GridBagConstraints fieldConstraints() {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx   = 1;
        gc.fill    = GridBagConstraints.HORIZONTAL;
        gc.insets  = new Insets(6, 0, 6, 0);
        gc.weightx = 1.0;
        return gc;
    }

    private void addRow(JPanel form, GridBagConstraints lc, GridBagConstraints fc,
                        int row, String label, Component field) {
        lc.gridy = row;
        fc.gridy = row;

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(ThemeManager.C_TEXT);   // BLACK text
        lbl.setPreferredSize(new Dimension(220, 28));

        form.add(lbl,   lc);
        form.add(field, fc);
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void doRegister() {
        try {
            Donor donor = buildDonorFromForm();
            donorService.registerDonor(donor);

            JOptionPane.showMessageDialog(this,
                "Donor '" + donor.getDonorName() + "' registered successfully!\nID: " + donor.getDonorId(),
                "Registration Successful",
                JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            if (onSuccess != null) onSuccess.run();

        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this,
                ve.getMessage(),
                "Validation Error – " + (ve.getFieldName() != null ? ve.getFieldName() : "Input"),
                JOptionPane.WARNING_MESSAGE);

        } catch (DatabaseException de) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + de.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private Donor buildDonorFromForm() throws ValidationException {
        String id         = txtDonorId.getText().trim();
        String name       = txtDonorName.getText().trim();
        String ageText    = txtAge.getText().trim();
        String gender     = (String) cmbGender.getSelectedItem();
        String bloodGroup = (String) cmbBloodGroup.getSelectedItem();
        String phone      = txtPhone.getText().trim();
        String email      = txtEmail.getText().trim();
        String address    = txtAddress.getText().trim();
        String city       = txtCity.getText().trim();
        String dateText   = txtLastDonation.getText().trim();
        String status     = (String) cmbStatus.getSelectedItem();

        int age = ValidationUtil.parseAge(ageText);

        // Validation is also performed in DonorService, but we validate city early
        // so the user gets the correct "Please enter the city." message.
        if (city.isEmpty()) {
            throw new ValidationException("City", "Please enter the city.");
        }

        Date donationDate = ValidationUtil.parseDate(dateText);

        return new Donor(id, name, age, gender, bloodGroup,
                         phone, email.isEmpty() ? null : email,
                         address.isEmpty() ? null : address,
                         city, donationDate, status);
    }

    private void clearForm() {
        txtDonorId.setText("");
        txtDonorName.setText("");
        txtAge.setText("");
        cmbGender.setSelectedIndex(0);
        cmbBloodGroup.setSelectedIndex(0);
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        txtCity.setText("");
        txtLastDonation.setText("");
        cmbStatus.setSelectedIndex(0);
        txtDonorId.requestFocus();
    }
}
