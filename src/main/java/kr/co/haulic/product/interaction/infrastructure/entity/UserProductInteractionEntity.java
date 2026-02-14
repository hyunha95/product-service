package kr.co.haulic.product.interaction.infrastructure.entity;

import jakarta.persistence.*;
import kr.co.haulic.product.interaction.domain.InteractionType;
import lombok.*;

import java.time.LocalDateTime;

/**
 * JPA Entity for UserProductInteraction persistence
 * Maps to the 'user_product_interactions' table in the database
 */
@Entity
@Table(name = "user_product_interactions",
       indexes = {
           @Index(name = "idx_user_id", columnList = "userId"),
           @Index(name = "idx_product_id", columnList = "productId"),
           @Index(name = "idx_user_product", columnList = "userId,productId")
       })
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserProductInteractionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String productId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InteractionType interactionType;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (weight == null && interactionType != null) {
            weight = interactionType.getWeight();
        }
    }
}
