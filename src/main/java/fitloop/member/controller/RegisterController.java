package fitloop.member.controller;

import fitloop.member.dto.request.RegisterRequest;
import fitloop.member.service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.validation.Valid;

import java.util.Map;

@Controller
@ResponseBody
@RequestMapping("/api/v1")
public class RegisterController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerProcess(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            registerService.registerProcess(registerRequest);
            return ResponseEntity.ok("회원가입 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "회원가입 실패: " + e.getMessage()));
        }
    }
}
