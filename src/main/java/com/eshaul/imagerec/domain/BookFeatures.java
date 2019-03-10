package com.eshaul.imagerec.domain;

public class BookFeatures {
    private String title;
    private String description;
    private String author;
    private String publishedDate;

    public BookFeatures(String title, String description, String author, String publishedDate) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.publishedDate = publishedDate;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublishedDate() {
        return publishedDate;
    }
}
