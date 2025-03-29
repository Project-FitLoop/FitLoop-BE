package fitloop.product.repository;

import fitloop.product.entity.ProductCategoryRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRelationRepository extends JpaRepository<ProductCategoryRelationEntity, Long> {
}
