package kr.co.haulic.product.product.presentation.dto;

import kr.co.haulic.product.product.application.dto.ProductDetailResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public record ProductDetailResponse(
        String id,
        String name,
        Long price,
        Long originalPrice,
        Double rating,
        Long reviewCount,
        List<String> category,
        List<String> badges,
        List<String> images,
        List<String> description,
        String status,
        Integer stock,
        Boolean isActive,
        LocalDateTime createdAt
) {
    public static ProductDetailResponse from(ProductDetailResult result, Function<String, String> urlConverter) {
        List<String> images = new ArrayList<>();
        if (result.image() != null) {
            images.add(urlConverter.apply(result.image()));
        }
        if (result.additionalImages() != null) {
            result.additionalImages().stream()
                    .map(urlConverter)
                    .forEach(images::add);
        }

        List<String> description = new ArrayList<>();
        if (result.detailDescriptionImage() != null) {
            description.add(urlConverter.apply(result.detailDescriptionImage()));
        }

        List<String> category = new ArrayList<>();
        if (result.category() != null) {
            category.add(result.category());
        }

        List<String> badges = new ArrayList<>();
        if (result.badge() != null) {
            badges.add(result.badge());
        }

        return new ProductDetailResponse(
                result.id(),
                result.name(),
                result.price(),
                result.originalPrice(),
                result.rating(),
                result.reviewCount(),
                category,
                badges,
                images,
                description,
                result.status(),
                result.stock(),
                result.isActive(),
                result.createdAt()
        );
    }
}
