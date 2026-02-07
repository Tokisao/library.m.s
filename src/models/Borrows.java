package models;

import java.util.Date;

public class Borrows {
    private int id;
    private int book_id;
    private String user_id;
    private Date borrowed_date;
    private Date due_date;
    private Date returned_day;
    private int days_extended;
    private float fine_amount;
    private String status;

    public Borrows(int id, int book_id, String user_id, Date borrowed_date, Date due_date, Date returned_day, int days_extended, String status) {
        this.id = id;
        this.book_id = book_id;
        this.user_id = user_id;
        this.borrowed_date = borrowed_date;
        this.due_date = due_date;
        this.returned_day = returned_day;
        this.days_extended = days_extended;
        this.status = status;
    }

    @Override
    public String toString() {
        return "\nBorrow ID: " + id + "\nBorrowed Date: " + borrowed_date
                + "\nDays Extended: " + days_extended + "\nStatus: " + status + "\n";
    }

    public Date getDueDate() {
        return due_date;
    }

    public Date getReturnDate() {
        return returned_day;
    }

    public String getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public float getFineAmount() {
        return fine_amount;
    }

    public int getDaysExtended() {
        return days_extended;
    }

    public String getUserId() {
        return user_id;
    }

    public int getBookId() {
        return book_id;
    }

    public static class BorrowDetails {
        private int id;
        private int book_id;
        private String name;
        private String author;
        private String category;
        private String user_id;
        private String first_name;
        private String second_name;
        private String phone_number;
        private Date borrowed_date;
        private Date due_date;
        private Date returned_date;
        private int days_extended;
        private float fine_amount;
        private String status;

        public BorrowDetails(int id, int book_id, String name, String author,
                             String category, String user_id, String first_name,
                             String second_name, String phone_number, Date borrowed_date,
                             Date due_date, Date returned_date, int days_extended,
                             float fine_amount, String status) {
            this.id= id;
            this.book_id = book_id;
            this.name = name;
            this.author = author;
            this.category = category;
            this.user_id = user_id;
            this.first_name = first_name;
            this.second_name = second_name;
            this.phone_number = phone_number;
            this.borrowed_date = borrowed_date;
            this.due_date = due_date;
            this.returned_date = returned_date;
            this.days_extended = days_extended;
            this.fine_amount = fine_amount;
            this.status = status;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("\n=== Borrow Details ===\n");
            sb.append("Borrow ID: ").append(id).append("\n");
            sb.append("Book: ").append(name).append(" by ").append(author);
            if (category != null && !category.isEmpty()) {
                sb.append(" (").append(category).append(")");
            }
            sb.append("\n");
            sb.append("User: ").append(first_name).append(" ").append(second_name);
            sb.append(" (ID: ").append(user_id).append(", Phone: ").append(phone_number).append(")\n");
            sb.append("Borrowed: ").append(borrowed_date).append("\n");
            sb.append("Due Date: ").append(due_date).append("\n");
            if (returned_date != null) {
                sb.append("Returned: ").append(returned_date).append("\n");
            }
            if (days_extended > 0) {
                sb.append("Days Extended: ").append(days_extended).append("\n");
            }
            if (fine_amount > 0) {
                sb.append("Fine: ").append(String.format("%.2f", fine_amount)).append(" тг\n");
            }
            sb.append("Status: ").append(status).append("\n");
            return sb.toString();
        }
    }

}
