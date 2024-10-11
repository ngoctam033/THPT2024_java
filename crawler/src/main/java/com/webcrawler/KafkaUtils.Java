import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

public class KafkaUtils {

    private static KafkaProducer<String, String> producer;

    static {
        try {
            Properties props = new Properties();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            producer = new KafkaProducer<>(props);
        } catch (Exception e) {
            System.err.println("Failed to initialize Kafka producer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Future<RecordMetadata> sendJsonToKafka(String topic, String json) {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, json);
            return producer.send(record);
        } catch (Exception e) {
            System.err.println("Failed to send record to Kafka: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}