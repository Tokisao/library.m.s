package util;

import models.User;
import dao.BorrowsRepository;
import dao.UserRepository;
import dao.BookRepository;
import models.Borrows;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class FineService {
    private BorrowsRepository borrowsRepository;
    private UserRepository userRepository;
    private BookRepository bookRepository;
    public User currentUser;

    private static final float LOST_BOOK_FINE = 10000.0f;
    private static final float DAILY_FINE_RATE = 1000.0f;

    public FineService(UserRepository userRepository,
                       BorrowsRepository borrowsRepository,
                       BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.borrowsRepository = borrowsRepository;
        this.bookRepository = bookRepository;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void calculateAndUpdateAllFines() {
        System.out.println("\n~~~ Calculation and updating of all fines ~~~");

        if (currentUser == null) {
            System.out.println("Error: user not found");
            return;
        }

        String userId = currentUser.getUserId();

        List<Borrows> userBorrows = borrowsRepository.findByUserId(userId);
        float totalCalculatedFines = 0.0f;

        for (Borrows borrow : userBorrows) {
            float calculatedFine = calculateFineForBorrow(borrow);
            totalCalculatedFines += calculatedFine;

            if (borrow.getFineAmount() != calculatedFine) {
                borrowsRepository.updateFine(borrow.getId(), calculatedFine);
            }
        }

        setUserFinesDirectly(userId, totalCalculatedFines);
    }

    private float calculateFineForBorrow(Borrows borrow) {
        if ("LOST".equals(borrow.getStatus())) {
            System.out.println("Loan #" + borrow.getId() + ": LOST = " + LOST_BOOK_FINE + " тг");
            return LOST_BOOK_FINE;
        }

        if ("OVERDUE".equals(borrow.getStatus())) {
            int days = borrow.getDaysExtended() > 0 ?
                    borrow.getDaysExtended() :
                    calculateDaysFromDates(borrow);
            float fine = days * DAILY_FINE_RATE;
            System.out.println("Loan #" + borrow.getId() + ": OVERDUE " + days + " days = " + fine + " тг");
            return fine;
        }

        if ("RETURNED".equals(borrow.getStatus()) &&
                borrow.getReturnDate() != null &&
                borrow.getReturnDate().after(borrow.getDueDate())) {
            int days = borrow.getDaysExtended() > 0 ?
                    borrow.getDaysExtended() :
                    calculateDaysFromDates(borrow);
            float fine = days * DAILY_FINE_RATE;
            System.out.println("Loan #" + borrow.getId() + ": RETURNED LATE " + days + " days = " + fine + " тг");
            return fine;
        }

        return 0.0f;
    }

    private void setUserFinesDirectly(String userId, float amount) {
        try {
            java.sql.Connection con = dao.DatabaseConnection.getConnection();
            java.sql.PreparedStatement stmt = con.prepareStatement(
                    "UPDATE users SET fines = ? WHERE user_id = ?"
            );
            stmt.setFloat(1, amount);
            stmt.setString(2, userId);
            stmt.executeUpdate();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.err.println("Error setting fines: " + e.getMessage());
        }
    }

    private int calculateDaysFromDates(Borrows borrow) {
        Date dueDate = borrow.getDueDate();
        Date returnDate = borrow.getReturnDate();
        Date currentDate = new Date();

        if (dueDate == null) return 0;

        Date endDate = (returnDate != null) ? returnDate : currentDate;

        if (endDate.after(dueDate)) {
            LocalDate dueLocalDate = dueDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            LocalDate endLocalDate = endDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            long days = ChronoUnit.DAYS.between(dueLocalDate, endLocalDate);
            return (int) Math.max(0, days);
        }

        return 0;
    }

    public void showCurrentFines() {
        if (currentUser == null) {
            System.out.println("Error: User is not authorized.");
            return;
        }

        float totalFines = userRepository.getUserFines(currentUser.getUserId());
        System.out.println("\nTotal Fines: " + totalFines + " tenge");

        List<Borrows> userBorrows = borrowsRepository.findByUserId(currentUser.getUserId());

        for (Borrows borrow : userBorrows) {
            if (borrow.getFineAmount() > 0) {
                System.out.println("Loan #" + borrow.getId() +
                        " (status: " + borrow.getStatus() +
                        ", days_extended: " + borrow.getDaysExtended() +
                        "): " + borrow.getFineAmount() + " tenge");
            }
        }
    }
}
