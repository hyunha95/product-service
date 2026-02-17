package kr.co.haulic.product.product.presentation.dto;

import kr.co.haulic.product.product.application.dto.ProductSummaryResult;

import java.util.function.Function;

public record ProductSummaryResponse(
        String id,
        String name,
        Long price,
        Long originalPrice,
        String imageUrl,
        Double rating,
        Long reviewCount,
        String badge,
        String category
) {
    public static ProductSummaryResponse from(ProductSummaryResult result, Function<String, String> urlConverter) {
        return new ProductSummaryResponse(
                result.id(),
                result.name(),
                result.price(),
                result.originalPrice(),
                urlConverter.apply(result.image()),
                result.rating(),
                result.reviewCount(),
                result.badge(),
                result.category()
        );
    }
}
