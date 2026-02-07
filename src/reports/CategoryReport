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

public class CategoryReport extends BaseReport {

    public CategoryReport(BookRepository bookRepository,
                          BorrowsRepository borrowsRepository,
                          UserRepository userRepository) {
        super(bookRepository, borrowsRepository, userRepository);
    }

    @Override
    public String getName() {
        return "Popular Categories Report";
    }

    @Override
    public void generate() {
        System.out.println("\n=== Popular Categories Report ===");

        List<Book> allBooks = bookRepository.findAll();
        List<Borrows> allBorrows = borrowsRepository.findAll();

        if (allBooks.isEmpty()) {
            System.out.println("No books found in the database.");
            return;
        }

        Map<String, Integer> booksPerCategory = new HashMap<>();
        Map<String, Integer> borrowsPerCategory = new HashMap<>();

        for (Book book : allBooks) {
            String category = book.getCategory();
            if (category == null || category.trim().isEmpty()) {
                category = "Uncategorized";
            }
            booksPerCategory.put(category, booksPerCategory.getOrDefault(category, 0) + 1);
        }

        for (Borrows borrow : allBorrows) {
            Book book = bookRepository.findById(borrow.getBookId());
            if (book != null) {
                String category = book.getCategory();
                if (category == null || category.trim().isEmpty()) {
                    category = "Uncategorized";
                }
                borrowsPerCategory.put(category, borrowsPerCategory.getOrDefault(category, 0) + 1);
            }
        }

        System.out.println("\nCategory Statistics:");
        System.out.printf("Total books in system: %d\n", allBooks.size());
        System.out.printf("Total categories: %d\n", booksPerCategory.size());
        System.out.println();

        var sortedCategories = borrowsPerCategory.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .collect(Collectors.toList());

        if (sortedCategories.isEmpty()) {
            System.out.println("No borrowing data available for categories.");
            return;
        }

        int rank = 1;
        for (var entry : sortedCategories) {
            String category = entry.getKey();
            int totalBorrows = entry.getValue();
            int totalBooks = booksPerCategory.getOrDefault(category, 0);
            double borrowsPerBook = totalBooks > 0 ? (double) totalBorrows / totalBooks : 0;

            System.out.println("Category: "+ rank++);
            System.out.println("   Total books: "+ totalBooks);
            System.out.println("   Total borrows: "+ totalBorrows);
            System.out.println("   Borrows per book: "+ borrowsPerBook);
            System.out.println();
        }

        System.out.println("Category Summary:");

        long uncategorizedBooks = allBooks.stream()
                .filter(book -> book.getCategory() == null || book.getCategory().trim().isEmpty())
                .count();
        System.out.printf("Uncategorized books: %d\n", uncategorizedBooks);

        if (!sortedCategories.isEmpty()) {
            var mostPopular = sortedCategories.get(0);
            System.out.printf("Most popular category: %s (%d borrows)\n",
                    mostPopular.getKey(), mostPopular.getValue());

            var leastPopular = sortedCategories.get(sortedCategories.size() - 1);
            System.out.printf("Least popular category: %s (%d borrows)\n",
                    leastPopular.getKey(), leastPopular.getValue());
        }
    }
}
