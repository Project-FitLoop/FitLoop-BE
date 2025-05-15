package fitloop.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/kafka")
public class KafkaTestController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/buy")
    public ResponseEntity<String> buyProduct(@RequestParam String username) {
        String message = username + "님이 상품을 구매했습니다";
        kafkaTemplate.send("test", message);
        return ResponseEntity.ok("전송 완료: " + message);
    }
}
