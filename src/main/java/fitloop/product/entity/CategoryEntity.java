package fitloop.product.entity;

import fitloop.product.entity.category.TopCategory;
import fitloop.product.entity.category.MiddleCategory;
import fitloop.product.entity.category.BottomCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TopCategory topCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MiddleCategory middleCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BottomCategory bottomCategory;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
