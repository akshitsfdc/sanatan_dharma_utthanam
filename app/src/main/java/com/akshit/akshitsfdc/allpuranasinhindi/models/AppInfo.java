package com.akshit.akshitsfdc.allpuranasinhindi.models;

import java.util.ArrayList;
import java.util.List;

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
    private List<DisplayModel> displayModels;
    private int primePrice;
    private ArrayList<BookDisplayCollectionModel> bookDisplayCollection;
    private boolean saveSearchAnalytics;
    private String homeBannerId;
    private String bookAccessAdId;
    private String downloadingGapAdId;
    private boolean allowPrime;
    private boolean showIndividualPrice;

    public AppInfo() {
    }


    public boolean isSaveSearchAnalytics() {
        return saveSearchAnalytics;
    }

    public void setSaveSearchAnalytics(boolean saveSearchAnalytics) {
        this.saveSearchAnalytics = saveSearchAnalytics;
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

    public String getHomeBannerId() {
        return homeBannerId;
    }

    public void setHomeBannerId(String homeBannerId) {
        this.homeBannerId = homeBannerId;
    }

    public String getBookAccessAdId() {
        return bookAccessAdId;
    }

    public void setBookAccessAdId(String bookAccessAdId) {
        this.bookAccessAdId = bookAccessAdId;
    }

    public String getDownloadingGapAdId() {
        return downloadingGapAdId;
    }

    public void setDownloadingGapAdId(String downloadingGapAdId) {
        this.downloadingGapAdId = downloadingGapAdId;
    }

    public boolean isAllowPrime() {
        return allowPrime;
    }

    public void setAllowPrime(boolean allowPrime) {
        this.allowPrime = allowPrime;
    }

    public boolean isShowIndividualPrice() {
        return showIndividualPrice;
    }

    public void setShowIndividualPrice(boolean showIndividualPrice) {
        this.showIndividualPrice = showIndividualPrice;
    }

    public List<DisplayModel> getDisplayModels() {
        return displayModels;
    }

    public void setDisplayModels(List<DisplayModel> displayModels) {
        this.displayModels = displayModels;
    }
}
