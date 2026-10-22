package model;

import java.util.Date;

/**
 * Donor model class demonstrating OOP Encapsulation.
 * All fields are private with public getters and setters.
 */
public class Donor {

    private String donorId;
    private String donorName;
    private int age;
    private String gender;
    private String bloodGroup;
    private String phone;
    private String email;
    private String address;
    private String city;
    private Date lastDonationDate;
    private String status;

    // ── Constructors ─────────────────────────────────────────────────────────

    public Donor() {}

    public Donor(String donorId, String donorName, int age, String gender,
                 String bloodGroup, String phone, String email,
                 String address, String city, Date lastDonationDate, String status) {
        this.donorId        = donorId;
        this.donorName      = donorName;
        this.age            = age;
        this.gender         = gender;
        this.bloodGroup     = bloodGroup;
        this.phone          = phone;
        this.email          = email;
        this.address        = address;
        this.city           = city;
        this.lastDonationDate = lastDonationDate;
        this.status         = status;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public String getDonorId()   { return donorId; }
    public void   setDonorId(String donorId) { this.donorId = donorId; }

    public String getDonorName() { return donorName; }
    public void   setDonorName(String donorName) { this.donorName = donorName; }

    public int    getAge()       { return age; }
    public void   setAge(int age) { this.age = age; }

    public String getGender()    { return gender; }
    public void   setGender(String gender) { this.gender = gender; }

    public String getBloodGroup() { return bloodGroup; }
    public void   setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getPhone()     { return phone; }
    public void   setPhone(String phone) { this.phone = phone; }

    public String getEmail()     { return email; }
    public void   setEmail(String email) { this.email = email; }

    public String getAddress()   { return address; }
    public void   setAddress(String address) { this.address = address; }

    public String getCity()      { return city; }
    public void   setCity(String city) { this.city = city; }

    public Date   getLastDonationDate() { return lastDonationDate; }
    public void   setLastDonationDate(Date lastDonationDate) { this.lastDonationDate = lastDonationDate; }

    public String getStatus()    { return status; }
    public void   setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Donor{id=" + donorId + ", name=" + donorName +
               ", bloodGroup=" + bloodGroup + ", city=" + city + "}";
    }
}
