package models;

public class Book {
    private int book_id;
    private String name;
    private String author;
    private String description;
    private int available_copies;
    private int total_copies;

    public Book(int book_id, String name, String author, String description, int available_copies) {
        this.book_id = book_id;
        this.name = name;
        this.author=author;
        this.description=description;
        this.available_copies=available_copies;
    }
    @Override
    public String toString() {
        return "Book name: " + name + "\nAuthor: " + author + "\nDescription: " + description +"\nAvailable copies: " + available_copies;
    }

    public Integer getAvailableCopies(){
        return available_copies;
    }
    public Integer getTotalCopies() {
        return total_copies;
    }

}
