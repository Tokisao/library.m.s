# Library Management System

A console-based library management system built with Java and PostgreSQL.
Creators: Zharylgapova Dayana, Alish Medina, Nurtas Akmarzhan, Asylkhan Tolganay

## Features

### Book Management
- View all books with availability status

### User Management
- Update user information

### Borrowing System
- Borrow books with automatic due date calculation (14 days)
- View active borrowings
- View overdue books with fine details

## Database Schema

The application uses 2 main tables with 1 relationship table:

1. **users** - Library users information
2. **books** - Book catalog information
3. **book_borrowings** - Relationship table for book borrowings

1. **Borrowing Limits**: Users can borrow up to 5 books at a time
2. **Fine Calculation**: $0.50 per day for overdue books
3. **Availability Check**: Prevents borrowing unavailable books
4. **User Status**: Inactive users cannot borrow books
5. **Automatic Updates**: Available copies update automatically on borrow/return
