package kr.co.haulic.product.recommendation.application;

import kr.co.haulic.product.recommendation.domain.Recommendation;
import kr.co.haulic.product.recommendation.domain.RecommendationEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService implements GetPersonalizedRecommendationsUseCase, GetSimilarProductsUseCase {

    private final RecommendationEngine recommendationEngine;

    @Override
    public List<Recommendation> getPersonalizedRecommendations(String userId, int limit) {
        return recommendationEngine.generatePersonalizedRecommendations(userId, limit);
    }

    @Override
    public List<Recommendation> getSimilarProducts(String productId, int limit) {
        return recommendationEngine.generateSimilarProducts(productId, limit);
    }
}
