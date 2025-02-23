package fitloop.member.service;

import fitloop.member.dto.request.RegisterRequest;
import fitloop.member.entity.UserEntity;
import fitloop.member.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public RegisterService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    public UserEntity registerProcess(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        String email = registerRequest.getEmail();
        String name = registerRequest.getName();

        // 사용자 이름 또는 이메일이 이미 존재하는지 확인
        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        // 새 사용자 생성
        UserEntity user = UserEntity.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .email(email)
                .birthday(registerRequest.getBirthday())
                .name(name)
                .build();

        return userRepository.save(user);
    }
}