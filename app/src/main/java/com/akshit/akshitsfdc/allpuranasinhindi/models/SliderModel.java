package com.akshit.akshitsfdc.allpuranasinhindi.models;

public class SliderModel {

    private String moveTo;
    private String imageUrl;
    private boolean externalLink;
    private String externalUrl;

    public SliderModel() {
    }

    public SliderModel(String moveTo, String imageUrl, boolean externalLink, String externalUrl) {
        this.moveTo = moveTo;
        this.imageUrl = imageUrl;
        this.externalLink = externalLink;
        this.externalUrl = externalUrl;
    }

    public String getMoveTo() {
        return moveTo;
    }

    public void setMoveTo(String moveTo) {
        this.moveTo = moveTo;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isExternalLink() {
        return externalLink;
    }

    public void setExternalLink(boolean externalLink) {
        this.externalLink = externalLink;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }
}
