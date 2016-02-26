package com.minhdd.app.partials.messages;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Date;
import java.util.Random;

/**
 * Created by minhdao on 26/02/16.
 */
@Component
public class RandomDataGenerator implements Runnable {

    private final MessageSendingOperations<String> messagingTemplate;

    @Inject
    public RandomDataGenerator(
            final MessageSendingOperations<String> messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void run() {
        int random = new Random().nextInt(100);
        String text = RandomStringUtils.randomAlphanumeric(100);
        Message message = new Message(random, text);
        this.messagingTemplate.convertAndSend("/topic/message", new OutputMessage(message, new Date()));
    }
}