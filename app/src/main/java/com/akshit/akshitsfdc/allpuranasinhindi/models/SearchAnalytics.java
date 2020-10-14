package com.akshit.akshitsfdc.allpuranasinhindi.models;

public class SearchAnalytics {

    private String userId;
    private String email;
    private String name;
    private boolean prime;
    private long timestamp;
    private String searchedKeyword;


    public SearchAnalytics() {
    }


    public boolean isPrime() {
        return prime;
    }

    public void setPrime(boolean prime) {
        this.prime = prime;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSearchedKeyword() {
        return searchedKeyword;
    }

    public void setSearchedKeyword(String searchedKeyword) {
        this.searchedKeyword = searchedKeyword;
    }

}
