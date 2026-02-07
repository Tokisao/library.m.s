import dao.*;
import factories.*;
import services.core.*;
import services.management.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection.loadDriver();

        //Repositories
        BookRepository bookRepository = new BookRepository();
        UserRepository userRepository = new UserRepository();
        BorrowsRepository borrowRepository = new BorrowsRepository();

        //Fabrics
        ValidatorFactory validatorFactory = new ValidatorFactory();
        ReportFactory reportFactory = new ReportFactory(bookRepository, borrowRepository, userRepository);

        // Services
        Authentication authService = new Authentication(userRepository, validatorFactory);
        LibraryService libraryService = new LibraryService(bookRepository, borrowRepository, userRepository, validatorFactory);
        FineService fineService = new FineService(userRepository, borrowRepository, bookRepository, validatorFactory);
        ReportService reportService = new ReportService(reportFactory);

        BookManagementService bookService = new BookManagementService(bookRepository, libraryService, validatorFactory);
        UserManagementService userService = new UserManagementService(userRepository, libraryService, validatorFactory);
        BorrowManagementService borrowService = new BorrowManagementService(borrowRepository, validatorFactory);

        // Menu
        Scanner scanner = new Scanner(System.in);
        MenuManager menuManager = new MenuManager(scanner, authService, libraryService,
                fineService, bookService, userService,
                borrowService, reportService);

        menuManager.run();
    }
}
