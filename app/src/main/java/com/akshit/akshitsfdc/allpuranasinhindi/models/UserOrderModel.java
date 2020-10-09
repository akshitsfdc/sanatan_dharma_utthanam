package com.akshit.akshitsfdc.allpuranasinhindi.models;

import java.io.Serializable;

public class UserOrderModel implements Serializable {

    private HardCopyModel book;
    private AddressModel address;
    private String status;
    private boolean paid;
    private String orderDate;
    private String orderTime;
    private String email;
    private String phone;
    private String name;
    private String orderId;
    private String uId;

    public UserOrderModel() {
    }

    public UserOrderModel(HardCopyModel book, AddressModel address, String status, boolean paid, String orderDate, String orderTime, String email, String phone, String name, String orderId, String uId) {
        this.book = book;
        this.address = address;
        this.status = status;
        this.paid = paid;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.setEmail(email);
        this.setPhone(phone);
        this.setName(name);
        this.setOrderId(orderId);
        this.setuId(uId);
    }

    public HardCopyModel getBook() {
        return book;
    }

    public void setBook(HardCopyModel book) {
        this.book = book;
    }

    public AddressModel getAddress() {
        return address;
    }

    public void setAddress(AddressModel address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}
