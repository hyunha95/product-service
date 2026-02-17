package kr.co.haulic.product.recommendation.presentation;

import kr.co.haulic.product.config.GatewayProperties;
import kr.co.haulic.product.product.domain.Product;
import kr.co.haulic.product.product.domain.ProductRepository;
import kr.co.haulic.product.recommendation.application.GetPersonalizedRecommendationsUseCase;
import kr.co.haulic.product.recommendation.application.GetPopularProductsUseCase;
import kr.co.haulic.product.recommendation.application.GetSimilarProductsUseCase;
import kr.co.haulic.product.recommendation.domain.Recommendation;
import kr.co.haulic.product.recommendation.domain.RecommendationEngine;
import kr.co.haulic.product.recommendation.presentation.dto.RecommendationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final GetPersonalizedRecommendationsUseCase getPersonalizedRecommendationsUseCase;
    private final GetPopularProductsUseCase getPopularProductsUseCase;
    private final GetSimilarProductsUseCase getSimilarProductsUseCase;
    private final RecommendationEngine recommendationEngine;
    private final ProductRepository productRepository;
    private final GatewayProperties gatewayProperties;

    @GetMapping("/personalized")
    public List<RecommendationResponse> getPersonalizedRecommendations(
        @RequestHeader(value = "X-User-Id", required = false) String userId,
        @RequestParam(defaultValue = "10") int limit
    ) {
        log.info("Personalized recommendations request: userId={}, limit={}", userId, limit);

        List<Recommendation> recommendations = List.of();
        if (userId != null && !userId.isBlank()) {
            recommendations = getPersonalizedRecommendationsUseCase.getPersonalizedRecommendations(userId, limit);
            log.info("Generated {} personalized recommendations for userId={}", recommendations.size(), userId);
        }

        if (recommendations.isEmpty()) {
            recommendations = getPopularProductsUseCase.getPopularProducts(limit);
            log.info("Falling back to {} popular products", recommendations.size());
        }

        List<RecommendationResponse> response = enrichWithProductInfo(recommendations);
        log.info("Returning {} recommendations", response.size());
        return response;
    }

    @GetMapping("/similar/{productId}")
    public List<RecommendationResponse> getSimilarProducts(
        @PathVariable String productId,
        @RequestParam(defaultValue = "10") int limit
    ) {
        log.info("Similar products request: productId={}, limit={}", productId, limit);

        List<Recommendation> recommendations = getSimilarProductsUseCase.getSimilarProducts(productId, limit);
        log.info("Generated {} raw similar products for productId={}", recommendations.size(), productId);

        List<RecommendationResponse> response = enrichWithProductInfo(recommendations);
        log.info("Returning {} enriched similar products for productId={}", response.size(), productId);
        return response;
    }

    @PostMapping("/rebuild")
    public void rebuildModel() {
        log.info("Recommendation model rebuild requested");
        recommendationEngine.rebuildRecommendationModel();
    }

    private List<RecommendationResponse> enrichWithProductInfo(List<Recommendation> recommendations) {
        if (recommendations.isEmpty()) {
            return List.of();
        }

        List<String> productIds = recommendations.stream()
            .map(Recommendation::getProductId)
            .collect(Collectors.toList());

        List<Product> products = productRepository.findAllById(productIds);
        Map<String, Product> productMap = products.stream()
            .collect(Collectors.toMap(Product::getId, product -> product));

        if (productMap.size() < productIds.size()) {
            Set<String> foundIds = productMap.keySet();
            List<String> missingIds = productIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();
            log.warn("Product lookup mismatch: requested {} IDs, found {} products. Missing IDs: {}",
                    productIds.size(), productMap.size(), missingIds);
        }

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
