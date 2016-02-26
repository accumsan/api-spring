package com.minhdd.app.partials.websocket;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by minhdao on 26/02/16.
 */
public class RandomSender implements Runnable {
    final Logger logger = LoggerFactory.getLogger(RandomSender.class);

    private int length;
    private WebSocketSession session;

    public RandomSender(int length, WebSocketSession session) {
        this.length = length;
        this.session = session;
    }

    @Override
    public void run() {
        try {
            session.sendMessage(new TextMessage(RandomStringUtils.randomAlphanumeric(length)));
        } catch (IOException e) {
            logger.error("Error on sending random message", e);
        }
    }
}
