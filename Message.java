package com.example.manav.pingme;

/**
 * Created by manav on 8/1/17.
 */

public class Message {
    private String sender;
    private String message;
    private String receiver;

    public Message() {
    }

    public Message(String sender, String message, String receiver) {
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Message(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
