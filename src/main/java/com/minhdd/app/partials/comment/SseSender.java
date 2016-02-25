package com.minhdd.app.partials.comment;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by mdao on 25/02/2016.
 */
public class SseSender implements Runnable {

    final Logger logger = LoggerFactory.getLogger(SseSender.class);

    private SseEmitter sseEmitter;
    private Comment comment;
    private CommentsService commentsService;
    private String id;

    public SseSender(SseEmitter sseEmitter, Comment comment, CommentsService commentsService, String id) {
        this.sseEmitter = sseEmitter;
        this.comment = comment;
        this.commentsService = commentsService;
        this.id = id;
    }
    @Override
    public void run() {
        try {
            sseEmitter.send(sseEmitter.event().name("commentPostedEvent").data(comment));
        } catch (Exception e) {
            logger.info("SseEmitter is probably closed");
            commentsService.remove(sseEmitter, id);
        }
    }

}
