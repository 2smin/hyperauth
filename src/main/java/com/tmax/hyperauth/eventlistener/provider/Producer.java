package com.tmax.hyperauth.eventlistener.provider;

import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.keycloak.events.Event;

import java.util.Properties;

public class Producer {

    private final static String BOOTSTRAP_SERVER = "kafkas.hyperauth:9092";

    public static void publishEvent(String topic, Object value){
        //reset thread context
        resetThreadContext();
        // create the producer

        KafkaProducer<String, String> producer = new KafkaProducer<>(getProperties());

        // create a producer record
        Gson gson = new Gson();
        String jsonValue = gson.toJson(value);
        ProducerRecord<String, String> eventRecord =
                new ProducerRecord<String, String>(topic, jsonValue);

        // send data - asynchronous
        producer.send(eventRecord);

        // flush data
        producer.flush();
        // flush and close producer
        producer.close();
    }

    private static void resetThreadContext() {
        Thread.currentThread().setContextClassLoader(null);
    }

    public static Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "2000");
        return properties;
    }
}
