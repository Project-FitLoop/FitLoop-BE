package fitloop.member.controller;

import fitloop.member.dto.request.RegisterRequest;
import fitloop.member.service.RegisterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class RegisterController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping("/register")
    public String registerProcess(RegisterRequest registerRequest) {

        System.out.println(registerRequest.getUsername());
        registerService.registerProcess(registerRequest);

        return "ok";
    }
}
