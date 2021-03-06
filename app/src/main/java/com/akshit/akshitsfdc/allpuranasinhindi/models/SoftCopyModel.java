package com.akshit.akshitsfdc.allpuranasinhindi.models;

import java.io.Serializable;
import java.util.ArrayList;

public class SoftCopyModel implements Serializable {

    private String picUrl;
    private String name;
    private String language;
    private boolean free;
    private float price;
    private String downloadUrl;
    private String description;
    private String fileName;
    private String pages;
    private String bookId;
    private boolean videoOption;
    private ArrayList<String> bookParts;
    private boolean booksInPart;
    private boolean isOneOfThePart;
    private int priority;
    private String type;
    private ArrayList<String> searchKeywords;


    public SoftCopyModel() {
    }

    public SoftCopyModel(String picUrl, String name, String language, boolean free, float price, String downloadUrl,
                         String description, String fileName, String pages, String bookId, boolean videoOption, ArrayList<String> bookParts,
                         boolean booksInPart)
    {
        this.setPicUrl(picUrl);
        this.setName(name);
        this.setLanguage(language);
        this.setFree(free);
        this.setPrice(price);
        this.setDownloadUrl(downloadUrl);
        this.setDescription(description);
        this.setFileName(fileName);
        this.setPages(pages);
        this.setBookId(bookId);
        this.setVideoOption(videoOption);
        this.setBookParts(bookParts);
        this.setBooksInPart(booksInPart);
    }


    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(ArrayList<String> searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    public boolean isOneOfThePart() {
        return isOneOfThePart;
    }

    public void setOneOfThePart(boolean oneOfThePart) {
        isOneOfThePart = oneOfThePart;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }



    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public boolean isVideoOption() {
        return videoOption;
    }

    public void setVideoOption(boolean videoOption) {
        this.videoOption = videoOption;
    }

    public ArrayList<String> getBookParts() {
        return bookParts;
    }

    public void setBookParts(ArrayList<String> bookParts) {
        this.bookParts = bookParts;
    }

    public boolean isBooksInPart() {
        return booksInPart;
    }

    public void setBooksInPart(boolean booksInPart) {
        this.booksInPart = booksInPart;
    }
}
