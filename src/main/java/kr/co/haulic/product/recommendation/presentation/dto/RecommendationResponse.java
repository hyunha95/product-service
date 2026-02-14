package kr.co.haulic.product.recommendation.presentation.dto;

import kr.co.haulic.product.product.domain.Product;
import kr.co.haulic.product.recommendation.domain.Recommendation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RecommendationResponse {

    private String productId;
    private Double score;
    private String reason;

    // 상품 정보
    private String name;
    private String imageUrl;
    private Long price;
    private String category;

    public static RecommendationResponse from(Recommendation recommendation, Product product, String imageUrl) {
        return RecommendationResponse.builder()
            .productId(recommendation.getProductId())
            .score(recommendation.getScore())
            .reason(recommendation.getReason())
            .name(product.getName())
            .imageUrl(imageUrl)
            .price(product.getPrice())
            .category(product.getCategory())
            .build();
    }
}
