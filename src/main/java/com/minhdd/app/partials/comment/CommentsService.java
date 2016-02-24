package com.minhdd.app.partials.comment;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Created by mdao on 24/02/2016.
 */
@Component
public class CommentsService {
    private Map<String, List<SseEmitter>> emittersMap = newHashMap();

    public void addSseEmitter(SseEmitter sseEmitter, String id) {
        if (emittersMap.containsKey(id)) {
            emittersMap.get(id).add(sseEmitter);
        } else {
            List<SseEmitter> sseEmittersList = newArrayList();
            sseEmittersList.add(sseEmitter);
            emittersMap.put(id, sseEmittersList);
        }
    }

    public void postComment(Comment comment, String id) {
        if (emittersMap.containsKey(id)) {
            for (SseEmitter sseEmitter : emittersMap.get(id)) {
                try {
                    sseEmitter.send(comment, MediaType.APPLICATION_JSON);
                } catch (IOException e) {
                    sseEmitter.complete();
                }
            }
        }
    }
}
