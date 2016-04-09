package com.minhdd.app.partials.sms;

import org.junit.Test;

/**
 * Created by mdao on 18/03/2016.
 */
public class SenderTry {
    @Test
    public void test() {
        Sender.sendUsingEsendex("message test", "minhdd", "0624427004");
    }
}
