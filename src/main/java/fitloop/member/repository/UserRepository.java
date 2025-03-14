package fitloop.member.repository;

import fitloop.member.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Boolean existsByUsername(String username);
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<UserEntity> findById(Long id);
}