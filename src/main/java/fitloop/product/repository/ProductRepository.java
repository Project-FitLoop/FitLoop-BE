package fitloop.product.repository;

import fitloop.product.entity.ProductEntity;

import fitloop.product.entity.category.BottomCategory;
import fitloop.product.entity.category.MiddleCategory;
import fitloop.product.entity.category.TopCategory;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Page<ProductEntity> findAllByIsActiveTrue(Pageable pageable);

    @Query("""
    SELECT p FROM ProductEntity p
    JOIN ProductCategoryRelationEntity rel ON p.id = rel.productEntity.id
    JOIN CategoryEntity c ON rel.categoryEntity.id = c.id
    WHERE (:top = fitloop.product.entity.category.TopCategory.ALL AND c.topCategory IN (fitloop.product.entity.category.TopCategory.MALE, fitloop.product.entity.category.TopCategory.FEMALE)
           OR c.topCategory = :top)
      AND c.middleCategory = :middle
      AND p.isActive = true
    """)
    Page<ProductEntity> findByTopAndMiddle(
            @Param("top") TopCategory top,
            @Param("middle") MiddleCategory middle,
            Pageable pageable
    );

    @Query("""
    SELECT p FROM ProductEntity p
    JOIN ProductCategoryRelationEntity rel ON p.id = rel.productEntity.id
    JOIN CategoryEntity c ON rel.categoryEntity.id = c.id
    WHERE (:top = fitloop.product.entity.category.TopCategory.ALL AND c.topCategory IN (fitloop.product.entity.category.TopCategory.MALE, fitloop.product.entity.category.TopCategory.FEMALE)
           OR c.topCategory = :top)
      AND c.middleCategory = :middle
      AND c.bottomCategory = :bottom
      AND p.isActive = true
    """)
    Page<ProductEntity> findByTopMiddleBottom(
            @Param("top") TopCategory top,
            @Param("middle") MiddleCategory middle,
            @Param("bottom") BottomCategory bottom,
            Pageable pageable
    );
}
