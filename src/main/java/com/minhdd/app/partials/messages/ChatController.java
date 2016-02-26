package com.minhdd.app.partials.messages;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by minhdao on 25/02/16.
 */
@Controller
public class ChatController {

    @Inject
    RandomDataGenerator randomDataGenerator;

    @MessageMapping("/chat")
    @SendTo("/topic/message")
    public OutputMessage sendMessage(Message message) {
        return new OutputMessage(message, new Date());
    }

    @MessageMapping("/random")
    public void random() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
        for (int i=1; i<6; i++) {
            executorService.schedule(randomDataGenerator, i, TimeUnit.SECONDS);
        }
    }
}