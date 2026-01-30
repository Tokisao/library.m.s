    import util.LibraryService;
    import models.User;
    import dao.*;
    import util.*;
    import util.FineService;
    import dao.DatabaseConnection;

    import java.util.Optional;
    import java.util.Scanner;

    public class Main {
        public static void main(String[] args) {
            DatabaseConnection.loadDriver();

            BookRepository bookRepository = new BookRepository();
            UserRepository userRepository = new UserRepository();
            BorrowsRepository borrowRepository = new BorrowsRepository();

            Authentication authService = new Authentication(userRepository);
            LibraryService libraryService = new LibraryService(
                    bookRepository, borrowRepository, userRepository
            );
            FineService fineService = new FineService(
                    userRepository, borrowRepository, bookRepository);

            Scanner scanner = new Scanner(System.in);
            boolean exit = false;

            System.out.println("~~~ Welcome to Library System ~~~");

            while (!exit) {
                if (libraryService.getCurrentUser()==null) {
                    showMainMenu(scanner, authService, libraryService, fineService);
                } else {
                    showUserMenu(scanner, libraryService, fineService);
                }
            }

            scanner.close();
            System.out.println("\nThank you for using our library system!");
        }

        private static void showMainMenu(Scanner scanner,
                                         Authentication authService,
                                         LibraryService libraryService,
                                         FineService fineService) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Login");
            System.out.println("2. View available books");
            System.out.println("3. Exit");
            System.out.print("Choose option (1-3): ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    Optional<User> user = authService.login(scanner);
                    user.ifPresent(libraryService::setCurrentUser);
                    user.ifPresent(fineService::setCurrentUser);
                    break;
                case 2:
                    libraryService.showAllBooks();
                    break;
                case 3:
                    System.exit(0);
                    System.out.println("Thank you for visiting our library!");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }

        private static void showUserMenu(Scanner scanner, LibraryService libraryService, FineService fineService) {
            System.out.println("\n~~~ User Menu ~~~");
            System.out.println("1. View available books");
            System.out.println("2. Borrow a book");
            System.out.println("3. View my borrows");
            System.out.println("4. View my account");
            System.out.println("5. Logout");
            System.out.println("Choose option (1-5): ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    libraryService.showAllBooks();
                    break;
                case 2:
                    libraryService.borrowBook(scanner);
                    break;
                case 3:
                    libraryService.viewMyBorrows();
                    break;
                case 4:
                    libraryService.showUserInfo();
                    fineService.calculateAndUpdateAllFines();
                    fineService.showCurrentFines();
                    break;
                case 5:
                    libraryService.setCurrentUser(null);
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
