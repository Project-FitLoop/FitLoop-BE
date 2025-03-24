package fitloop.product.repository;

import fitloop.product.entity.ProductConditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductConditionRepository extends JpaRepository<ProductConditionEntity, Long> {
}
