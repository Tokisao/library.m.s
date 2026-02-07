package models;

public class Book {
    private int book_id;
    private String name;
    private String author;
    private String description;
    private int available_copies;
    private int total_copies;
    private String category;

    public Book(int book_id, String name, String author, String description, int available_copies, int total_copies, String category) {
        this.book_id = book_id;
        this.name = name;
        this.author=author;
        this.description=description;
        this.available_copies=available_copies;
        this.total_copies = total_copies;
        this.category=category;
    }
    @Override
    public String toString() {
        return "\n\nBook ID: " +book_id + "\nBook name: " + name + "\nAuthor: " + author + "\nDescription: " + description +"\nAvailable copies: " + available_copies + "\nCategory: " + category;
    }

    public Integer getAvailableCopies(){
        return available_copies;
    }
    public Integer getTotalCopies() {
        return total_copies;
    }
    public String getName(){
        return name;
    }
    public String getAuthor(){
        return author;
    }
    public String getDescription(){
        return description;
    }
    public String getCategory(){
        return category;
    }
    public Integer getBorrowedCopies(){return total_copies-available_copies;}
}
