package com.shiranaor.GetWork.data.model;

public class Upload {
    private String mName;
    private String mImageUrl;
    private String owner;
    private String id;
    private String uploadsId;

    public Upload(String name, String imageUrl, String owner, String uploadsId) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        mName = name;
        mImageUrl = imageUrl;
        this.owner = owner;
        this.uploadsId = uploadsId;
    }

    public Upload() {
        //empty constructor needed
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUploadsId() {
        return uploadsId;
    }

    public void setUploadsId(String uploadsId) {
        this.uploadsId = uploadsId;
    }
}