package util;

import models.Book;
import models.Borrows;
import models.User;
import dao.BookRepository;
import dao.BorrowsRepository;
import dao.UserRepository;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class LibraryService {
    private final BookRepository bookRepository;
    private final BorrowsRepository borrowRepository;
    private final UserRepository userRepository;
    public User currentUser;

    public LibraryService(BookRepository bookRepository, BorrowsRepository borrowRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.borrowRepository = borrowRepository;
        this.userRepository = userRepository;
    }



    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void showAllBooks() {
        List<Book> books = bookRepository.findAll();

        if (books.isEmpty()) {
            System.out.println("No books available.");
            return;
        }

        System.out.println("\n~~~ Books ~~~");
        for (Book book : books) {
            System.out.println(book);
        }
    }


    public void viewMyBorrows() {
        if (currentUser == null) {
            System.out.println("Please login first!");
            return;
        }

        List<Borrows> borrows = borrowRepository.findByUserId(currentUser.getUserId());

        if (borrows.isEmpty()) {
            System.out.println("You have no borrowed books.");
            return;
        }

        System.out.println("\n~~~ Your Borrowed Books ~~~");
        for (Borrows borrow : borrows) {
            System.out.println(borrow);
        }
    }

    public void showUserInfo() {
        if (currentUser == null) {
            System.out.println("Please login first!");
            return;
        }

        System.out.println(currentUser);
    }

    public void borrowBook(Scanner scanner) {
        if (currentUser == null) {
            System.out.println("Please login first!");
            return;
        }

        System.out.print("Enter book ID to borrow: ");
        int bookId;
        try {
            bookId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid book ID!");
            return;
        }

        Book book = bookRepository.findById(bookId);
        if (book == null) {
            System.out.println("Book not found!");
            return;
        }

        if (book.getAvailableCopies() <= 0) {
            System.out.println("No copies available!");
            return;
        }

        int userBorrowedCount = borrowRepository.getUserBorrowedCount(currentUser.getUserId());
        if (userBorrowedCount >= 5) {
            System.out.println("You have reached the borrowing limit (5 books)!");
            return;
        }

        if (borrowRepository.hasOverdueBooks(currentUser.getUserId())) {
            System.out.println("You have overdue books! Please return them first.");
            return;
        }

        if (borrowRepository.isBookBorrowedByUser(bookId, currentUser.getUserId())) {
            System.out.println("You have already borrowed this book!");
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 14);
        Date dueDate = calendar.getTime();

        boolean borrowCreated = borrowRepository.createBorrow(bookId, currentUser.getUserId(), dueDate);


        if (borrowCreated) {
            bookRepository.updateAvailableCopies(bookId, -1);
            System.out.println("Book borrowed successfully!");
            System.out.println("Due date: " + dueDate);
            System.out.println("You have borrowed " + (userBorrowedCount + 1) + " out of 5 books.");
        } else {
            System.out.println("Failed to borrow book.");
        }




    }

}
