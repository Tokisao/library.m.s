package reports;

import dao.BookRepository;
import dao.BorrowsRepository;
import dao.UserRepository;
import models.Book;
import models.Borrows;
import models.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class OverdueReport extends BaseReport {

    public OverdueReport(BookRepository bookRepository,
                         BorrowsRepository borrowsRepository,
                         UserRepository userRepository) {
        super(bookRepository, borrowsRepository, userRepository);
    }

    @Override
    public String getName() {
        return "Overdue Books Report";
    }

    @Override
    public void generate() {
        System.out.println("\n=== Overdue Books Report ===");

        List<Borrows> overdueBorrows = borrowsRepository.findOverdueBorrows();

        if (overdueBorrows.isEmpty()) {
            System.out.println("No overdue books found. Great job!");
            return;
        }

        System.out.println("\nOverdue Books List:");
        System.out.println("Total overdue items: " + overdueBorrows.size());

        Map<String, List<Borrows>> overdueByUser = overdueBorrows.stream()
                .collect(Collectors.groupingBy(Borrows::getUserId));
        System.out.println("Users with overdue items: " + overdueByUser.size());
        System.out.println();

        int count = 1;
        for (Borrows borrow : overdueBorrows) {
            Book book = bookRepository.findById(borrow.getBookId());
            Optional<User> user = userRepository.findByUserId(borrow.getUserId());

            System.out.println ("Book:"+ count++);
            System.out.printf("   Borrower: %s %s (ID: %s)\n",
                    user.isPresent() ? user.get().getFirstName() : "Unknown",
                    user.isPresent() ? user.get().getLastName() : "",
                    borrow.getUserId());
            System.out.print("   Author: " + book.getAuthor()+"\n");
            System.out.print("   Due Date:"+borrow.getDueDate().toString());
            System.out.println("   Days late: "+ borrow.getDaysExtended());
            System.out.println();
        }

        System.out.println("Statistics:");
        String worstOffender = findWorstOffender(overdueByUser);
        if (worstOffender != null) {
            User user = userRepository.findByUserId(worstOffender).orElse(null);
            if (user != null) {
                System.out.println("User with most overdue items: " +
                        user.getFirstName() + " " + user.getLastName() +
                        " (" + overdueByUser.get(worstOffender).size() + " items)");
            }
        }
    }

    private String findWorstOffender(Map<String, List<Borrows>> overdueByUser) {
        String worstUserId = null;
        int maxItems = 0;

        for (var entry : overdueByUser.entrySet()) {
            if (entry.getValue().size() > maxItems) {
                maxItems = entry.getValue().size();
                worstUserId = entry.getKey();
            }
        }

        return worstUserId;
    }
}
