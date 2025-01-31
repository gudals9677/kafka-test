package kafka.kafka_test.test;

import ch.qos.logback.classic.pattern.MessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${test.project.topic}")
    private String topic;

    @Autowired
    private ObjectMapper objectMapper;

    // ConsumerFactory 설정 (JsonDeserializer 사용)
    public ConsumerFactory<String, MessageDto> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // 신뢰할 수 있는 패키지 설정
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, MessageDto.class); // JSON을 MessageDto 객체로 변환

        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    // MessageListener 설정
    @Bean
    public MessageListenerContainer messageListenerContainer() {
        ContainerProperties containerProps = new ContainerProperties(topic);

        // MessageListener 설정
        MessageListener<String, MessageDto> messageListener = new MessageListener<String, MessageDto>() {
            @Override
            public void onMessage(ConsumerRecord<String, MessageDto> record) {
                // 메시지를 수신했을 때 처리할 로직
                MessageDto message = record.value();
                System.out.println("[Config] received message: " + message.toString());
            }
        };

        containerProps.setMessageListener(messageListener);

        // 메시지를 소비하는 ConcurrentMessageListenerContainer 반환
        return new ConcurrentMessageListenerContainer<>(consumerFactory(), containerProps);
    }
}
