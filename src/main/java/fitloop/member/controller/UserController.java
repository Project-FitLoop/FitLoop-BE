package fitloop.member.controller;

import fitloop.member.dto.request.CustomUserDetails;
import fitloop.member.dto.request.ProfileRequest;
import fitloop.member.entity.ProfileEntity;
import fitloop.member.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@ResponseBody
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        return ResponseEntity.ok(Map.of(
                "username", userDetails.getUsername(),
                "roles", userDetails.getAuthorities()
        ));
    }

    private final ProfileService profileService;

    @PostMapping("/users/profile")
    public ResponseEntity<ProfileEntity> createProfile(
            @RequestBody @Valid ProfileRequest profileRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        System.out.println("userDetails : " + userDetails);
        System.out.println("나와랍 얍");
        Long userId = userDetails.getId();
        System.out.println(userId);

        ProfileEntity profile = profileService.createProfile(
                userId,
                profileRequest.getNickname(),
                profileRequest.getGender(),
                profileRequest.getAgeRange(),
                profileRequest.getHeight(),
                profileRequest.getWeight()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

}
