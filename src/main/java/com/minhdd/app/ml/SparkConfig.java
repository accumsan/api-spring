package com.minhdd.app.ml;

import com.minhdd.app.config.Constants;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import scala.collection.JavaConversions;
import scala.collection.Seq;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * Created by mdao on 04/03/2016.
 */
@Configuration
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
public class SparkConfig {

    SparkContext sparkContext;

    @PostConstruct
    public void init(){
        Seq<String> jars = JavaConversions.asScalaBuffer(
                Arrays.asList(System.getenv("JARS"))
        ).seq();
        SparkContext sc = new SparkContext("local", "app", System.getenv("SPARK_HOME"), jars);
//        JavaSparkContext jsc = new JavaSparkContext("local", "app", System.getenv("SPARK_HOME"), System.getenv("JARS"));
        sparkContext = sc;
    }

    @Bean
    public SparkContext sparkContext() {
        return sparkContext;
    }

    @Bean
    public SQLContext sqlContext() {
        return new SQLContext(sparkContext);
    }



}
