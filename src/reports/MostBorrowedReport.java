package reports;

import dao.BookRepository;
import dao.BorrowsRepository;
import dao.UserRepository;
import models.Book;
import models.Borrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MostBorrowedReport extends BaseReport {

    public MostBorrowedReport(BookRepository bookRepository,
                              BorrowsRepository borrowsRepository,
                              UserRepository userRepository) {
        super(bookRepository, borrowsRepository, userRepository);
    }

    @Override
    public String getName() {
        return "Most Borrowed Books Report";
    }

    @Override
    public void generate() {
        System.out.println("\n=== Most Borrowed Books Report ===");
        List<Borrows> allBorrows = borrowsRepository.findAll();
        Map<Integer, Integer> bookBorrowCount = new HashMap<>();
        for (Borrows borrow : allBorrows) {
            int bookId = borrow.getBookId();
            bookBorrowCount.put(bookId, bookBorrowCount.getOrDefault(bookId, 0) + 1);
        }

        var sortedBooks = bookBorrowCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .collect(Collectors.toList());

        if (sortedBooks.isEmpty()) {
            System.out.println("No borrowing data available.");
            return;
        }

        System.out.println("\nTop 10 Most Borrowed Books:");
        int rank = 1;
        for (var entry : sortedBooks) {
            Book book = bookRepository.findById(entry.getKey());
            if (book != null) {
                System.out.printf("%d. %s\n", rank++, book.getName());
                System.out.printf("   Author: %s\n", book.getAuthor());
                System.out.printf("   Times borrowed: %d\n", entry.getValue());
                System.out.printf("   Category: %s\n", book.getCategory() != null ? book.getCategory() : "N/A");
                System.out.println();
            }
        }

        System.out.println("Statistics:");
        int totalBorrows = allBorrows.size();
        System.out.println("Total borrows in system: " + totalBorrows);
        System.out.println("Unique books borrowed: " + bookBorrowCount.size());

        if (!sortedBooks.isEmpty()) {
            var mostBorrowed = sortedBooks.get(0);
            Book book = bookRepository.findById(mostBorrowed.getKey());
            if (book != null) {
                System.out.println("Most borrowed book: " + book.getName() +
                        " (" + mostBorrowed.getValue() + " times)");
            }
        }
    }
}
