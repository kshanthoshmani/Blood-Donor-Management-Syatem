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
 * DonorUpdateFrame – embedded panel for editing an existing donor record.
 * Pre-populated from the selected Donor object.
 * Demonstrates: JDBC UPDATE, input validation, event handling.
 */
public class DonorUpdateFrame extends JPanel {

    // ── Form fields ───────────────────────────────────────────────────────────
    private JTextField    txtDonorId;          // read-only
    private JTextField    txtDonorName;
    private JTextField    txtAge;
    private JComboBox<String> cmbGender;
    private JComboBox<String> cmbBloodGroup;
    private JTextField    txtPhone;
    private JTextField    txtEmail;
    private JTextField    txtAddress;
    private JTextField    txtCity;
    private JTextField    txtLastDonation;
    private JComboBox<String> cmbStatus;

    private final Donor         currentDonor;
    private final DonorService  donorService = new DonorService();
    private final Runnable      onSuccess;

    // ── Constructor ───────────────────────────────────────────────────────────

    public DonorUpdateFrame(Donor donor, Runnable onSuccess) {
        this.currentDonor = donor;
        this.onSuccess    = onSuccess;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.bg());
        buildUI();
        populateForm();
    }

    // ── UI build ──────────────────────────────────────────────────────────────

    private void buildUI() {
        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(ThemeManager.bg());
        titleBar.setBorder(new EmptyBorder(20, 24, 8, 24));

        JLabel title = new JLabel("Update Donor Information");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ThemeManager.text());

        JLabel id = new JLabel("Donor ID: " + currentDonor.getDonorId());
        id.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        id.setForeground(ThemeManager.accent());

        titleBar.add(title, BorderLayout.WEST);
        titleBar.add(id,    BorderLayout.EAST);
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

        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0; lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(6, 0, 6, 14); lc.weightx = 0;

        GridBagConstraints fc = new GridBagConstraints();
        fc.gridx = 1; fc.fill = GridBagConstraints.HORIZONTAL;
        fc.insets = new Insets(6, 0, 6, 0); fc.weightx = 1.0;

        int row = 0;

        // Donor ID (read-only)
        txtDonorId = ThemeManager.createStyledTextField(22);
        txtDonorId.setEditable(false);
        txtDonorId.setBackground(ThemeManager.card());
        addRow(form, lc, fc, row++, "Donor ID (read-only)", txtDonorId);

        addRow(form, lc, fc, row++, "Donor Name *", txtDonorName = field());
        addRow(form, lc, fc, row++, "Age * (18–65)", txtAge = field());

        cmbGender = ThemeManager.createStyledComboBox(new String[]{"Male","Female","Other"});
        addRow(form, lc, fc, row++, "Gender *", cmbGender);

        cmbBloodGroup = ThemeManager.createStyledComboBox(
            new String[]{"A+","A-","B+","B-","AB+","AB-","O+","O-"});
        addRow(form, lc, fc, row++, "Blood Group *", cmbBloodGroup);

        // Phone – 10-digit real-time filter
        txtPhone = field();
        ThemeManager.applyDigitOnlyFilter(txtPhone, 10);
        txtPhone.setToolTipText("Enter exactly 10 digits. Example: 9876543210");
        addRow(form, lc, fc, row++, "Phone * (10 digits)", txtPhone);
        addRow(form, lc, fc, row++, "Email",   txtEmail = field());
        addRow(form, lc, fc, row++, "Address", txtAddress = field());

        // City with required marker
        JPanel cityPanel = new JPanel(new BorderLayout(4, 0));
        cityPanel.setBackground(ThemeManager.bg());
        txtCity = field();
        JLabel req = new JLabel("Required");
        req.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        req.setForeground(ThemeManager.danger());
        cityPanel.add(txtCity, BorderLayout.CENTER);
        cityPanel.add(req,     BorderLayout.EAST);
        addRow(form, lc, fc, row++, "City *", cityPanel);

        // Date with Today shortcut
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

        cmbStatus = ThemeManager.createStyledComboBox(new String[]{"Available","Not Available"});
        addRow(form, lc, fc, row, "Status *", cmbStatus);

        return form;
    }

    private JPanel buildButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        bar.setBackground(ThemeManager.bg());
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeManager.border()));

        JButton btnCancel = ThemeManager.createSecondaryButton("Cancel");
        JButton btnUpdate = ThemeManager.createAccentButton("Save Changes");

        btnCancel.addActionListener(e -> {
            if (onSuccess != null) onSuccess.run();
        });
        btnUpdate.addActionListener(e -> doUpdate());

        bar.add(btnCancel);
        bar.add(btnUpdate);
        return bar;
    }

    private JTextField field() {
        return ThemeManager.createStyledTextField(22);
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

    private void populateForm() {
        txtDonorId.setText(currentDonor.getDonorId());
        txtDonorName.setText(currentDonor.getDonorName());
        txtAge.setText(String.valueOf(currentDonor.getAge()));
        cmbGender.setSelectedItem(currentDonor.getGender());
        cmbBloodGroup.setSelectedItem(currentDonor.getBloodGroup());
        txtPhone.setText(currentDonor.getPhone());
        txtEmail.setText(currentDonor.getEmail() != null ? currentDonor.getEmail() : "");
        txtAddress.setText(currentDonor.getAddress() != null ? currentDonor.getAddress() : "");
        txtCity.setText(currentDonor.getCity());
        if (currentDonor.getLastDonationDate() != null) {
            txtLastDonation.setText(
                new SimpleDateFormat("yyyy-MM-dd").format(currentDonor.getLastDonationDate())
            );
        } else {
            txtLastDonation.setText("");
        }
        cmbStatus.setSelectedItem(currentDonor.getStatus());
    }

    private void doUpdate() {
        try {
            Donor donor = buildDonorFromForm();
            donorService.updateDonor(donor);

            JOptionPane.showMessageDialog(this,
                "Donor record updated successfully!",
                "Update Successful",
                JOptionPane.INFORMATION_MESSAGE);

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
        if (city.isEmpty()) {
            throw new ValidationException("City", "Please enter the city.");
        }
        Date donationDate = ValidationUtil.parseDate(dateText);

        return new Donor(currentDonor.getDonorId(), name, age, gender, bloodGroup,
                         phone, email.isEmpty() ? null : email,
                         address.isEmpty() ? null : address,
                         city, donationDate, status);
    }
}
