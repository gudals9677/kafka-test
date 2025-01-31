package kafka.kafka_test.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaService {

    @Value("${test.project.topic}")
    private String topic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(MessageDto messages) {
        log.info("[Service] publishing messages:: {}", messages);
        kafkaTemplate.send(topic, messages);
    }

    @KafkaListener(topics = "test-topic", groupId = "test-group" )
    public void listener(MessageDto messages) {
        log.info("[Service] listener message:: {}", messages);
    }
}
