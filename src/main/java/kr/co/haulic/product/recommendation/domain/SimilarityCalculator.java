package kr.co.haulic.product.recommendation.domain;

import java.util.Map;

public interface SimilarityCalculator {
    /**
     * 두 상품 간의 유사도 계산 (코사인 유사도)
     * @param productId1 상품 1
     * @param productId2 상품 2
     * @param userInteractions 사용자 상호작용 데이터
     * @return 유사도 점수 (0.0 ~ 1.0)
     */
    double calculateCosineSimilarity(Long productId1, Long productId2, 
                                     Map<String, Map<Long, Double>> userInteractions);
}
