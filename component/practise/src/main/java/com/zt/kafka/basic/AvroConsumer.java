package com.zt.kafka.basic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.zt.spark.pvuv.InternalRedisClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.Properties;

public class AvroConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AvroConsumer.class);
    private static final BloomFilter<CharSequence> dealIdBloomFilter =
            BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 17159420);

    public static void main(String[] args) throws Exception {
//        produce();
        consume();
    }

    private static void produce() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProperties.KAFKA_SERVER_URL + ":" + KafkaProperties.KAFKA_SERVER_PORT);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                io.confluent.kafka.serializers.KafkaAvroSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                io.confluent.kafka.serializers.KafkaAvroSerializer.class);
        props.put("schema.registry.url", "http://" + KafkaProperties.KAFKA_SERVER_URL + ":8081");
        KafkaProducer producer = new KafkaProducer(props);

        String key = "key1";
        String userSchema = "{\"type\":\"record\"," +
                "\"name\":\"myrecord\"," +
                "\"fields\":[{\"name\":\"f1\",\"type\":\"string\"}]}";
        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(userSchema);
        GenericRecord avroRecord = new GenericData.Record(schema);
        avroRecord.put("f1", "value1");

        ProducerRecord<String, GenericRecord> record = new ProducerRecord<>("topic1", key, avroRecord);
        producer.send(record);
        producer.close();

    }

    private static void consume() throws Exception {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.200.159.17:9092,10.200.159.18:9092,10.200.159.19:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "ddfdddddddd");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put("schema.registry.url", "http://10.200.159.17:8081");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        String topic = "jingshu-0808-1.credit_cloud_db.jianguo_bankbill";
        final Consumer<Object, Object> consumer = new KafkaConsumer<>(props);
        TopicPartition topicPartition = new TopicPartition(topic, 0);
        consumer.assign(Arrays.asList(topicPartition));

        consumer.seek(topicPartition, 0l);

        try {
            while (true) {
                ConsumerRecords<Object, Object> records = consumer.poll(1000);
                logger.info("消费{}条", records.count());
                for (ConsumerRecord<Object, Object> record : records) {
                    JSONObject jsonObject = JSON.parseObject(record.value().toString());
                    String after = jsonObject.getString("after");
                    JSONObject afterJson = JSON.parseObject(after);
                    String id = JSON.parseObject(afterJson.getString("_id")).getString("$oid");
                    boolean exists = dealIdBloomFilter.mightContain(id);
                    if (exists) {
                        logger.error("重复id：{}", id);
                    } else {
                        dealIdBloomFilter.put(id);
                    }
                }
            }
        } finally {
            consumer.close();
        }
    }
}
