package com.akshit.akshitsfdc.allpuranasinhindi.models;

import java.util.ArrayList;

public class BookDisplayCollectionModel {

    private ArrayList<BookDisplaySliderModel> bookDisplaySliders;
    private String headerTitle;

    public BookDisplayCollectionModel() {
    }

    public ArrayList<BookDisplaySliderModel> getBookDisplaySliders() {
        return bookDisplaySliders;
    }

    public void setBookDisplaySliders(ArrayList<BookDisplaySliderModel> bookDisplaySliders) {
        this.bookDisplaySliders = bookDisplaySliders;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }
}
