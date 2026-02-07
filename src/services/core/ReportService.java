package services.core;

import factories.ReportFactory;
import interfaces.Report;
import java.util.Scanner;

public class ReportService {
    private final ReportFactory reportFactory;

    public ReportService(ReportFactory reportFactory) {
        this.reportFactory = reportFactory;
    }

    public void showReportsMenu(Scanner scanner) {
        boolean back = false;

        while (!back) {
            System.out.println("\n=== System Reports ===");
            System.out.println("1. Most Borrowed Books Report");
            System.out.println("2. Overdue Books Report");
            System.out.println("3. Users with Fines Report");
            System.out.println("4. Popular Categories Report");
            System.out.println("5. User Activity Report");
            System.out.println("6. Book Inventory Report");
            System.out.println("7. Back to main menu");
            System.out.print("Choose option (1-7): ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice >= 1 && choice <= 6) {
                try {
                    Report report = reportFactory.createReport(choice);
                    report.generate();

                    System.out.print("\nPress Enter to continue...");
                    scanner.nextLine();
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else if (choice == 7) {
                back = true;
            } else {
                System.out.println("Invalid choice!");
            }
        }
    }
}
