package com.akshit.akshitsfdc.allpuranasinhindi.models;

import java.io.Serializable;
import java.util.ArrayList;

public class UserDataModel implements Serializable {

    private String name;
    private String email;
    private String uId;
    private ArrayList<SoftCopyModel> purchasedBooks;
    private boolean primeMember;

    public UserDataModel() {
    }

    public UserDataModel(String name, String email, String uId, ArrayList<SoftCopyModel> purchasedBooks, boolean primeMember) {
        this.setName(name);
        this.setEmail(email);
        this.setuId(uId);
        this.setPurchasedBooks(purchasedBooks);
        this.setPrimeMember(primeMember);
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

    public ArrayList<SoftCopyModel> getPurchasedBooks() {
        return purchasedBooks;
    }

    public void setPurchasedBooks(ArrayList<SoftCopyModel> purchasedBooks) {
        this.purchasedBooks = purchasedBooks;
    }

    public boolean isPrimeMember() {
        return primeMember;
    }

    public void setPrimeMember(boolean primeMember) {
        this.primeMember = primeMember;
    }
}
