package com.zt.kafka.basic.string;

import com.zt.kafka.basic.KafkaProperties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.regex.Pattern;

public class Consumer extends Thread {
    private final KafkaConsumer<String, String> consumer;
    private final String topic;
    private final String group;

    public Consumer(String group, String topic) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProperties.BOOTSTRAPS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumer = new KafkaConsumer<>(props);

        this.topic = topic;
        this.group = group;
    }

    @Override
    public void run() {

//        consumer.subscribe(Collections.singletonList(this.topic));
//
//        consumer.subscribe(Arrays.asList(topic));



//        String patternStrng = "aaa.*";
//        Pattern pattern = Pattern.compile(patternStrng);
        consumer.subscribe(Arrays.asList(topic));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            for (ConsumerRecord<String, String> record : records) {
                long consumeTime = System.currentTimeMillis();
                String produceTime = record.value();
                System.out.println("消费 topic:" + record.topic() + "，耗时:" + (consumeTime - Long.parseLong(produceTime)));
            }
        }
    }

    public static void main(String[] args) {
        Consumer consumer = new Consumer("xxxxx","abcd4");
        consumer.start();
    }
}
