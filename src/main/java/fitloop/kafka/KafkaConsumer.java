package fitloop.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @KafkaListener(topics = "test", groupId = "test-group")
    public void consume(String message) {
        System.out.println("구매가 완료되었습니다: " + message);
    }

    @KafkaListener(topics = "test", groupId = "admin-group")
    public void consumeAsAdmin(String message) {
        System.out.println("관리자 확인: " + message);
    }
}
