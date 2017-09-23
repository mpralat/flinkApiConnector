package org.flinkproject;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch5.ElasticsearchSink;
import org.apache.log4j.BasicConfigurator;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * Created by marta on 26.08.17.
 *
 */
public class MainClass {
    public static void main(String[] args) throws Exception {
        // Config
        Properties properties = new ConfigReader().getPropValues();
        // Logger init
        BasicConfigurator.configure();

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        String url = properties.getProperty("api_url");

        // Get the raw data
        DataStream<String> stream = env.addSource(new APISource(url, 100));
        // Parse input JSON and sum M/F
        DataStream<Tuple2<String, Integer>> ds = StreamTransformer.countClientSex(stream);
        // Total price calculating
        DataStream<Tuple2<String, Integer>> price = StreamTransformer.countTotalPrice(stream);
        // ELASTIC SEARCH
        Map<String, String> config = new HashMap<>();
        // This instructs the sink to emit after every element, otherwise they would be buffered
        config.put("bulk.flush.max.actions", properties.getProperty("bulk_flush_max_actions"));
        config.put("cluster.name", properties.getProperty("cluster_name"));

        List<InetSocketAddress> transports = new ArrayList<>();
        transports.add(new InetSocketAddress(
                InetAddress.getByName(properties.getProperty("elasticsearch_host")),
                Integer.parseInt(properties.getProperty("elasticsearch_port")))
        );

        price.addSink(new ElasticsearchSink<>(
                config,
                transports,
                new PriceInserter()));


        price.print();
        ds.print();
        env.execute();
    }
}