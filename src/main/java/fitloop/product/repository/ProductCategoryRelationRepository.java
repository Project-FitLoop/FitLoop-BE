package fitloop.product.repository;

import fitloop.product.entity.ProductCategoryRelationEntity;
import fitloop.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductCategoryRelationRepository extends JpaRepository<ProductCategoryRelationEntity, Long> {
    Optional<ProductCategoryRelationEntity> findByProductEntity(ProductEntity product);
}
