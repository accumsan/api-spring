package com.minhdd.app.ml;

import com.minhdd.app.config.Constants;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by mdao on 04/03/2016.
 */
@Configuration
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
public class SparkConfig {

    @Bean
    public SQLContext sqlContext() {
        JavaSparkContext javaSparkContext = new JavaSparkContext("local", "basicmap", System.getenv("SPARK_HOME"), System.getenv("JARS"));
        return new SQLContext(javaSparkContext);
    }

}
