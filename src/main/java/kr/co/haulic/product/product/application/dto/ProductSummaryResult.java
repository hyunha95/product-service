package kr.co.haulic.product.product.application.dto;

import kr.co.haulic.product.product.domain.Product;

public record ProductSummaryResult(
        String id,
        String name,
        Long price,
        Long originalPrice,
        String image,
        Double rating,
        Long reviewCount,
        String badge,
        String category
) {
    public static ProductSummaryResult from(Product product) {
        return new ProductSummaryResult(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getOriginalPrice(),
                product.getImage(),
                product.getRating(),
                product.getReviewCount(),
                product.getBadge(),
                product.getCategory()
        );
    }
}
