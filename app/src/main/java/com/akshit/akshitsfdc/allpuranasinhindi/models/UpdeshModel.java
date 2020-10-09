package com.akshit.akshitsfdc.allpuranasinhindi.models;

import java.io.Serializable;
import java.util.ArrayList;

public class UpdeshModel implements Serializable {

    private String updeshakName;
    private String updeshakPicId;
    private String updeshTime;
    private String updesh;
    private ArrayList<String> likeList;
    private ArrayList<CommentModel> commentsList;
    private int shareCount;
    private long createdMilliseconds;

    public UpdeshModel() {
    }

    public String getUpdeshakName() {
        return updeshakName;
    }

    public void setUpdeshakName(String updeshakName) {
        this.updeshakName = updeshakName;
    }

    public String getUpdeshakPicId() {
        return updeshakPicId;
    }

    public void setUpdeshakPicId(String updeshakPicId) {
        this.updeshakPicId = updeshakPicId;
    }

    public String getUpdeshTime() {
        return updeshTime;
    }

    public void setUpdeshTime(String updeshTime) {
        this.updeshTime = updeshTime;
    }

    public String getUpdesh() {
        return updesh;
    }

    public void setUpdesh(String updesh) {
        this.updesh = updesh;
    }

    public ArrayList<String> getLikeList() {
        return likeList;
    }

    public void setLikeList(ArrayList<String> likeList) {
        this.likeList = likeList;
    }


    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public long getCreatedMilliseconds() {
        return createdMilliseconds;
    }

    public void setCreatedMilliseconds(long createdMilliseconds) {
        this.createdMilliseconds = createdMilliseconds;
    }

    public ArrayList<CommentModel> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(ArrayList<CommentModel> commentsList) {
        this.commentsList = commentsList;
    }
}
