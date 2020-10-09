package com.akshit.akshitsfdc.allpuranasinhindi.models;

import java.util.ArrayList;

public class AppInfo {

    private String latestVersion;
    private String feature;
    private boolean forceUpdate;
    private boolean abortApp;
    private String header;
    private String okText;
    private String cancelText;
    private boolean checkVersionPopup;
    private String paymentApiProduction;
    private String paymentApiSandbox;
    private ArrayList<SliderModel> bannerUrls;
    private int primePrice;

    private ArrayList<BookDisplayCollectionModel> bookDisplayCollection;

    public AppInfo() {
    }


    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public boolean isAbortApp() {
        return abortApp;
    }

    public void setAbortApp(boolean abortApp) {
        this.abortApp = abortApp;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getOkText() {
        return okText;
    }

    public void setOkText(String okText) {
        this.okText = okText;
    }

    public String getCancelText() {
        return cancelText;
    }

    public void setCancelText(String cancelText) {
        this.cancelText = cancelText;
    }

    public boolean isCheckVersionPopup() {
        return checkVersionPopup;
    }

    public void setCheckVersionPopup(boolean checkVersionPopup) {
        this.checkVersionPopup = checkVersionPopup;
    }

    public String getPaymentApiProduction() {
        return paymentApiProduction;
    }

    public void setPaymentApiProduction(String paymentApiProduction) {
        this.paymentApiProduction = paymentApiProduction;
    }

    public String getPaymentApiSandbox() {
        return paymentApiSandbox;
    }

    public void setPaymentApiSandbox(String paymentApiSandbox) {
        this.paymentApiSandbox = paymentApiSandbox;
    }

    public ArrayList<SliderModel> getBannerUrls() {
        return bannerUrls;
    }

    public void setBannerUrls(ArrayList<SliderModel> bannerUrls) {
        this.bannerUrls = bannerUrls;
    }

    public int getPrimePrice() {
        return primePrice;
    }

    public void setPrimePrice(int primePrice) {
        this.primePrice = primePrice;
    }

    public ArrayList<BookDisplayCollectionModel> getBookDisplayCollection() {
        return bookDisplayCollection;
    }

    public void setBookDisplayCollection(ArrayList<BookDisplayCollectionModel> bookDisplayCollection) {
        this.bookDisplayCollection = bookDisplayCollection;
    }
}
