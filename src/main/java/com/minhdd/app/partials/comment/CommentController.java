package com.minhdd.app.partials.comment;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mdao on 24/02/2016.
 */
@RestController
public class CommentController {
    @Inject
    CommentsService commentsService;


    @RequestMapping("/comments/{id}")
    SseEmitter subscribeComments(@PathVariable String id) {
        SseEmitter sseEmitter = new SseEmitter();
        commentsService.addSseEmitter(sseEmitter, id);
        return sseEmitter;
    }

    @RequestMapping(path = "/comment/{id}", method = RequestMethod.POST)
    void post(@PathVariable String id, @RequestBody @Valid Comment comment) throws IOException {
        commentsService.postComment(comment, id);
    }
}