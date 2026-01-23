package com.library.model;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private int publicationYear;
    private int totalCopies;
    private int availableCopies;
    private String genre;
    private String language;
    private int pages;
    private String description;
    private String location;
    private double rating;
    private int timesBorrowed;

    public Book() {}

    public Book(String title, String author, String isbn, String publisher,
                int publicationYear, int totalCopies, String genre,
                String language, int pages, String description, double rating) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.genre = genre;
        this.language = language;
        this.pages = pages;
        this.description = description;
        this.location = "Shelf-" + (char)('A' + (int)(Math.random() * 5)) + (int)(Math.random() * 100);
        this.rating = rating;
        this.timesBorrowed = 0;
    }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
        if (availableCopies > totalCopies) {
            availableCopies = totalCopies;
        }
    }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) {
        if (availableCopies <= totalCopies && availableCopies >= 0) {
            this.availableCopies = availableCopies;
        }
    }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getTimesBorrowed() { return timesBorrowed; }
    public void setTimesBorrowed(int timesBorrowed) { this.timesBorrowed = timesBorrowed; }

    public void borrowCopy() {
        if (availableCopies > 0) {
            availableCopies--;
            timesBorrowed++;
        }
    }

    public void returnCopy() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public double getAvailabilityPercentage() {
        if (totalCopies == 0) return 0;
        return (double) availableCopies / totalCopies * 100;
    }

    public void updateRating(double newRating) {
        if (timesBorrowed > 0) {
            this.rating = ((this.rating * timesBorrowed) + newRating) / (timesBorrowed + 1);
        } else {
            this.rating = newRating;
        }
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %-3d | %-25s | Author: %-15s | Available: %d/%d | Rating: %.1f ",
                bookId,
                title.length() > 25 ? title.substring(0, 22) + "..." : title,
                author.length() > 15 ? author.substring(0, 12) + "..." : author,
                availableCopies,
                totalCopies,
                rating
        );
    }

    public String toDetailedString() {
        return String.format(
                "\n═══════════════════════════════════════════════════════════════════\n" +
                        "BOOK DETAILS\n" +
                        "═══════════════════════════════════════════════════════════════════\n" +
                        "ID:                 %d\n" +
                        "Title:              %s\n" +
                        "Author:             %s\n" +
                        "ISBN:               %s\n" +
                        "Publisher:          %s\n" +
                        "Publication Year:   %d\n" +
                        "Copies:             %d available / %d total (%.1f%%)\n" +
                        "Genre:              %s\n" +
                        "Language:           %s\n" +
                        "Pages:              %d\n" +
                        "Location:           %s\n" +
                        "Rating:             %.2f \n" +
                        "Times Borrowed:     %d\n" +
                        "Description:        %s\n" +
                        "═══════════════════════════════════════════════════════════════════",
                bookId, title, author, isbn, publisher, publicationYear,
                availableCopies, totalCopies, getAvailabilityPercentage(),
                genre, language, pages, location, rating, timesBorrowed, description
        );
    }

    public String toCompactString() {
        return String.format("Book{id=%d, title='%s', author='%s', available=%d/%d}",
                bookId, title, author, availableCopies, totalCopies);
    }
}