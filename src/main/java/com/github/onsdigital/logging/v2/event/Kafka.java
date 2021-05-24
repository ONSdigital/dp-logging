package com.github.onsdigital.logging.v2.event;

import org.apache.avro.specific.SpecificRecordBase;

/**
 * POJO containing Kafka details required by log events.
 */
public class Kafka {

    private SpecificRecordBase msg;
    private String topic;

    /**
     * Construct a new Kafka instance and populate the SpecificRecordBase fields.
     */
    public Kafka(SpecificRecordBase msg) {
        this(msg, "");
    }

    /**
     * Construct a new Kafka instance and populate the SpecificRecordBase and topic fields.
     */
    public Kafka(SpecificRecordBase msg, String topic) {
        this.msg = msg;
        this.topic = topic;
    }


    public String getTopic() {
        return this.topic;
    }

    public String getMessage() {
        return this.msg.toString();
    }
}
