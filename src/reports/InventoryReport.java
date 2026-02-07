package reports;

import dao.BookRepository;
import dao.BorrowsRepository;
import dao.UserRepository;
import models.Book;

import java.util.List;

public class InventoryReport extends BaseReport {

    public InventoryReport(BookRepository bookRepository,
                           BorrowsRepository borrowsRepository,
                           UserRepository userRepository) {
        super(bookRepository, borrowsRepository, userRepository);
    }

    @Override
    public String getName() {
        return "Book Inventory Report";
    }

    @Override
    public void generate() {
        System.out.println("\n=== Book Inventory Report ===");

        List<Book> allBooks = bookRepository.findAll();

        System.out.println("\nInventory Overview:");
        System.out.println("Total books in inventory: " + allBooks.size());

        int availableBooks = 0;
        int borrowedBooks = 0;
        int totalCopies = 0;
        int availableCopies = 0;

        for (Book book : allBooks) {
            totalCopies += book.getTotalCopies();
            availableCopies += book.getAvailableCopies();
            borrowedBooks += (book.getTotalCopies() - book.getAvailableCopies());

            if (book.getAvailableCopies() > 0) {
                availableBooks++;
            }
        }

        System.out.println("Unique book titles: " + allBooks.size());
        System.out.println("Total physical copies: " + totalCopies);
        System.out.println("Available copies for borrowing: " + availableCopies);
        System.out.println("Currently borrowed copies: " + borrowedBooks);

        if (totalCopies > 0) {
            double availabilityRate = (double) availableCopies / totalCopies * 100;
            System.out.printf("Inventory availability: %.1f%%\n", availabilityRate);
        }

        System.out.println("\nLow Stock Alerts:");
        int lowStockCount = 0;
        for (Book book : allBooks) {
            if (book.getAvailableCopies() <= 2 && book.getTotalCopies() > 0) {
                System.out.printf("- %s by %s\n", book.getName(), book.getAuthor());
                System.out.printf("  Available copies: %d\n", book.getAvailableCopies());
                System.out.printf("  Total copies: %d\n", book.getTotalCopies());
                System.out.printf("  Category: %s\n", book.getCategory() != null ? book.getCategory() : "N/A");
                System.out.println();
                lowStockCount++;
            }
        }

        if (lowStockCount == 0) {
            System.out.println("No low stock items. Good inventory levels maintained.");
        }

        System.out.println("\nOut of Stock Items:");
        int outOfStockCount = 0;
        for (Book book : allBooks) {
            if (book.getAvailableCopies() == 0 && book.getTotalCopies() > 0) {
                System.out.printf("- %s by %s\n", book.getName(), book.getAuthor());
                System.out.printf("  Total copies: %d\n", book.getTotalCopies());
                System.out.printf("  Category: %s\n", book.getCategory() != null ? book.getCategory() : "N/A");
                System.out.println();
                outOfStockCount++;
            }
        }

        if (outOfStockCount == 0) {
            System.out.println("No items completely out of stock.");
        }
    }
}
