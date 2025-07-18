package com.tcs.kafka.connectors;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.source.SourceConnector;

public class MySourceConnector extends SourceConnector {
    private Map<String, String> configProps;

    @Override
    public void start(Map<String, String> props) {
        this.configProps = props;
    }

    @Override
    public Class<MySourceTask> taskClass() {
        return MySourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        return Collections.singletonList(configProps);
    }

    @Override
    public void stop() {}

    @Override
    public ConfigDef config() {
        return MySourceConnectorConfig.config();
    }

    @Override
    public String version() {
        return "1.0";
    }
}
