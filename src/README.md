# Library Management System

A console-based library management system built with Java and PostgreSQL.

## Features

### Book Management
- Add new books to the library
- View all books with availability status
- Update book information
- Delete books
- Search books by title, author, or genre

### User Management
- Register new library users
- View all registered users
- Update user information
- Activate/deactivate user accounts
- Search users by name, email, or library ID

### Borrowing System
- Borrow books with automatic due date calculation (14 days)
- Return books with fine calculation for overdue items
- View active borrowings
- View overdue books with fine details

### Reports
- Library statistics (total books, users, borrowings)
- Overdue books report
- Most popular books
- Active borrowings list

## Database Schema

The application uses 2 main tables with 1 relationship table:

1. **users** - Library users information
2. **books** - Book catalog information
3. **book_borrowings** - Relationship table for book borrowings

## Business Logic

1. **Borrowing Limits**: Users can borrow up to 5 books at a time
2. **Fine Calculation**: $0.50 per day for overdue books
3. **Availability Check**: Prevents borrowing unavailable books
4. **User Status**: Inactive users cannot borrow books
5. **Automatic Updates**: Available copies update automatically on borrow/return
