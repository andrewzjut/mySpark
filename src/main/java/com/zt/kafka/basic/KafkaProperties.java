package com.zt.kafka.basic;

public class KafkaProperties {
    public static final String TOPIC = "testMongodb.yes_db.no_users";
    public static final String KAFKA_SERVER_URL = "10.200.131.154:9092,10.200.131.155:9092,10.200.131.156:9092";
    public static final String KAFKA_SERVER_URL_PRD = "10.210.6.54:9092,10.210.6.55:9092,10.210.6.56:9092";
    public static final String schema_url = "http://10.200.131.154:8081";
    public static final int KAFKA_SERVER_PORT = 9092;
    public static final String BOOTSTRAPS = "10.200.20.98:9092";
    public static final int KAFKA_PRODUCER_BUFFER_SIZE = 64 * 1024;
    public static final int CONNECTION_TIMEOUT = 100000;
    public static final String TOPIC2 = "topic2";
    public static final String TOPIC3 = "topic3";
    public static final String CLIENT_ID = "SimpleConsumerDemoClient";

    private KafkaProperties() {
    }
}
