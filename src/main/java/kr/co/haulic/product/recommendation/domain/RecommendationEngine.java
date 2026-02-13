package kr.co.haulic.product.recommendation.domain;

import java.util.List;

public interface RecommendationEngine {
    /**
     * 사용자 기반 개인화 추천 생성
     */
    List<Recommendation> generatePersonalizedRecommendations(String userId, int limit);

    /**
     * 상품 기반 유사 상품 추천
     */
    List<Recommendation> generateSimilarProducts(Long productId, int limit);

    /**
     * 추천 모델 재학습 (배치 작업용)
     */
    void rebuildRecommendationModel();
}
