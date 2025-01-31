package kafka.kafka_test.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class KafkaController {

    private final KafkaService kafkaService;

    @PostMapping("/send")
    public void test(@RequestBody MessageDto messages) {
        log.info("[Controller] publishing messages:: {}", messages);
        kafkaService.sendMessage(messages);
    }
}
