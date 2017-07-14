package com.example.android.booklistingapp;

import java.util.ArrayList;

/**
 * This class represents a book entry.
 */

public class Book {

    /* Book Title */
    String title;

    /* Book Author */
    ArrayList<String> authors;

    /* Book Description */
    String description;

    /* Book Image URL */
    String bookImageUrl;

    /* Book Publisher */
    String publisher;

    /* Book Publish Date */
    String publishDate;

    /* Constructor */

    public Book(String title, ArrayList<String> authors, String description, String bookImageUrl, String publisher, String publishDate) {
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.bookImageUrl = bookImageUrl;
        this.publisher = publisher;
        this.publishDate = publishDate;
    }

    /** Title getter */
    public String getTitle() {
        return title;
    }

    /** Author getter */
    public ArrayList<String> getAuthor() {
        return authors;
    }

    /** Book description getter */
    public String getDescription() {
        return description;
    }

    /** Book Image Url getter */
    public String getBookImageUrl() {
        return bookImageUrl;
    }

    /** Publisher getter */
    public String getPublisher() {
        return publisher;
    }

    /** Publish date getter */
    public String getPublishDate() {
        return publishDate;
    }

    public String getAuthorsLine(){
        StringBuilder builder = new StringBuilder();
        for (String author : authors){
            builder.append(author + ", ");
        }
        builder.delete(builder.length() - 2, builder.length() - 1);
        return builder.toString();
    }

}
