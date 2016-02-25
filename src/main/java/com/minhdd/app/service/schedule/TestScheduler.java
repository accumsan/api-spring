package com.minhdd.app.service.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by minhdao on 22/02/16.
 */
@Component
public class TestScheduler {
    //@Scheduled(fixedDelayString = "${test.delay}")
    public void demoServiceMethod(){
        System.out.println("this method is called at every ${test.delay}/1000 seconds");
    }
    //@Scheduled(cron = "0 56 22 * * ?")
    public void cron1(){
        System.out.println("this method is called at 22:56 everyday");
    }
    //@Scheduled(cron = "${test.cron.expression}")
    public void cron()
    {
        System.out.println("Method scheduled by cron set by property test.cron.expression");
    }
}
