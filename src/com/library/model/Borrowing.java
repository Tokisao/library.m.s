package com.library.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Borrowing {
    private int borrowingId;
    private int userId;
    private int bookId;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private LocalDate returnedDate;
    private String status;
    private double fineAmount;
    private boolean finePaid;
    private int daysExtended;

    private String userName;
    private String bookTitle;
    private String userEmail;

    public Borrowing() {
        this.borrowedDate = LocalDate.now();
        this.dueDate = borrowedDate.plusDays(14);
        this.status = "BORROWED";
        this.fineAmount = 0.0;
        this.finePaid = false;
        this.daysExtended = 0;
    }

    public Borrowing(int userId, int bookId, int borrowDays) {
        this();
        this.userId = userId;
        this.bookId = bookId;
        if (borrowDays > 0) {
            this.dueDate = borrowedDate.plusDays(borrowDays);
        }
    }

    public int getBorrowingId() { return borrowingId; }
    public void setBorrowingId(int borrowingId) { this.borrowingId = borrowingId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public LocalDate getBorrowedDate() { return borrowedDate; }
    public void setBorrowedDate(LocalDate borrowedDate) { this.borrowedDate = borrowedDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnedDate() { return returnedDate; }
    public void setReturnedDate(LocalDate returnedDate) { this.returnedDate = returnedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }

    public boolean isFinePaid() { return finePaid; }
    public void setFinePaid(boolean finePaid) { this.finePaid = finePaid; }

    public int getDaysExtended() { return daysExtended; }
    public void setDaysExtended(int daysExtended) { this.daysExtended = daysExtended; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public boolean isOverdue() {
        if ("RETURNED".equals(status)) {
            return false;
        }
        return LocalDate.now().isAfter(dueDate);
    }

    public long getDaysOverdue() {
        if (!isOverdue()) return 0;
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    public long getDaysBorrowed() {
        if (returnedDate != null) {
            return ChronoUnit.DAYS.between(borrowedDate, returnedDate);
        } else {
            return ChronoUnit.DAYS.between(borrowedDate, LocalDate.now());
        }
    }

    public long getDaysRemaining() {
        if ("RETURNED".equals(status)) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    public double calculateFine(double dailyRate) {
        if (!isOverdue()) return 0;
        long daysOverdue = getDaysOverdue();
        return daysOverdue * dailyRate;
    }

    public boolean extendDueDate(int additionalDays) {
        if ("RETURNED".equals(status)) return false;
        this.dueDate = this.dueDate.plusDays(additionalDays);
        this.daysExtended += additionalDays;
        return true;
    }

    public boolean returnBook() {
        if ("RETURNED".equals(status)) return false;
        this.returnedDate = LocalDate.now();
        this.status = "RETURNED";
        return true;
    }

    public String getStatusWithDetails() {
        switch (status) {
            case "BORROWED":
                if (isOverdue()) {
                    return String.format("OVERDUE (%d days)", getDaysOverdue());
                } else {
                    return String.format("BORROWED (%d days remaining)", getDaysRemaining());
                }
            case "RETURNED":
                return "RETURNED";
            case "OVERDUE":
                return String.format("OVERDUE (%d days)", getDaysOverdue());
            default:
                return status;
        }
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %-3d | User: %-15s | Book: %-25s | Due: %s | Status: %s",
                borrowingId,
                userName != null && userName.length() > 15 ? userName.substring(0, 12) + "..." : userName,
                bookTitle != null && bookTitle.length() > 25 ? bookTitle.substring(0, 22) + "..." : bookTitle,
                dueDate,
                getStatusWithDetails()
        );
    }

    public String toDetailedString() {
        return String.format(
                "\n═══════════════════════════════════════════════════════════════════\n" +
                        "BORROWING DETAILS\n" +
                        "═══════════════════════════════════════════════════════════════════\n" +
                        "Borrowing ID:       %d\n" +
                        "User:               %s (ID: %d)\n" +
                        "Book:               %s (ID: %d)\n" +
                        "Borrowed Date:      %s\n" +
                        "Due Date:           %s\n" +
                        "Returned Date:      %s\n" +
                        "Days Borrowed:      %d\n" +
                        "Status:             %s\n" +
                        "Fine Amount:        $%.2f\n" +
                        "Fine Paid:          %s\n" +
                        "Days Extended:      %d\n" +
                        "═══════════════════════════════════════════════════════════════════",
                borrowingId,
                userName != null ? userName : "User #" + userId,
                userId,
                bookTitle != null ? bookTitle : "Book #" + bookId,
                bookId,
                borrowedDate,
                dueDate,
                returnedDate != null ? returnedDate.toString() : "Not returned yet",
                getDaysBorrowed(),
                getStatusWithDetails(),
                fineAmount,
                finePaid ? "Yes" : "No",
                daysExtended
        );
    }
}