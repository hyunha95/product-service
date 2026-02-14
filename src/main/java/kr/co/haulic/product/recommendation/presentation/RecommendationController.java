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
        List<Recommendation> recommendations = getPersonalizedRecommendationsUseCase.getPersonalizedRecommendations(userId, limit);
        return enrichWithProductInfo(recommendations);
    }

    @GetMapping("/similar/{productId}")
    public List<RecommendationResponse> getSimilarProducts(
        @PathVariable String productId,
        @RequestParam(defaultValue = "10") int limit
    ) {
        List<Recommendation> recommendations = getSimilarProductsUseCase.getSimilarProducts(productId, limit);
        return enrichWithProductInfo(recommendations);
    }

    @PostMapping("/rebuild")
    public void rebuildModel() {
        recommendationEngine.rebuildRecommendationModel();
    }

    private List<RecommendationResponse> enrichWithProductInfo(List<Recommendation> recommendations) {
        List<String> productIds = recommendations.stream()
            .map(Recommendation::getProductId)
            .collect(Collectors.toList());

        Map<String, Product> productMap = productRepository.findAllById(productIds).stream()
            .collect(Collectors.toMap(Product::getId, product -> product));

        return recommendations.stream()
            .filter(rec -> productMap.containsKey(rec.getProductId()))
            .map(rec -> {
                Product product = productMap.get(rec.getProductId());
                return RecommendationResponse.from(rec, product, convertToGatewayUrl(product.getImage()));
            })
            .collect(Collectors.toList());
    }

    private String convertToGatewayUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return imageUrl;
        }

        if (imageUrl.startsWith(gatewayProperties.getBaseUrl())) {
            return imageUrl;
        }

        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return imageUrl;
        }

        if (imageUrl.startsWith("/")) {
            return gatewayProperties.getBaseUrl() + imageUrl;
        }

        return gatewayProperties.getBaseUrl() + "/" + imageUrl;
    }
}
