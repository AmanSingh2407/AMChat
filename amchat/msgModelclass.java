package com.example.amchat;

public class msgModelclass {

    String message;
    String senderid;
    long timestamp;

    public msgModelclass(String message, String senderUID, long time) {
    }

    public msgModelclass(String message) {
        this.message = message;
        this.senderid = senderid;
        this.timestamp = timestamp;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getSenderid() {
        return senderid;
    }
    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
