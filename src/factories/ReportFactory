package factories;

import dao.BookRepository;
import dao.BorrowsRepository;
import dao.UserRepository;
import interfaces.Report;
import reports.*;

public class ReportFactory {
    private final BookRepository bookRepository;
    private final BorrowsRepository borrowsRepository;
    private final UserRepository userRepository;

    public ReportFactory(BookRepository bookRepository,
                         BorrowsRepository borrowsRepository,
                         UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.borrowsRepository = borrowsRepository;
        this.userRepository = userRepository;
    }

    public Report createReport(String reportType) {
        switch (reportType.toUpperCase()) {
            case "MOST_BORROWED":
                return new MostBorrowedReport(bookRepository, borrowsRepository, userRepository);
            case "OVERDUE":
                return new OverdueReport(bookRepository, borrowsRepository, userRepository);
            case "FINES":
                return new FinesReport(bookRepository, borrowsRepository, userRepository);
            case "CATEGORY":
                return new CategoryReport(bookRepository, borrowsRepository, userRepository);
            case "USER_ACTIVITY":
                return new UserActivityReport(bookRepository, borrowsRepository, userRepository);
            case "INVENTORY":
                return new InventoryReport(bookRepository, borrowsRepository, userRepository);
            default:
                throw new IllegalArgumentException("Unknown report type: " + reportType);
        }
    }

    public Report createReport(int choice) {
        switch (choice) {
            case 1: return createReport("MOST_BORROWED");
            case 2: return createReport("OVERDUE");
            case 3: return createReport("FINES");
            case 4: return createReport("CATEGORY");
            case 5: return createReport("USER_ACTIVITY");
            case 6: return createReport("INVENTORY");
            default: throw new IllegalArgumentException("Invalid choice: " + choice);
        }
    }
}
