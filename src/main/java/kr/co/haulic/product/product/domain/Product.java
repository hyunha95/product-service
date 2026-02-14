package kr.co.haulic.product.product.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product {

    private final String id;
    private final String name;
    private final String category;
    private final Long price;
    private final Integer stock;
    private final String status;
    private final String badge;
    private final String image;
    @Builder.Default
    private final List<String> additionalImages = List.of();
    private final String detailDescriptionImage;
    private final Long originalPrice;
    private final Boolean isActive;
    private final Double rating;
    private final Long reviewCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String createdBy;
    private final String updatedBy;

    public boolean hasValidPrice() {
        return price != null && price > 0;
    }

    public boolean isAvailableForPurchase() {
        return isActive && hasValidPrice();
    }
}
