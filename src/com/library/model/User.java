package com.library.model;

import java.time.LocalDate;
import java.time.Period;

public class User {
    private int userId;
    private String libraryId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private LocalDate registrationDate;
    private String status;
    private int totalBorrowed;
    private double totalFines;
    private int currentBorrowings;

    public User() {
        this.registrationDate = LocalDate.now();
        this.status = "ACTIVE";
        this.totalBorrowed = 0;
        this.totalFines = 0;
        this.currentBorrowings = 0;
    }

    public User(String libraryId, String firstName, String lastName,
                String email, String phone, String address, LocalDate dateOfBirth) {
        this();
        this.libraryId = libraryId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getLibraryId() { return libraryId; }
    public void setLibraryId(String libraryId) { this.libraryId = libraryId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getTotalBorrowed() { return totalBorrowed; }
    public void setTotalBorrowed(int totalBorrowed) { this.totalBorrowed = totalBorrowed; }

    public double getTotalFines() { return totalFines; }
    public void setTotalFines(double totalFines) { this.totalFines = totalFines; }

    public int getCurrentBorrowings() { return currentBorrowings; }
    public void setCurrentBorrowings(int currentBorrowings) { this.currentBorrowings = currentBorrowings; }

    public int getAge() {
        if (dateOfBirth == null) return 0;
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }

    public void activate() {
        this.status = "ACTIVE";
    }

    public void deactivate() {
        this.status = "INACTIVE";
    }

    public void incrementBorrowings() {
        currentBorrowings++;
        totalBorrowed++;
    }

    public void decrementBorrowings() {
        if (currentBorrowings > 0) {
            currentBorrowings--;
        }
    }

    public void addFine(double amount) {
        totalFines += amount;
    }

    public void payFine(double amount) {
        if (amount <= totalFines) {
            totalFines -= amount;
        }
    }

    public boolean canBorrowMore(int maxBorrowings) {
        return isActive() && currentBorrowings < maxBorrowings;
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %-3d | %-12s %-12s | Lib.ID: %-6s | Status: %-7s | Borrowed: %d",
                userId,
                firstName.length() > 12 ? firstName.substring(0, 9) + "..." : firstName,
                lastName.length() > 12 ? lastName.substring(0, 9) + "..." : lastName,
                libraryId,
                status,
                currentBorrowings
        );
    }

    public String toDetailedString() {
        return String.format(
                "\n═══════════════════════════════════════════════════════════════════\n" +
                        " USER DETAILS\n" +
                        "═══════════════════════════════════════════════════════════════════\n" +
                        "User ID:            %d\n" +
                        "Library ID:         %s\n" +
                        "Name:               %s %s\n" +
                        "Email:              %s\n" +
                        "Phone:              %s\n" +
                        "Address:            %s\n" +
                        "Date of Birth:      %s (Age: %d)\n" +
                        "Registration Date:  %s\n" +
                        "Status:             %s\n" +
                        "Currently Borrowed: %d books\n" +
                        "Total Borrowed:     %d books\n" +
                        "Total Fines:        $%.2f\n" +
                        "═══════════════════════════════════════════════════════════════════",
                userId, libraryId, firstName, lastName, email, phone, address,
                dateOfBirth, getAge(), registrationDate, status,
                currentBorrowings, totalBorrowed, totalFines
        );
    }
}