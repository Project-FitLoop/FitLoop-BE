package fitloop.member.jwt;

import fitloop.member.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public CustomLogoutFilter(JWTUtil jwtUtil, RefreshRepository refreshRepository, RedisTemplate<String, String> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // 로그아웃 API가 아니면 그냥 넘김
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/api/v1/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Refresh 토큰 가져오기
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                }
            }
        }

        // Refresh 토큰 null 체크
        if (refresh == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Refresh 토큰 만료 체크
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Refresh 토큰이 맞는지 카테고리 체크
        String category = jwtUtil.getCategory(refresh);
        if (!"refresh".equals(category)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // DB에 Refresh 토큰 존재 여부 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // ===== 실제 로그아웃 처리 시작 =====

        // Refresh 토큰 DB에서 삭제
        refreshRepository.deleteByRefresh(refresh);

        // Access 토큰 Redis에서 삭제
        String accessToken = request.getHeader("access");
        if (accessToken != null) {
            redisTemplate.delete("ACCESS:" + accessToken);
        }

        // Refresh 토큰 쿠키 제거
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
