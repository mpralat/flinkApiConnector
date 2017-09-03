package org.flinkproject;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.flink.util.Collector;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;

/**
 * Created by marta on 26.08.17.
 *
 */
public class main {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
        // todo parametrize
        String url = "http://localhost:5000/hello";

        // Get the raw data
        DataStream<String> stream = env.addSource(new APISource(url, 100));
        // Parse input JSON and sum M/F
        DataStream<Tuple2<String, Integer>> ds = stream.flatMap(new SelectJSONFlatMap()).keyBy(0).sum(1);

        DataStream<Tuple2<String, Integer>> price = stream.flatMap(new PriceCalculator()).keyBy(0).sum(1);

        price.print();
        ds.print();
        env.execute();

    }
    public static class SelectJSONFlatMap implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String data, Collector<Tuple2<String, Integer>> out) throws Exception {
            JSONObject obj = new JSONObject(data);
            JSONObject client = obj.getJSONObject("client");
            out.collect(new Tuple2(client.getString("sex"), 1));
        }
    }

    public static class PriceCalculator implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String data, Collector<Tuple2<String, Integer>> out) throws Exception {
            JSONObject obj = new JSONObject(data);
            JSONArray products = obj.getJSONArray("products");
            for (int i = 0; i < products.length(); i++) {
                JSONObject item = products.getJSONObject(i);
                Integer price = item.getInt("amount") * item.getInt("pricePerUnit");
                out.collect(new Tuple2(item.getString("name"), price));
            }
        }
    }
}