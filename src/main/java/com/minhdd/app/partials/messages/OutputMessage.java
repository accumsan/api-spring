package com.minhdd.app.partials.messages;

import java.util.Date;

/**
 * Created by minhdao on 25/02/16.
 */
public class OutputMessage extends Message {

    private Date time;

    public OutputMessage(Message original, Date time) {
        super(original.getId(), original.getMessage());
        this.time = time;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
