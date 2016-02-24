package com.minhdd.app.partials.comment;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mdao on 24/02/2016.
 */
@RestController
public class CommentController {

    List<SseEmitter> sseEmitters = new ArrayList<>();

    @RequestMapping("/comments")
    SseEmitter subscribeComments() {
        SseEmitter sseEmitter = new SseEmitter();
        this.sseEmitters.add(sseEmitter);
        return sseEmitter;
    }

    @RequestMapping(path = "/comment/{comment}", method = RequestMethod.GET)
    String create(@PathVariable String comment) throws IOException {
        for (SseEmitter sseEmitter : this.sseEmitters) {
            sseEmitter.send(comment, MediaType.APPLICATION_JSON);
        }
        return comment;
    }
}