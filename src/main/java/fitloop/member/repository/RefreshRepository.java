package fitloop.member.repository;

import fitloop.member.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {

    Boolean existsByRefresh(String refresh);

    Optional<RefreshEntity> findByRefresh(String refresh);

    @Transactional
    void deleteByRefresh(String refresh);
}