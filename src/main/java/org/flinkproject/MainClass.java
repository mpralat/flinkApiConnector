package org.flinkproject;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.flink.streaming.connectors.elasticsearch5.ElasticsearchSink;
import org.apache.log4j.BasicConfigurator;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by marta on 26.08.17.
 *
 */
public class MainClass {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        // todo parameterize
        String url = "http://localhost:5000/hello";

        // Logger init
        BasicConfigurator.configure();

        // Get the raw data
        DataStream<String> stream = env.addSource(new APISource(url, 100));
        // Parse input JSON and sum M/F
        DataStream<Tuple2<String, Integer>> ds = StreamTransformer.countClientSex(stream);

        // Total price calculating
        DataStream<Tuple2<String, Integer>> price = StreamTransformer.countTotalPrice(stream);

        // ELASTIC SEARCH
        Map<String, String> config = new HashMap<>();
        // This instructs the sink to emit after every element, otherwise they would be buffered
        config.put("bulk.flush.max.actions", "10");
        config.put("cluster.name", "elasticsearch");

        List<InetSocketAddress> transports = new ArrayList<>();
        transports.add(new InetSocketAddress(InetAddress.getByName("localhost"), 9300));

        price.addSink(new ElasticsearchSink<>(
                config,
                transports,
                new PriceInserter()));


        price.print();
        ds.print();
        env.execute();
    }
}