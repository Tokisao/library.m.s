package util;

import models.User;
import dao.UserRepository;
import java.util.Optional;
import java.util.Scanner;

public class Authentication {
    private final UserRepository userRepository;

    public Authentication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> login(Scanner scanner) {
        System.out.print("\nEnter your ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        Optional<User> user = userRepository.findByIdAndPassword(id, password);

        if (user.isPresent()) {
            System.out.println("Login successful!");
            System.out.println("Welcome, " + user.get().getFirstName() + " " +
                    user.get().getLastName() + " (" + user.get().getRole() + ")");
            return user;
        } else {
            System.out.println("Invalid credentials. Try again.");
            return Optional.empty();
        }
    }

    public Optional<User> login(String id, String password) {
        return userRepository.findByIdAndPassword(id, password);
    }
}
