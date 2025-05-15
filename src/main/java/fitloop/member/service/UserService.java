package fitloop.member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fitloop.member.dto.request.CustomUserDetails;
import fitloop.member.dto.request.ProfileRequest;
import fitloop.member.entity.ProfileEntity;
import fitloop.member.entity.RefreshEntity;
import fitloop.member.entity.UserEntity;
import fitloop.member.jwt.JWTUtil;
import fitloop.member.repository.ProfileRepository;
import fitloop.member.repository.RefreshRepository;
import fitloop.member.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RefreshRepository refreshRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseEntity<?> getUserInfo(Object principal) {
        if (!(principal instanceof UserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
        }

        return ResponseEntity.ok(Map.of(
                "username", userDetails.getUsername(),
                "roles", userDetails.getAuthorities()
        ));
    }

    @Transactional
    public ResponseEntity<?> createProfile(ProfileRequest profileRequest, Object principal, String accessToken) {
        if (!(principal instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = jwtUtil.getUsername(accessToken);

        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "사용자를 찾을 수 없습니다."));
        }

        UserEntity user = optionalUser.get();
        user.setPersonalInfo(true);
        userRepository.save(user);

        ProfileEntity profile = ProfileEntity.builder()
                .userId(user)
                .nickname(profileRequest.getNickname())
                .gender(profileRequest.getGender())
                .ageRange(profileRequest.getAgeRange())
                .height(profileRequest.getHeight())
                .weight(profileRequest.getWeight())
                .build();

        profileRepository.save(profile);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "프로필 작성 성공"));
    }

    public ResponseEntity<?> reissueTokens(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("리프레시 토큰이 존재하지 않습니다");
        }

        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.badRequest().body("리프레시 토큰이 만료되었습니다.");
        }

        if (!"refresh".equals(jwtUtil.getCategory(refreshToken))) {
            return ResponseEntity.badRequest().body("유효하지 않은 리프레시 토큰입니다.");
        }

        Optional<RefreshEntity> optionalEntity = refreshRepository.findByRefresh(refreshToken);
        if (optionalEntity.isEmpty()) {
            return ResponseEntity.badRequest().body("유효하지 않은 리프레시 토큰입니다.");
        }

        RefreshEntity refreshEntity = optionalEntity.get();
        LocalDateTime firstIssuedAt = refreshEntity.getFirstIssuedAt();

        if (firstIssuedAt != null) {
            Duration duration = Duration.between(firstIssuedAt, LocalDateTime.now());
            if (duration.toMillis() > 1_209_600_000L) { // 14일
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("리프레시 토큰 발급 유효기간(14일)을 초과했습니다.");
            }
        }

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccess = createAccessToken(username, role);
        String newRefresh = createRefreshToken(username, role);

        saveAccessTokenToRedis(username, role, newAccess);
        renewRefreshToken(refreshToken, newRefresh, username, firstIssuedAt);

        response.addCookie(createAccessCookie(newAccess));
        response.addCookie(createRefreshCookie(newRefresh));

        return ResponseEntity.ok().build();
    }


    public String createAccessToken(String username, String role) {
        return jwtUtil.createJwt("access", username, role, 600_000L); // 10분
    }

    public String createRefreshToken(String username, String role) {
        return jwtUtil.createJwt("refresh", username, role, 86_400_000L); // 24시간
    }

    public void saveAccessTokenToRedis(String username, String role, String token) {
        try {
            String redisKey = "auth:access:" + username;
            String redisValue = objectMapper.writeValueAsString(Map.of(
                    "role", role,
                    "token", token
            ));
            redisTemplate.opsForValue().set(redisKey, redisValue, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Redis 저장 실패", e);
        }
    }

    public void saveNewRefreshToken(String username, String refreshToken) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plusDays(1); // 24시간

        RefreshEntity entity = RefreshEntity.builder()
                .username(username)
                .refresh(refreshToken)
                .expiration(expiration.toString())
                .firstIssuedAt(now)
                .lastReissuedAt(now)
                .build();

        refreshRepository.save(entity);
    }

    public void renewRefreshToken(String oldToken, String newToken, String username, LocalDateTime firstIssuedAt) {
        refreshRepository.deleteByRefresh(oldToken);

        refreshRepository.save(RefreshEntity.createRenewed(
                username,
                newToken,
                new Date(System.currentTimeMillis() + 86_400_000L).toString(),
                firstIssuedAt != null ? firstIssuedAt : LocalDateTime.now()
        ));
    }

    public Cookie createAccessCookie(String token) {
        Cookie cookie = new Cookie("access", token);
        cookie.setMaxAge(10 * 60);
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        cookie.setPath("/");
        return cookie;
    }

    public Cookie createRefreshCookie(String token) {
        Cookie cookie = new Cookie("refresh", token);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        return cookie;
    }
}
