package com.tcs.kafka.connectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

@SuppressWarnings("unused")
public class MySourceTask extends SourceTask {
	private String someInput;

	@Override
	public void start(Map<String, String> props) {
		this.someInput = props.get("some.input");
	}

	@Override
	public List<SourceRecord> poll() throws InterruptedException {
		List<SourceRecord> records = new ArrayList<>();

		// Simulate a data fetch (e.g., from DB, API, file)
		Map<String, String> sourcePartition = Collections.singletonMap("partition", "default");
		Map<String, Long> sourceOffset = Collections.singletonMap("offset", System.currentTimeMillis());

		String value = "Hello from custom connector!";
		records.add(new SourceRecord(sourcePartition, sourceOffset, "my-custom-topic", Schema.STRING_SCHEMA, value));
		Thread.sleep(1000);
		return records;
	}

	@Override
	public void stop() {
	}

	@Override
	public String version() {
		return "1.0";
	}
}
