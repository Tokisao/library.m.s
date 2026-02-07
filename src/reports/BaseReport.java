package reports;

import dao.BookRepository;
import dao.BorrowsRepository;
import dao.UserRepository;
import interfaces.Report;

public abstract class BaseReport implements Report {
    protected final BookRepository bookRepository;
    protected final BorrowsRepository borrowsRepository;
    protected final UserRepository userRepository;

    public BaseReport(BookRepository bookRepository,
                      BorrowsRepository borrowsRepository,
                      UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.borrowsRepository = borrowsRepository;
        this.userRepository = userRepository;
    }

    @Override
    public abstract String getName();

    @Override
    public abstract void generate();
}
