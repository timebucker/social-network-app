package com.example.kokolo.socialnetwork.models.chat;

public class Messages {
    public String data, time, type, message, from;

    public Messages(){

    }

    public Messages(String data, String time, String type, String message, String from) {
        this.data = data;
        this.time = time;
        this.type = type;
        this.message = message;
        this.from = from;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
