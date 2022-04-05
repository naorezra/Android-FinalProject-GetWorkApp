package com.shiranaor.GetWork.data.model;

public class Conversation {
    private String id;
    private String name;
    private String img;

    public Conversation(String id, String name, String img) {
        this.id = id;
        this.name = name;
        this.img = img;
    }

    public Conversation(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public boolean hasImage() {
        return this.img != null && this.img.length() > 0;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
