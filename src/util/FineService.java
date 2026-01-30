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
    private static final float DAILY_FINE_RATE = 1000.0f; // 1000 тг за день просрочки

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
        float totalFinesToAdd = 0.0f;

        System.out.println("Fines found: " + userBorrows.size());

        for (Borrows borrow : userBorrows) {
            System.out.println("Loan processing #" + borrow.getId() +
                    " (status: " + borrow.getStatus() + ")");

            float newFine = 0.0f;

            if ("LOST".equals(borrow.getStatus())) {
                newFine = LOST_BOOK_FINE;
                System.out.println("  Lost: fine " + newFine + " tenge");
            }
            else if ("OVERDUE".equals(borrow.getStatus())) {
                int daysExtended = borrow.getDaysExtended();
                if (daysExtended > 0) {
                    newFine = daysExtended * DAILY_FINE_RATE;
                    System.out.println("  Overdue: " + daysExtended +
                            " days * " + DAILY_FINE_RATE +
                            " = " + newFine + " tenge");
                } else {
                    newFine = calculateDaysFromDates(borrow) * DAILY_FINE_RATE;
                    System.out.println("  Overdue (by dates): " + newFine + " tenge");
                }
            }

            else if ("RETURNED".equals(borrow.getStatus()) &&
                    borrow.getReturnDate() != null &&
                    borrow.getReturnDate().after(borrow.getDueDate())) {
                int daysExtended = borrow.getDaysExtended();
                if (daysExtended > 0) {
                    newFine = daysExtended * DAILY_FINE_RATE;
                    System.out.println(" Returned late: " + newFine + " tenge");
                }
            }

            if (newFine > 0 && newFine != borrow.getFineAmount()) {
                borrowsRepository.updateFine(borrow.getId(), newFine);
                totalFinesToAdd += newFine;
            }
        }
        userRepository.updateFines(userId, 0);
        if (totalFinesToAdd > 0) {

            userRepository.updateFines(userId, totalFinesToAdd);
            System.out.println("Total added to user's fines: " + totalFinesToAdd + " тг");
        } else {
            System.out.println("There are no new fines to add");
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

        System.out.println("\nTotal Fines: " + userRepository.getUserFines(currentUser.getUserId()) + " tenge");

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
