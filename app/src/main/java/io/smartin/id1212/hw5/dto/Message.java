package io.smartin.id1212.hw5.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sam on 2017-12-17.
 */

public class Message {
    private String from;
    private String message;
    private long timestamp;

    public Message() {
    }

    public Message(String from, String message) {
        this.from = from;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFormattedTime() {
        return new SimpleDateFormat("HH:mm").format(new Date(timestamp));
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
