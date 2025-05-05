package fitloop.member.repository;

import fitloop.member.entity.ProfileEntity;
import fitloop.member.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
    Optional<ProfileEntity> findByUserId(UserEntity userEntity);
}
