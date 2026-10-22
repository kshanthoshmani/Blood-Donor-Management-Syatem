package service;

import dao.DonorDAO;
import dao.DonorDAOImpl;
import exception.DatabaseException;
import exception.ValidationException;
import model.Donor;
import util.ValidationUtil;

import java.util.List;

/**
 * DonorService – business logic layer between GUI and DAO.
 * Demonstrates Service Layer pattern, OOP, and exception handling.
 * All validation is performed here before delegating to the DAO.
 */
public class DonorService {

    private final DonorDAO donorDAO;

    public DonorService() {
        this.donorDAO = new DonorDAOImpl();
    }

    // ── Register ──────────────────────────────────────────────────────────────

    /**
     * Validates and registers a new donor.
     * Throws ValidationException for bad input, DatabaseException for DB errors.
     */
    public void registerDonor(Donor donor)
            throws ValidationException, DatabaseException {

        validateDonor(donor);

        if (donorDAO.donorIdExists(donor.getDonorId())) {
            throw new ValidationException("Donor ID",
                "Donor ID '" + donor.getDonorId() + "' is already registered.");
        }

        donorDAO.addDonor(donor);
    }

    // ── View ──────────────────────────────────────────────────────────────────

    public List<Donor> getAllDonors() throws DatabaseException {
        return donorDAO.getAllDonors();
    }

    public Donor getDonorById(String donorId)
            throws ValidationException, DatabaseException {

        if (donorId == null || donorId.trim().isEmpty()) {
            throw new ValidationException("Donor ID", "Please enter a Donor ID.");
        }
        return donorDAO.getDonorById(donorId.trim());
    }

    // ── Search ────────────────────────────────────────────────────────────────

    public List<Donor> searchDonors(String donorId, String donorName,
                                    String bloodGroup, String city,
                                    String phone, String status)
            throws DatabaseException {

        return donorDAO.searchDonors(donorId, donorName, bloodGroup, city, phone, status);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public void updateDonor(Donor donor)
            throws ValidationException, DatabaseException {

        validateDonor(donor);
        donorDAO.updateDonor(donor);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public void deleteDonor(String donorId)
            throws ValidationException, DatabaseException {

        if (donorId == null || donorId.trim().isEmpty()) {
            throw new ValidationException("Donor ID", "Please select a donor to delete.");
        }
        donorDAO.deleteDonor(donorId.trim());
    }

    // ── Dashboard statistics ──────────────────────────────────────────────────

    public int getTotalDonors() throws DatabaseException {
        return donorDAO.getTotalDonors();
    }

    public int getAvailableDonors() throws DatabaseException {
        return donorDAO.getAvailableDonors();
    }

    public int getBloodGroupCount() throws DatabaseException {
        return donorDAO.getBloodGroupCount();
    }

    public List<Donor> getRecentDonors() throws DatabaseException {
        return donorDAO.getRecentDonors();
    }

    // ── Internal validation ───────────────────────────────────────────────────

    private void validateDonor(Donor donor) throws ValidationException {
        ValidationUtil.validateDonorId(donor.getDonorId());
        ValidationUtil.validateDonorName(donor.getDonorName());
        ValidationUtil.validateAge(donor.getAge());
        ValidationUtil.validatePhone(donor.getPhone());
        ValidationUtil.validateEmail(donor.getEmail());
        ValidationUtil.validateCity(donor.getCity());
        ValidationUtil.validateBloodGroup(donor.getBloodGroup());
        ValidationUtil.validateStatus(donor.getStatus());
    }
}
