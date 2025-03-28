package fitloop.product.repository;

import fitloop.product.entity.ProductTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTagRepository extends JpaRepository<ProductTagEntity, Long> {
}
