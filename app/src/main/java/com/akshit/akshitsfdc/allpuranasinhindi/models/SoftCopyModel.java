package com.akshit.akshitsfdc.allpuranasinhindi.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class SoftCopyModel implements  Parcelable {

    private String picUrl;
    private String name;
    private String language;
    private boolean free;
    private float price;
    private String downloadUrl;
    private String description;
    private String fileName;
    private String pages;
    private String bookId;
    private boolean videoOption;
    private ArrayList<String> bookParts;
    private boolean booksInPart;
    private boolean isOneOfThePart;
    private int priority;
    private String type;
    private ArrayList<String> searchKeywords;
    private List<String> displayKeys;
    private String category;
    private long addedTime;
    private boolean favorite;
    private int readingPage;
    private Uri bookUri;
    private Uri coverUri;
    private String horizontalImage;


    public SoftCopyModel() {
    }

    protected SoftCopyModel(Parcel in) {
        picUrl = in.readString();
        name = in.readString();
        language = in.readString();
        free = in.readByte() != 0;
        price = in.readFloat();
        downloadUrl = in.readString();
        description = in.readString();
        fileName = in.readString();
        pages = in.readString();
        bookId = in.readString();
        videoOption = in.readByte() != 0;
        bookParts = in.createStringArrayList();
        booksInPart = in.readByte() != 0;
        isOneOfThePart = in.readByte() != 0;
        priority = in.readInt();
        type = in.readString();
        searchKeywords = in.createStringArrayList();
        category = in.readString();
        addedTime = in.readLong();
        favorite = in.readByte() != 0;
        readingPage = in.readInt();
        bookUri = in.readParcelable(Uri.class.getClassLoader());
        coverUri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<SoftCopyModel> CREATOR = new Creator<SoftCopyModel>() {
        @Override
        public SoftCopyModel createFromParcel(Parcel in) {
            return new SoftCopyModel(in);
        }

        @Override
        public SoftCopyModel[] newArray(int size) {
            return new SoftCopyModel[size];
        }
    };

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(picUrl);
        dest.writeString(name);
        dest.writeString(language);
        dest.writeByte((byte) (free ? 1 : 0));
        dest.writeFloat(price);
        dest.writeString(downloadUrl);
        dest.writeString(description);
        dest.writeString(fileName);
        dest.writeString(pages);
        dest.writeString(bookId);
        dest.writeByte((byte) (videoOption ? 1 : 0));
        dest.writeStringList(bookParts);
        dest.writeByte((byte) (booksInPart ? 1 : 0));
        dest.writeByte((byte) (isOneOfThePart ? 1 : 0));
        dest.writeInt(priority);
        dest.writeString(type);
        dest.writeStringList(searchKeywords);
        dest.writeString(category);
        dest.writeLong(addedTime);
        dest.writeByte((byte) (favorite ? 1 : 0));
        dest.writeInt(readingPage);
        dest.writeParcelable(bookUri, flags);
        dest.writeParcelable(coverUri, flags);
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(ArrayList<String> searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    public boolean isOneOfThePart() {
        return isOneOfThePart;
    }

    public void setOneOfThePart(boolean oneOfThePart) {
        isOneOfThePart = oneOfThePart;
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

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public boolean isVideoOption() {
        return videoOption;
    }

    public void setVideoOption(boolean videoOption) {
        this.videoOption = videoOption;
    }

    public ArrayList<String> getBookParts() {
        return bookParts;
    }

    public void setBookParts(ArrayList<String> bookParts) {
        this.bookParts = bookParts;
    }

    public boolean isBooksInPart() {
        return booksInPart;
    }

    public void setBooksInPart(boolean booksInPart) {
        this.booksInPart = booksInPart;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(long addedTime) {
        this.addedTime = addedTime;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getReadingPage() {
        return readingPage;
    }

    public void setReadingPage(int readingPage) {
        this.readingPage = readingPage;
    }

    public Uri getBookUri() {
        return bookUri;
    }

    public void setBookUri(Uri bookUri) {
        this.bookUri = bookUri;
    }

    public Uri getCoverUri() {
        return coverUri;
    }

    public void setCoverUri(Uri coverUri) {
        this.coverUri = coverUri;
    }

    public List<String> getDisplayKeys() {
        return displayKeys;
    }

    public void setDisplayKeys(List<String> displayKeys) {
        this.displayKeys = displayKeys;
    }

    public String getHorizontalImage() {
        return horizontalImage;
    }

    public void setHorizontalImage(String horizontalImage) {
        this.horizontalImage = horizontalImage;
    }
}
