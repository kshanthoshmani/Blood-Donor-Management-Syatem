package dao;

import exception.DatabaseException;
import model.Donor;

import java.util.List;

/**
 * DonorDAO interface – demonstrates Abstraction.
 * DonorDAOImpl provides the concrete JDBC implementation.
 */
public interface DonorDAO {

    /**
     * Inserts a new donor record (INSERT).
     */
    void addDonor(Donor donor) throws DatabaseException;

    /**
     * Returns all donor records (SELECT).
     */
    List<Donor> getAllDonors() throws DatabaseException;

    /**
     * Returns a single donor by primary key (SELECT).
     */
    Donor getDonorById(String donorId) throws DatabaseException;

    /**
     * Searches donors using flexible criteria (SELECT).
     * Any parameter may be null/empty to be ignored.
     */
    List<Donor> searchDonors(String donorId, String donorName,
                             String bloodGroup, String city,
                             String phone, String status) throws DatabaseException;

    /**
     * Updates an existing donor record (UPDATE).
     */
    void updateDonor(Donor donor) throws DatabaseException;

    /**
     * Deletes a donor record by primary key (DELETE).
     */
    void deleteDonor(String donorId) throws DatabaseException;

    /**
     * Returns the total number of donor records.
     */
    int getTotalDonors() throws DatabaseException;

    /**
     * Returns the number of donors whose status is 'Available'.
     */
    int getAvailableDonors() throws DatabaseException;

    /**
     * Returns the distinct blood group count.
     */
    int getBloodGroupCount() throws DatabaseException;

    /**
     * Returns the five most recently added donors.
     */
    List<Donor> getRecentDonors() throws DatabaseException;

    /**
     * Checks whether a donor ID already exists.
     */
    boolean donorIdExists(String donorId) throws DatabaseException;
}
