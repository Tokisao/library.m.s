package reports;

import dao.BookRepository;
import dao.BorrowsRepository;
import dao.UserRepository;
import models.User;

import java.util.List;
import java.util.stream.Collectors;

public class FinesReport extends BaseReport {

    public FinesReport(BookRepository bookRepository,
                       BorrowsRepository borrowsRepository,
                       UserRepository userRepository) {
        super(bookRepository, borrowsRepository, userRepository);
    }

    @Override
    public String getName() {
        return "Users with Fines Report";
    }

    @Override
    public void generate() {
        System.out.println("\n=== Users with Fines Report ===");

        List<User> allUsers = userRepository.findAll();
        List<User> usersWithFines = allUsers.stream()
                .filter(user -> user.getFines() > 0)
                .sorted((a, b) -> Float.compare(b.getFines(), a.getFines()))
                .collect(Collectors.toList());

        if (usersWithFines.isEmpty()) {
            System.out.println("No users with outstanding fines. Excellent!");
            return;
        }

        float totalFines = usersWithFines.stream()
                .map(User::getFines)
                .reduce(0.0f, Float::sum);

        System.out.println("\nUsers with Outstanding Fines:");
        System.out.println("Total users with fines: " + usersWithFines.size());
        System.out.printf("Total outstanding fines: %.2f tenge\n", totalFines);
        System.out.println();

        int count = 1;
        for (User user : usersWithFines) {
            System.out.printf("%d. %s %s\n", count++, user.getFirstName(), user.getLastName());
            System.out.printf("   User ID: %s\n", user.getUserId());
            System.out.printf("   Phone: %s\n", user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A");
            System.out.printf("   Fine amount: %.2f tenge\n", user.getFines());
            System.out.println();
        }

        System.out.println("Top Debtors:");
        int limit = Math.min(5, usersWithFines.size());
        for (int i = 0; i < limit; i++) {
            User user = usersWithFines.get(i);
            System.out.printf("%d. %s %s - %.2f tenge\n",
                    i + 1,
                    user.getFirstName(),
                    user.getLastName(),
                    user.getFines());
        }
    }
}
