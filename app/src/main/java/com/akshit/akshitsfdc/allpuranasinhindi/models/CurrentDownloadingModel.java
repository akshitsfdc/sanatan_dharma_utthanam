package com.akshit.akshitsfdc.allpuranasinhindi.models;

public class CurrentDownloadingModel {

    private String picUrl;
    private String bookName;

    public CurrentDownloadingModel() {
    }

    public CurrentDownloadingModel(String picUrl, String bookName) {
        this.picUrl = picUrl;
        this.bookName = bookName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
}
