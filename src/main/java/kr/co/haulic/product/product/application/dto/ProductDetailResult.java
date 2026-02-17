package kr.co.haulic.product.product.application.dto;

import kr.co.haulic.product.product.domain.Product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record ProductDetailResult(
        String id,
        String name,
        Long price,
        Long originalPrice,
        Double rating,
        Long reviewCount,
        String category,
        String badge,
        String image,
        List<String> additionalImages,
        String detailDescriptionImage,
        String status,
        Integer stock,
        Boolean isActive,
        LocalDateTime createdAt
) {
    public static ProductDetailResult from(Product product) {
        return new ProductDetailResult(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getOriginalPrice(),
                product.getRating(),
                product.getReviewCount(),
                product.getCategory(),
                product.getBadge(),
                product.getImage(),
                product.getAdditionalImages(),
                product.getDetailDescriptionImage(),
                product.getStatus(),
                product.getStock(),
                product.getIsActive(),
                product.getCreatedAt()
        );
    }
}
