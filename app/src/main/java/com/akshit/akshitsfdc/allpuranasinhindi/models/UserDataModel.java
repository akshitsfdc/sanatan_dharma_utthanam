package com.akshit.akshitsfdc.allpuranasinhindi.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserDataModel implements Serializable {

    private String name;
    private String email;
    private String uId;
    private List<SoftCopyModel> purchasedBooks;
    private boolean primeMember;
    private long createdTime;
    private String phone;
    private String photoUrl;

    public UserDataModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }


    public boolean isPrimeMember() {
        return primeMember;
    }

    public void setPrimeMember(boolean primeMember) {
        this.primeMember = primeMember;
    }


    public List<SoftCopyModel> getPurchasedBooks() {
        return purchasedBooks;
    }

    public void setPurchasedBooks(List<SoftCopyModel> purchasedBooks) {
        this.purchasedBooks = purchasedBooks;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
