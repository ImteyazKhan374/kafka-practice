package com.abkatk.kafka.connectors;

import org.apache.kafka.common.config.ConfigDef;

public class MySourceConnectorConfig {
    public static final String INPUT_CONFIG = "some.input";

    public static ConfigDef config() {
        return new ConfigDef()
            .define(INPUT_CONFIG, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Some input");
    }
}
