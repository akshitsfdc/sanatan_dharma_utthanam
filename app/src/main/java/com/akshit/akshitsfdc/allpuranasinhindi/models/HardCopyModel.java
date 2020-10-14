package com.akshit.akshitsfdc.allpuranasinhindi.models;

import java.io.Serializable;
import java.util.ArrayList;

public class HardCopyModel implements Serializable {

    private String picUrl;
    private ArrayList<String> allPicsUrls;
    private String name;
    private String language;
    private float price;
    private float discount;
    private float deliveryCharge;
    private boolean available;
    private String bookId;
    private int stock;
    private String merchant;
    private String description;
    private String pages;
    private boolean isBook;
    private String material;
    private ArrayList<String> searchKeywords;
    private String type;
    private int priority;


    public HardCopyModel() {
    }

    public HardCopyModel(String picUrl, String name, String language, float price, float discount, float deliveryCharge, boolean available, String bookId, int stock, String merchant, ArrayList<String>  allPicsUrls, String description, String pages) {
        this.setPicUrl(picUrl);
        this.setName(name);
        this.setLanguage(language);
        this.setPrice(price);
        this.setDiscount(discount);
        this.setDeliveryCharge(deliveryCharge);
        this.setAvailable(available);
        this.setBookId(bookId);
        this.setStock(stock);
        this.setMerchant(merchant);
        this.setAllPicsUrls(allPicsUrls);
        this.setDescription(description);
        this.setPages(pages);
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

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(float deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public ArrayList<String> getAllPicsUrls() {
        return allPicsUrls;
    }

    public void setAllPicsUrls(ArrayList<String> allPicsUrls) {
        this.allPicsUrls = allPicsUrls;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public boolean isIsBook() {
        return isBook;
    }

    public void setIsBook(boolean book) {
        isBook = book;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public ArrayList<String> getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(ArrayList<String> searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
