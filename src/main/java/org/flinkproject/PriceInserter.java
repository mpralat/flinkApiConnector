package org.flinkproject;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Sends the total price data to ElasticSearch.
 */
public class PriceInserter implements ElasticsearchSinkFunction<Tuple2<String, Integer>> {
    @Override
    public void process(
            Tuple2<String, Integer> record,
            RuntimeContext runtimeContext,
            RequestIndexer requestIndexer) {

        Map<String, String> json = new HashMap<>();
        json.put("price", record.f1.toString());
        json.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        IndexRequest request = Requests.indexRequest()
                .index("total-price")
                .type("total-price")
                .source(json);
        requestIndexer.add(request);
    }
}

//
//    PUT total-price/_mapping/total-price
//        {
//        "properties": {
//        "price": {
//        "type": "long"
//        },
//        "date": {
//        "type": "date",
//        "format": "yyyy-MM-dd HH:mm:ss"
//        }
//        }
//        }