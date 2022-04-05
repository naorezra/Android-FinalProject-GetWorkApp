package com.shiranaor.GetWork.data.model;

import java.util.Date;

public class ChatMessage {

    private String imgSrc;
    private String messageText;
    private String messageUser;
    private long messageTime;

    public ChatMessage(String messageText, String messageUser,String imgSrc ) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.imgSrc = imgSrc;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessage(){
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}