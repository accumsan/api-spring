package com.minhdd.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by mdao on 19/02/2016.
 */

public class AppProperties {
    public class PROP_KEYS {
        public final static String TASK_SSE_CRON = "task.sse.cron.expression";
        public final static String TEST = "test";
    }
    private static final Logger logger = LoggerFactory.getLogger(AppProperties.class);
    private static AppProperties instance;
    private static boolean reload = false;
    private static final Object MUTEX = new Object();
    private Properties properties;

    public static AppProperties getInstance() {
        if (instance == null || reload) {
            synchronized (MUTEX) {
                if (instance == null || reload) {
                    instance = new AppProperties();
                    try {
                        instance.load();
                        reload = false;
                    } catch (Exception e) {
                        logger.error("Properties loading error", e);
                    }
                }
            }
        }
        return instance;
    }


    private void load() throws IOException {
        properties = new Properties();
        InputStream input = null;
        String profile = System.getProperty("spring.profiles.active");
        if (profile == null) {
            profile = Constants.SPRING_PROFILE_DEVELOPMENT;
        }
        logger.debug("Properties file load for profile " + profile);
        try {
            ClassPathResource resource = new ClassPathResource("/config/"+profile+".properties");
            input = new FileInputStream(resource.getFile());
            properties.load(input);
        } catch (IOException e) {
            logger.error("Properties file loading error ", e);
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    public void reloadProperties() {
        reload = true;
        AppProperties.getInstance();
    }
}
