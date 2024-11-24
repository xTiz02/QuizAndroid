package com.prd.quizzoapp.model.entity;

public class UserMessage {
    private String message;
    private String senderId;
    private long timeStamp;

    public UserMessage() {
    }

    public UserMessage(String message, long timeStamp, String senderId) {
        this.message = message;
        this.timeStamp = timeStamp;
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    @Override
    public String toString() {
        return "UserMessage{" +
                "message='" + message + '\'' +
                ", senderId='" + senderId + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
