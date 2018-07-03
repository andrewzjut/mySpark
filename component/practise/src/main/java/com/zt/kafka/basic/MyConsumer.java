package com.zt.kafka.basic;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class MyConsumer extends Thread {
    private final KafkaConsumer<String, String> consumer;
    private final String topic;
    private final String group;

    public MyConsumer(String group, String topic) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProperties.KAFKA_SERVER_URL + ":" + KafkaProperties.KAFKA_SERVER_PORT);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,"io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,"io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put("schema.registry.url", "http://10.210.6.54:8091");
        consumer = new KafkaConsumer<>(props);

        this.topic = topic;
        this.group = group;
    }

    @Override
    public void run() {

        consumer.subscribe(Collections.singletonList(this.topic));

        consumer.subscribe(Arrays.asList(topic));

        ConsumerRecords<String, String> records = consumer.poll(1000);
        for (ConsumerRecord<String, String> record : records) {
            System.out.printf("offset = %d, key = %s, value = %s \n", record.offset(), record.key(), record.value());
        }
    }

    public static void main(String[] args) {
        MyConsumer myConsumer = new MyConsumer("xxdddddd1", "dmp.gateway.source-fdn-t_app_user");
        myConsumer.start();

    }


}
