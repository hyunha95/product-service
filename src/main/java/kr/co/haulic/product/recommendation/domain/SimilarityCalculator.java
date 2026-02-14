package kr.co.haulic.product.recommendation.domain;

import java.util.Map;

public interface SimilarityCalculator {
    double calculateCosineSimilarity(String productId1, String productId2,
                                     Map<String, Map<String, Double>> userInteractions);
}
