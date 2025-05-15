package fitloop.cart.repository;

import fitloop.cart.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    List<CartEntity> findByUserId(Long userId);
    Optional<CartEntity> findByUserIdAndProductId(Long userId, Long productId);
    @Transactional
    @Modifying
    void deleteByUserIdAndProductId(Long userId, Long productId);
}