package org.flinkproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.flink.util.Preconditions.checkArgument;
import static org.apache.flink.util.Preconditions.checkNotNull;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

/**
 * Created by marta on 03.09.17.
 *
 */
public class APISource implements SourceFunction<String> {
    private final String url;
    private final long maxNumRetries;
    private final long delayBetweenRetries;

    private static final int DEFAULT_CONNECTION_RETRY_SLEEP = 500;
    private volatile boolean isRunning = true;

    private static final Logger LOG = LoggerFactory.getLogger(APISource.class);
    private transient HttpURLConnection currentConnection;

    public APISource(String url,long maxNumRetries) {
        this(url, maxNumRetries, DEFAULT_CONNECTION_RETRY_SLEEP);
    }

    public APISource(String url, long maxNumRetries, long delayBetweenRetries) {
        checkArgument(maxNumRetries >= -1, "maxNumRetries must be zero or larger (num retries), or -1 (infinite retries)");
        checkArgument(delayBetweenRetries >=0, "delayBetweenRetries canot be smaller than 0");

        this.url = checkNotNull(url, "Url cannot be null.");
        this.delayBetweenRetries = delayBetweenRetries;
        this.maxNumRetries = maxNumRetries;
    }

    @Override
    public void run(SourceContext<String> sourceContext) throws Exception {

        final StringBuilder buffer = new StringBuilder();
        long attempt = 0;
        URL url = new URL(this.getUrl());
        HttpURLConnection conn = null;

        while(isRunning) {
            LOG.info("Connecting to API.");
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(1000 * 5);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                currentConnection = conn;
            } catch (IOException e) {
                if (isRunning) {
                    attempt++;
                    if (this.maxNumRetries == -1 || attempt < maxNumRetries) {
                        LOG.warn("Lost connection to server, retrying in " + this.delayBetweenRetries + " miliseconds");
                        Thread.sleep(this.delayBetweenRetries);
                    }
                } else {
                    LOG.error("Error connecting to the server");
                    break;
                }
            }

            assert conn != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            char[] cbuf = new char[8192];
            int bytesRead;
            // read responses
            while ((bytesRead = reader.read(cbuf)) != -1) {
                buffer.append(cbuf, 0, bytesRead);
                sourceContext.collect(buffer.toString());
                buffer.delete(0, buffer.length());
            }
        }
    }

    @Override
    public void cancel() {
        isRunning = false;

        HttpURLConnection connection = this.currentConnection;
        if (connection != null) {
            connection.disconnect();
        }


    }

    public String getUrl() {
        return url;
    }
}
