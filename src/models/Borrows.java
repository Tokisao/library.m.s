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

    public Borrows(int id, int book_id, String user_id, Date borrowed_date, Date due_date, Date returned_day, int days_extended,  String status) {
        this.id=id;
        this.book_id = book_id;
        this.user_id=user_id;
        this.borrowed_date=borrowed_date;
        this.due_date=due_date;
        this.returned_day=returned_day;
        this.days_extended=days_extended;
        this.status=status;
    }
    @Override
    public String toString() {
        return "\nBorrow ID: " + id + "\nBorrowed Date: " + borrowed_date
                +"\nDays Extended: " + days_extended + "\nStatus: "+status+"\n";
    }

    public Date getDueDate(){
        return due_date;
    }

    public Date getReturnDate(){
        return returned_day;
    }

    public String getStatus(){
        return status;
    }

    public int getId(){
        return id;
    }

    public float getFineAmount(){
        return fine_amount;

    }

    public int getDaysExtended(){
        return days_extended;
    }

    public String getUserId() {
        return user_id;
    }

    public int getBookId() {
        return book_id;
    }
}
