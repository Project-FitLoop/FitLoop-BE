package fitloop.member.dto.request;

import fitloop.member.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

public class CustomUserDetails implements UserDetails, OAuth2User {

    private final UserEntity userEntity;
    private final Map<String, Object> attributes;

    // 일반 로그인용 생성자 (OAuth2 없이)
    public CustomUserDetails(UserEntity userEntity) {
        this.userEntity = userEntity;
        this.attributes = new HashMap<>();
    }

    // OAuth2 로그인용 생성자
    public CustomUserDetails(UserEntity userEntity, Map<String, Object> attributes) {
        this.userEntity = userEntity;
        this.attributes = attributes != null ? attributes : new HashMap<>();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(userEntity.getRole().name()));
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        // OAuth2 로그인 시 loginId 사용
        return userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public String getName() {
        return userEntity.getUsername();
    }
}
