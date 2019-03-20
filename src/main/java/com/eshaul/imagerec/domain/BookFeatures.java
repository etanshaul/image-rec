package com.eshaul.imagerec.domain;

public class BookFeatures {
    private String title;
    private String description;
    private String author;
    private String publishedDate;

    public BookFeatures(String title) {
        this.title = title;
    }

    public BookFeatures(String title, String description, String author, String publishedDate) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.publishedDate = publishedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }
}
