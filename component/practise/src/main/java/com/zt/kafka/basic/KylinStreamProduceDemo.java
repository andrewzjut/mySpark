package com.zt.kafka.basic;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.Metadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.*;
import java.util.concurrent.Future;

public class KylinStreamProduceDemo {
    public static void main(String[] args) throws Exception{

        List<String> countries = new ArrayList();
        countries.add("AUSTRALIA");
        countries.add("CANADA");
        countries.add("CHINA");
        countries.add("INDIA");
        countries.add("JAPAN");
        countries.add("KOREA");
        countries.add("US");
        countries.add("Other");
        List<String> category = new ArrayList();
        category.add("BOOK");
        category.add("TOY");
        category.add("CLOTH");
        category.add("ELECTRONIC");
        category.add("Other");
        List<String> devices = new ArrayList();
        devices.add("iOS");
        devices.add("Windows");
        devices.add("Andriod");
        devices.add("Other");

        List<String> genders = new ArrayList();
        genders.add("Male");
        genders.add("Female");

        Properties props = new Properties();
        props.put("bootstrap.servers", "10.200.22.114:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer producer = new KafkaProducer<String, String>(props);

        boolean alive = true;
        Random rnd = new Random();
        Map<String, Object> record = new HashMap();
        while (alive == true) {
            //add normal record
            record.put("order_time", (new Date().getTime()));
            record.put("country", countries.get(rnd.nextInt(countries.size())));
            record.put("category", category.get(rnd.nextInt(category.size())));
            record.put("device", devices.get(rnd.nextInt(devices.size())));
            record.put("qty", rnd.nextInt(10));
            record.put("currency", "USD");
            record.put("amount", rnd.nextDouble() * 100);
            //add embedded record
            Map<String, Object> user = new HashMap();
            user.put("id", UUID.randomUUID().toString());
            user.put("gender", genders.get(rnd.nextInt(2)));
            user.put("age", rnd.nextInt(20) + 10);
            record.put("user", user);
            //send message
            ProducerRecord data = new ProducerRecord<String, String>("kylin_streaming_topic", System.currentTimeMillis() + "", JSONObject.toJSONString(record));
            Future<RecordMetadata> future = producer.send(data);

            System.out.println(future.get().offset());
            Thread.sleep(100l);
        }
        producer.close();
    }
}
