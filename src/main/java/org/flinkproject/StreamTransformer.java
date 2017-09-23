package org.flinkproject;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.util.Collector;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;

public class StreamTransformer {

    /***
     * @param stream DataStream that represents incoming transactions.
     * @return Total sum calculated for all products of a single transaction.
     */
    public static DataStream<Tuple2<String, Integer>> countTotalPrice(DataStream<String> stream) {
        return stream.flatMap(new PriceCalculator()).keyBy(0).reduce(new ReduceFunction<Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> reduce(Tuple2<String, Integer> v1, Tuple2<String, Integer> v2) throws Exception {
                return new Tuple2<>("Sum", v1.f1 + v2.f1);
            }
        });
    }
    public static class PriceCalculator implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String data, Collector<Tuple2<String, Integer>> out) throws Exception {
            JSONObject obj = new JSONObject(data);
            JSONArray products = obj.getJSONArray("products");
            for (int i = 0; i < products.length(); i++) {
                JSONObject item = products.getJSONObject(i);
                int price = item.getInt("amount") * item.getInt("pricePerUnit");
                out.collect(new Tuple2<>(item.getString("name"), price));
            }
        }
    }

    /***
     *
     * @param stream DataStream that represents incoming transaction.
     * @return Tuple indicating the sex of the buyer.
     */
//  Client sex counting
    public static DataStream<Tuple2<String, Integer>> countClientSex(DataStream<String> stream) {
        return stream.flatMap(new ClientSexCounter()).keyBy(0).sum(1);
    }
    public static class ClientSexCounter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String data, Collector<Tuple2<String, Integer>> out) throws Exception {
            JSONObject obj = new JSONObject(data);
            JSONObject client = obj.getJSONObject("client");
            out.collect(new Tuple2<>(client.getString("sex"), 1));
        }
    }
}
