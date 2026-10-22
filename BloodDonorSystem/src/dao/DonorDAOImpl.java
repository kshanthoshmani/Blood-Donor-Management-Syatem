package dao;

import database.DatabaseConnection;
import exception.DatabaseException;
import model.Donor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DonorDAOImpl – concrete JDBC implementation of DonorDAO.
 * Demonstrates: DAO Pattern, JDBC, PreparedStatement, exception handling.
 */
public class DonorDAOImpl implements DonorDAO {

    // ── INSERT ────────────────────────────────────────────────────────────────

    @Override
    public void addDonor(Donor donor) throws DatabaseException {
        String sql = "INSERT INTO DONORS " +
                     "(DONOR_ID, DONOR_NAME, AGE, GENDER, BLOOD_GROUP, PHONE, " +
                     " EMAIL, ADDRESS, CITY, LAST_DONATION_DATE, STATUS) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, donor.getDonorId());
            ps.setString(2, donor.getDonorName());
            ps.setInt   (3, donor.getAge());
            ps.setString(4, donor.getGender());
            ps.setString(5, donor.getBloodGroup());
            ps.setString(6, donor.getPhone());
            ps.setString(7, donor.getEmail());
            ps.setString(8, donor.getAddress());
            ps.setString(9, donor.getCity());

            if (donor.getLastDonationDate() != null) {
                ps.setDate(10, new java.sql.Date(donor.getLastDonationDate().getTime()));
            } else {
                ps.setNull(10, Types.DATE);
            }
            ps.setString(11, donor.getStatus() != null ? donor.getStatus() : "Available");

            ps.executeUpdate();

        } catch (SQLException e) {
            // Handle duplicate primary key (works for SQLite, Oracle, MySQL, etc.)
            String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            if (e instanceof java.sql.SQLIntegrityConstraintViolationException
                    || msg.contains("unique") || msg.contains("primary key")
                    || msg.contains("duplicate") || e.getErrorCode() == 19) {
                throw new DatabaseException(
                    "Donor ID '" + donor.getDonorId() + "' already exists.", e);
            }
            throw new DatabaseException(
                "Failed to register donor: " + e.getMessage(), e.getErrorCode(), e);
        }
    }

    // ── SELECT ALL ────────────────────────────────────────────────────────────

    @Override
    public List<Donor> getAllDonors() throws DatabaseException {
        String sql = "SELECT * FROM DONORS ORDER BY DONOR_ID";
        List<Donor> donors = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                donors.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                "Failed to retrieve donors: " + e.getMessage(), e.getErrorCode(), e);
        }
        return donors;
    }

    // ── SELECT BY ID ──────────────────────────────────────────────────────────

    @Override
    public Donor getDonorById(String donorId) throws DatabaseException {
        String sql = "SELECT * FROM DONORS WHERE DONOR_ID = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, donorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                "Failed to get donor: " + e.getMessage(), e.getErrorCode(), e);
        }
        return null;
    }

    // ── SEARCH (flexible multi-criteria) ─────────────────────────────────────

    @Override
    public List<Donor> searchDonors(String donorId, String donorName,
                                    String bloodGroup, String city,
                                    String phone, String status)
            throws DatabaseException {

        StringBuilder sql = new StringBuilder("SELECT * FROM DONORS WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (notBlank(donorId)) {
            sql.append(" AND UPPER(DONOR_ID) LIKE UPPER(?)");
            params.add("%" + donorId.trim() + "%");
        }
        if (notBlank(donorName)) {
            sql.append(" AND UPPER(DONOR_NAME) LIKE UPPER(?)");
            params.add("%" + donorName.trim() + "%");
        }
        if (notBlank(bloodGroup)) {
            sql.append(" AND BLOOD_GROUP = ?");
            params.add(bloodGroup.trim());
        }
        if (notBlank(city)) {
            sql.append(" AND UPPER(CITY) LIKE UPPER(?)");
            params.add("%" + city.trim() + "%");
        }
        if (notBlank(phone)) {
            sql.append(" AND PHONE LIKE ?");
            params.add("%" + phone.trim() + "%");
        }
        if (notBlank(status)) {
            sql.append(" AND STATUS = ?");
            params.add(status.trim());
        }
        sql.append(" ORDER BY DONOR_ID");

        List<Donor> results = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                "Search failed: " + e.getMessage(), e.getErrorCode(), e);
        }
        return results;
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Override
    public void updateDonor(Donor donor) throws DatabaseException {
        String sql = "UPDATE DONORS SET " +
                     "DONOR_NAME=?, AGE=?, GENDER=?, BLOOD_GROUP=?, PHONE=?, " +
                     "EMAIL=?, ADDRESS=?, CITY=?, LAST_DONATION_DATE=?, STATUS=? " +
                     "WHERE DONOR_ID=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, donor.getDonorName());
            ps.setInt   (2, donor.getAge());
            ps.setString(3, donor.getGender());
            ps.setString(4, donor.getBloodGroup());
            ps.setString(5, donor.getPhone());
            ps.setString(6, donor.getEmail());
            ps.setString(7, donor.getAddress());
            ps.setString(8, donor.getCity());

            if (donor.getLastDonationDate() != null) {
                ps.setDate(9, new java.sql.Date(donor.getLastDonationDate().getTime()));
            } else {
                ps.setNull(9, Types.DATE);
            }
            ps.setString(10, donor.getStatus());
            ps.setString(11, donor.getDonorId());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new DatabaseException(
                    "No donor found with ID '" + donor.getDonorId() + "'.");
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                "Failed to update donor: " + e.getMessage(), e.getErrorCode(), e);
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Override
    public void deleteDonor(String donorId) throws DatabaseException {
        String sql = "DELETE FROM DONORS WHERE DONOR_ID = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, donorId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new DatabaseException(
                    "No donor found with ID '" + donorId + "'.");
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                "Failed to delete donor: " + e.getMessage(), e.getErrorCode(), e);
        }
    }

    // ── STATISTICS ────────────────────────────────────────────────────────────

    @Override
    public int getTotalDonors() throws DatabaseException {
        return countQuery("SELECT COUNT(*) FROM DONORS");
    }

    @Override
    public int getAvailableDonors() throws DatabaseException {
        return countQuery("SELECT COUNT(*) FROM DONORS WHERE STATUS = 'Available'");
    }

    @Override
    public int getBloodGroupCount() throws DatabaseException {
        return countQuery("SELECT COUNT(DISTINCT BLOOD_GROUP) FROM DONORS WHERE BLOOD_GROUP IS NOT NULL");
    }

    private int countQuery(String sql) throws DatabaseException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            throw new DatabaseException(
                "Count query failed: " + e.getMessage(), e.getErrorCode(), e);
        }
    }

    @Override
    public List<Donor> getRecentDonors() throws DatabaseException {
        List<Donor> donors = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            boolean isOracle = false;
            try {
                DatabaseMetaData meta = conn.getMetaData();
                if (meta != null && meta.getDatabaseProductName() != null) {
                    isOracle = meta.getDatabaseProductName().toLowerCase().contains("oracle");
                }
            } catch (Exception ignored) {}

            String sql;
            if (isOracle) {
                sql = "SELECT * FROM (SELECT * FROM DONORS ORDER BY DONOR_ID DESC) WHERE ROWNUM <= 10";
            } else {
                sql = "SELECT * FROM DONORS ORDER BY DONOR_ID DESC LIMIT 10";
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setMaxRows(10);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        donors.add(mapRow(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(
                "Failed to retrieve recent donors: " + e.getMessage(), e.getErrorCode(), e);
        }
        return donors;
    }

    @Override
    public boolean donorIdExists(String donorId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM DONORS WHERE DONOR_ID = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, donorId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                "ID check failed: " + e.getMessage(), e.getErrorCode(), e);
        }
    }

    // ── Row mapper ────────────────────────────────────────────────────────────

    private Donor mapRow(ResultSet rs) throws SQLException {
        Donor d = new Donor();
        d.setDonorId          (rs.getString("DONOR_ID"));
        d.setDonorName        (rs.getString("DONOR_NAME"));
        d.setAge              (rs.getInt   ("AGE"));
        d.setGender           (rs.getString("GENDER"));
        d.setBloodGroup       (rs.getString("BLOOD_GROUP"));
        d.setPhone            (rs.getString("PHONE"));
        d.setEmail            (rs.getString("EMAIL"));
        d.setAddress          (rs.getString("ADDRESS"));
        d.setCity             (rs.getString("CITY"));
        d.setLastDonationDate (rs.getDate  ("LAST_DONATION_DATE"));
        d.setStatus           (rs.getString("STATUS"));
        return d;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
