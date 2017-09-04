package org.flinkproject;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.log4j.BasicConfigurator;

/**
 * Created by marta on 26.08.17.
 *
 */
public class MainClass {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        // todo parametrize
        String url = "http://localhost:5000/hello";

        // Logger init
        BasicConfigurator.configure();

        // Get the raw data
        DataStream<String> stream = env.addSource(new APISource(url, 100));
        // Parse input JSON and sum M/F
        DataStream<Tuple2<String, Integer>> ds = StreamTransformer.countClientSex(stream);

        // Total price calculating
        DataStream<Tuple2<String, Integer>> price = StreamTransformer.countTotalPrice(stream);

        price.print();
        ds.print();
        env.execute();
    }
}