package com.minhdd.app.partials.comment;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.inject.Inject;
import javax.validation.Valid;
import java.io.IOException;

/**
 * Created by mdao on 24/02/2016.
 */
@RestController
public class CommentController {
    @Inject
    CommentsService commentsService;

    @CrossOrigin(origins = "http://localhost:8081")
    @RequestMapping("/comments/{id}")
    SseEmitter subscribeComments(@PathVariable String id) {
        SseEmitter sseEmitter = new SseEmitter();
        commentsService.addSseEmitter(sseEmitter, id);
        return sseEmitter;
    }

    @CrossOrigin(origins = "http://localhost:8081")
    @RequestMapping(path = "/comment/{id}", method = RequestMethod.POST)
    ResponseEntity post(@PathVariable String id, @RequestBody @Valid Comment comment) throws IOException {
        commentsService.postComment(comment, id);
        return new ResponseEntity(HttpStatus.OK);
    }
}