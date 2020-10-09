package com.akshit.akshitsfdc.allpuranasinhindi.models;

import java.util.ArrayList;

public class HardCopyDB {

    private int priority;
    private String language;
    private String type;
    private ArrayList<HardCopyModel> bookCollection;

    public HardCopyDB() {
    }

    public HardCopyDB(int priority, String language, String type, ArrayList<HardCopyModel> bookCollection) {
        this.setPriority(priority);
        this.setLanguage(language);
        this.setType(type);
        this.setBookCollection(bookCollection);
    }


    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<HardCopyModel> getBookCollection() {
        return bookCollection;
    }

    public void setBookCollection(ArrayList<HardCopyModel> bookCollection) {
        this.bookCollection = bookCollection;
    }
}
