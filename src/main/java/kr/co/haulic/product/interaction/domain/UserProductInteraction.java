package kr.co.haulic.product.interaction.domain;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Pure domain model for UserProductInteraction
 * Represents a user's interaction with a product (VIEW, CART, PURCHASE)
 * No persistence concerns (JPA annotations removed)
 */
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserProductInteraction {

    private final Long id;
    private final String userId;
    private final String productId;
    private final InteractionType interactionType;
    private final Double weight;
    private final LocalDateTime createdAt;

    /**
     * Factory method for creating a new user-product interaction
     * Automatically assigns weight based on interaction type
     */
    public static UserProductInteraction create(String userId, String productId, InteractionType type) {
        return UserProductInteraction.builder()
                .userId(userId)
                .productId(productId)
                .interactionType(type)
                .weight(type.getWeight())
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Business validation: Check if interaction is recent (within last 30 days)
     */
    public boolean isRecent() {
        return createdAt != null &&
               createdAt.isAfter(LocalDateTime.now().minusDays(30));
    }

    /**
     * Business validation: Check if this is a high-value interaction
     */
    public boolean isHighValue() {
        return interactionType == InteractionType.PURCHASE ||
               interactionType == InteractionType.CART;
    }

    /**
     * Domain logic: Check if this interaction involves the same product as another
     */
    public boolean isSameProduct(UserProductInteraction other) {
        return other != null && this.productId.equals(other.productId);
    }

    /**
     * Domain logic: Check if this interaction is from the same user as another
     */
    public boolean isSameUser(UserProductInteraction other) {
        return other != null && this.userId.equals(other.userId);
    }
}
