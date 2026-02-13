package kr.co.haulic.product.product.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pure domain model for Product
 * Contains business logic and validation rules
 * No persistence concerns (JPA annotations removed)
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product {

    private final Long id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final String imageUrl;
    private final String categoryId;
    private final Integer viewCount;
    private final Integer purchaseCount;
    private final Boolean isActive;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    /**
     * Factory method for creating a new product
     */
    public static Product create(
            String name,
            String description,
            BigDecimal price,
            String imageUrl,
            String categoryId
    ) {
        LocalDateTime now = LocalDateTime.now();
        return Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .imageUrl(imageUrl)
                .categoryId(categoryId)
                .viewCount(0)
                .purchaseCount(0)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Domain logic: Increment view count
     * Returns a new instance (immutable pattern)
     */
    public Product incrementViewCount() {
        return this.toBuilder()
                .viewCount(this.viewCount + 1)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Domain logic: Increment purchase count
     * Returns a new instance (immutable pattern)
     */
    public Product incrementPurchaseCount() {
        return this.toBuilder()
                .purchaseCount(this.purchaseCount + 1)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Domain logic: Deactivate product
     */
    public Product deactivate() {
        return this.toBuilder()
                .isActive(false)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Domain logic: Activate product
     */
    public Product activate() {
        return this.toBuilder()
                .isActive(true)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Business validation: Check if price is valid
     */
    public boolean hasValidPrice() {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Business validation: Check if product is available for purchase
     */
    public boolean isAvailableForPurchase() {
        return isActive && hasValidPrice();
    }
}
