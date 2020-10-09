package com.akshit.akshitsfdc.allpuranasinhindi.models;

public class SliderItem {

    private String description;
    private String imageUrl;
    private boolean externalLink;
    private String externalUrl;

    public SliderItem() {
    }

    public SliderItem(String description, String imageUrl, boolean externalLink, String externalUrl) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.externalLink = externalLink;
        this.externalUrl = externalUrl;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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
