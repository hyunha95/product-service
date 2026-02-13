package kr.co.haulic.product.recommendation.infrastructure;

import kr.co.haulic.product.recommendation.domain.SimilarityCalculator;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;

@Component
public class CosineSimilarityCalculator implements SimilarityCalculator {

    @Override
    public double calculateCosineSimilarity(Long productId1, Long productId2, 
                                            Map<String, Map<Long, Double>> userInteractions) {
        // 두 상품을 모두 상호작용한 사용자 찾기
        Set<String> users1 = new HashSet<>();
        Set<String> users2 = new HashSet<>();

        for (Map.Entry<String, Map<Long, Double>> entry : userInteractions.entrySet()) {
            String userId = entry.getKey();
            Map<Long, Double> products = entry.getValue();

            if (products.containsKey(productId1)) {
                users1.add(userId);
            }
            if (products.containsKey(productId2)) {
                users2.add(userId);
            }
        }

        // 공통 사용자
        Set<String> commonUsers = new HashSet<>(users1);
        commonUsers.retainAll(users2);

        if (commonUsers.isEmpty()) {
            return 0.0;
        }

        // 코사인 유사도 계산
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (String userId : commonUsers) {
            Map<Long, Double> userProducts = userInteractions.get(userId);
            double rating1 = userProducts.getOrDefault(productId1, 0.0);
            double rating2 = userProducts.getOrDefault(productId2, 0.0);

            dotProduct += rating1 * rating2;
            norm1 += rating1 * rating1;
            norm2 += rating2 * rating2;
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
