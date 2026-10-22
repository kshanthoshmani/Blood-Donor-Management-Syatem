-- ============================================================
-- Blood Donor Information System - Oracle Database Schema
-- Run this script as SYSDBA or a DBA user to set up the schema
-- ============================================================

-- Step 1: Create the application user (run as SYSDBA)
-- If you already have a user, skip this block and grant privileges to your user

-- CREATE USER donor_admin IDENTIFIED BY donor_admin;
-- GRANT CONNECT, RESOURCE, CREATE TABLE, CREATE SEQUENCE TO donor_admin;
-- ALTER USER donor_admin QUOTA UNLIMITED ON USERS;

-- ============================================================
-- Step 2: Connect as donor_admin and run the rest
-- ============================================================

-- Drop existing objects (safe to re-run)
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE DONORS CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE USERS CASCADE CONSTRAINTS';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE donor_seq';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE user_seq';
EXCEPTION WHEN OTHERS THEN NULL;
END;
/

-- ============================================================
-- USERS Table
-- ============================================================
CREATE TABLE USERS (
    USER_ID   NUMBER PRIMARY KEY,
    USERNAME  VARCHAR2(50)  NOT NULL UNIQUE,
    PASSWORD  VARCHAR2(100) NOT NULL
);

-- Sequence for USERS
CREATE SEQUENCE user_seq START WITH 1 INCREMENT BY 1;

-- Default admin user (password: admin123)
INSERT INTO USERS (USER_ID, USERNAME, PASSWORD)
VALUES (user_seq.NEXTVAL, 'admin', 'admin123');

-- Additional test user
INSERT INTO USERS (USER_ID, USERNAME, PASSWORD)
VALUES (user_seq.NEXTVAL, 'donor_admin', 'donor_admin');

COMMIT;

-- ============================================================
-- DONORS Table
-- ============================================================
CREATE TABLE DONORS (
    DONOR_ID           VARCHAR2(20)  PRIMARY KEY,
    DONOR_NAME         VARCHAR2(100) NOT NULL,
    AGE                NUMBER(3)     NOT NULL,
    GENDER             VARCHAR2(10)  NOT NULL,
    BLOOD_GROUP        VARCHAR2(5)   NOT NULL,
    PHONE              VARCHAR2(15)  NOT NULL,
    EMAIL              VARCHAR2(100),
    ADDRESS            VARCHAR2(255),
    CITY               VARCHAR2(50)  NOT NULL,
    LAST_DONATION_DATE DATE,
    STATUS             VARCHAR2(20)  DEFAULT 'Available' NOT NULL
);

-- Sequence for auto-generating DONOR_ID numbers
CREATE SEQUENCE donor_seq START WITH 1 INCREMENT BY 1;

-- ============================================================
-- Sample Data
-- ============================================================
INSERT INTO DONORS (DONOR_ID, DONOR_NAME, AGE, GENDER, BLOOD_GROUP, PHONE, EMAIL, ADDRESS, CITY, LAST_DONATION_DATE, STATUS)
VALUES ('D001', 'Arun Kumar', 28, 'Male', 'O+', '9876543210', 'arun@example.com', '12 Anna Nagar', 'Chennai', TO_DATE('2026-06-15', 'YYYY-MM-DD'), 'Available');

INSERT INTO DONORS (DONOR_ID, DONOR_NAME, AGE, GENDER, BLOOD_GROUP, PHONE, EMAIL, ADDRESS, CITY, LAST_DONATION_DATE, STATUS)
VALUES ('D002', 'Priya Sharma', 25, 'Female', 'A+', '9876543211', 'priya@example.com', '45 MG Road', 'Bangalore', TO_DATE('2026-05-20', 'YYYY-MM-DD'), 'Available');

INSERT INTO DONORS (DONOR_ID, DONOR_NAME, AGE, GENDER, BLOOD_GROUP, PHONE, EMAIL, ADDRESS, CITY, LAST_DONATION_DATE, STATUS)
VALUES ('D003', 'Rahul Singh', 32, 'Male', 'B+', '9876543212', 'rahul@example.com', '78 Park Street', 'Kolkata', TO_DATE('2026-04-10', 'YYYY-MM-DD'), 'Not Available');

INSERT INTO DONORS (DONOR_ID, DONOR_NAME, AGE, GENDER, BLOOD_GROUP, PHONE, EMAIL, ADDRESS, CITY, LAST_DONATION_DATE, STATUS)
VALUES ('D004', 'Sneha Patel', 30, 'Female', 'AB+', '9876543213', 'sneha@example.com', '23 SG Highway', 'Ahmedabad', TO_DATE('2026-07-01', 'YYYY-MM-DD'), 'Available');

INSERT INTO DONORS (DONOR_ID, DONOR_NAME, AGE, GENDER, BLOOD_GROUP, PHONE, EMAIL, ADDRESS, CITY, LAST_DONATION_DATE, STATUS)
VALUES ('D005', 'Vikram Nair', 27, 'Male', 'O-', '9876543214', 'vikram@example.com', '9 Marine Drive', 'Mumbai', TO_DATE('2026-03-22', 'YYYY-MM-DD'), 'Available');

COMMIT;

-- ============================================================
-- Verification
-- ============================================================
SELECT 'USERS table created with ' || COUNT(*) || ' records.' AS STATUS FROM USERS;
SELECT 'DONORS table created with ' || COUNT(*) || ' records.' AS STATUS FROM DONORS;
