package kr.co.haulic.product.recommendation.presentation;

import kr.co.haulic.product.config.GatewayProperties;
import kr.co.haulic.product.product.domain.Product;
import kr.co.haulic.product.product.domain.ProductRepository;
import kr.co.haulic.product.recommendation.application.GetPersonalizedRecommendationsUseCase;
import kr.co.haulic.product.recommendation.application.GetSimilarProductsUseCase;
import kr.co.haulic.product.recommendation.domain.Recommendation;
import kr.co.haulic.product.recommendation.domain.RecommendationEngine;
import kr.co.haulic.product.recommendation.presentation.dto.RecommendationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final GetPersonalizedRecommendationsUseCase getPersonalizedRecommendationsUseCase;
    private final GetSimilarProductsUseCase getSimilarProductsUseCase;
    private final RecommendationEngine recommendationEngine;
    private final ProductRepository productRepository;
    private final GatewayProperties gatewayProperties;

    @GetMapping("/personalized")
    public List<RecommendationResponse> getPersonalizedRecommendations(
        @RequestParam String userId,
        @RequestParam(defaultValue = "10") int limit
    ) {
        List<Recommendation> recommendations = getPersonalizedRecommendationsUseCase.execute(userId, limit);
        return enrichWithProductInfo(recommendations);
    }

    @GetMapping("/similar/{productId}")
    public List<RecommendationResponse> getSimilarProducts(
        @PathVariable Long productId,
        @RequestParam(defaultValue = "10") int limit
    ) {
        List<Recommendation> recommendations = getSimilarProductsUseCase.execute(productId, limit);
        return enrichWithProductInfo(recommendations);
    }

    @PostMapping("/rebuild")
    public void rebuildModel() {
        recommendationEngine.rebuildRecommendationModel();
    }

    private List<RecommendationResponse> enrichWithProductInfo(List<Recommendation> recommendations) {
        // 추천된 상품 ID 목록 추출
        List<Long> productIds = recommendations.stream()
            .map(Recommendation::getProductId)
            .collect(Collectors.toList());

        // 상품 정보 일괄 조회
        Map<Long, Product> productMap = productRepository.findAllById(productIds).stream()
            .collect(Collectors.toMap(Product::getId, product -> product));

        // Recommendation과 Product 정보를 결합하여 응답 생성
        return recommendations.stream()
            .filter(rec -> productMap.containsKey(rec.getProductId()))
            .map(rec -> {
                Product product = productMap.get(rec.getProductId());
                String imageUrl = convertToGatewayUrl(product.getImageUrl());
                return RecommendationResponse.from(rec, product, imageUrl);
            })
            .collect(Collectors.toList());
    }

    /**
     * 이미지 URL을 API Gateway를 통한 URL로 변환
     */
    private String convertToGatewayUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return imageUrl;
        }

        // 이미 Gateway URL인 경우 그대로 반환
        if (imageUrl.startsWith(gatewayProperties.getBaseUrl())) {
            return imageUrl;
        }

        // 절대 URL인 경우 경로만 추출
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            // URL에서 경로 부분만 추출 (예: http://localhost:9090/uploads/... → /uploads/...)
            int pathStart = imageUrl.indexOf("/", imageUrl.indexOf("://") + 3);
            if (pathStart != -1) {
                String path = imageUrl.substring(pathStart);
                return gatewayProperties.getBaseUrl() + path;
            }
        }

        // 상대 경로인 경우 Gateway URL과 결합
        if (imageUrl.startsWith("/")) {
            return gatewayProperties.getBaseUrl() + imageUrl;
        }

        // 그 외에는 Gateway URL + "/" + imageUrl
        return gatewayProperties.getBaseUrl() + "/" + imageUrl;
    }
}
