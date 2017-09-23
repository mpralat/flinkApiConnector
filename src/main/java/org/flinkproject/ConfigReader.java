package org.flinkproject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    InputStream inputStream;

    public Properties getPropValues() throws IOException {
        try {
            Properties prop = new Properties();
            String propertiesFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("Could not find the properties file!");
            }
            return prop;
        } finally {
            assert inputStream != null;
            inputStream.close();
        }
    }
}
