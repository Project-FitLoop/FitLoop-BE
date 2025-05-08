package fitloop.product.repository;

import fitloop.product.entity.ProductEntity;
import fitloop.product.entity.ProductTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductTagRepository extends JpaRepository<ProductTagEntity, Long> {
    List<ProductTagEntity> findByProductEntityIdIn(List<Long> productIds);
    List<ProductTagEntity> findAllByProductEntity(ProductEntity product);
}