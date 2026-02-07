package reports;

import dao.BookRepository;
import dao.BorrowsRepository;
import dao.UserRepository;
import models.Borrows;
import models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserActivityReport extends BaseReport {

    public UserActivityReport(BookRepository bookRepository,
                              BorrowsRepository borrowsRepository,
                              UserRepository userRepository) {
        super(bookRepository, borrowsRepository, userRepository);
    }

    @Override
    public String getName() {
        return "User Activity Report";
    }

    @Override
    public void generate() {
        System.out.println("\n=== User Activity Report ===");

        List<User> allUsers = userRepository.findAll();
        System.out.println("\nUser Activity Overview:");
        System.out.println("Total registered users: " + allUsers.size());

        int activeUsers = 0;
        Map<String, Integer> userBorrowCount = new HashMap<>();
        List<Borrows> allBorrows = borrowsRepository.findAll();

        for (Borrows borrow : allBorrows) {
            String userId = borrow.getUserId();
            userBorrowCount.put(userId, userBorrowCount.getOrDefault(userId, 0) + 1);
        }

        for (User user : allUsers) {
            if (userBorrowCount.containsKey(user.getUserId())) {
                activeUsers++;
            }
        }

        System.out.println("Active users (with at least one borrow): " + activeUsers);
        System.out.println("Inactive users: " + (allUsers.size() - activeUsers));

        if (!allUsers.isEmpty()) {
            double activityPercentage = (double) activeUsers / allUsers.size() * 100;
            System.out.printf("User activity rate: %.1f%%\n", activityPercentage);
        }

        System.out.println("\nTop 5 Most Active Users:");

        var topUsers = userBorrowCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .collect(Collectors.toList());

        if (topUsers.isEmpty()) {
            System.out.println("No borrowing activity found.");
        } else {
            int rank = 1;
            for (var entry : topUsers) {
                User user = userRepository.findByUserId(entry.getKey()).orElse(null);
                if (user != null) {
                    System.out.printf("%d. %s %s\n", rank++, user.getFirstName(), user.getLastName());
                    System.out.printf("   User ID: %s\n", user.getUserId());
                    System.out.printf("   Total borrows: %d\n", entry.getValue());
                    System.out.printf("   Phone: %s\n", user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A");
                    System.out.println();
                }
            }
        }
    }
}
