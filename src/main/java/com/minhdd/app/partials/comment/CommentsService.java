package com.minhdd.app.partials.comment;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Created by mdao on 24/02/2016.
 */
@Component
public class CommentsService {
    private Map<String, List<SseEmitter>> emittersMap = newHashMap();
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);;

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
            List<SseEmitter> emitters = emittersMap.get(id);
            for (SseEmitter sseEmitter : emitters) {
                SseSender sseSender = new SseSender(sseEmitter, comment, this, id);
                executorService.schedule(sseSender, 1, TimeUnit.MILLISECONDS);
            }
        }
    }

    public void remove(SseEmitter sseEmitter, String id) {
        if (emittersMap.containsKey(id)) {
            List<SseEmitter> emitters = emittersMap.get(id);
            emitters.remove(sseEmitter);
        }
    }
}
