package com.minhdd.app.partials.messages;

/**
 * Created by minhdao on 25/02/16.
 */
public class Message {
    private String message;
    private int id;

    public Message() {

    }

    public Message(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
