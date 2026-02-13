package kr.co.haulic.product.interaction.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
public class UserProductInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Long productId;

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

    public static UserProductInteraction create(String userId, Long productId, InteractionType type) {
        return UserProductInteraction.builder()
            .userId(userId)
            .productId(productId)
            .interactionType(type)
            .weight(type.getWeight())
            .build();
    }
}
