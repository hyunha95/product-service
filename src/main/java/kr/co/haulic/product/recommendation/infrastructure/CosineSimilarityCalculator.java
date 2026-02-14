package kr.co.haulic.product.recommendation.infrastructure;

import kr.co.haulic.product.recommendation.domain.SimilarityCalculator;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;

@Component
public class CosineSimilarityCalculator implements SimilarityCalculator {

    @Override
    public double calculateCosineSimilarity(String productId1, String productId2,
                                            Map<String, Map<String, Double>> userInteractions) {
        Set<String> users1 = new HashSet<>();
        Set<String> users2 = new HashSet<>();

        for (Map.Entry<String, Map<String, Double>> entry : userInteractions.entrySet()) {
            String userId = entry.getKey();
            Map<String, Double> products = entry.getValue();

            if (products.containsKey(productId1)) {
                users1.add(userId);
            }
            if (products.containsKey(productId2)) {
                users2.add(userId);
            }
        }

        Set<String> commonUsers = new HashSet<>(users1);
        commonUsers.retainAll(users2);

        if (commonUsers.isEmpty()) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (String userId : commonUsers) {
            Map<String, Double> userProducts = userInteractions.get(userId);
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
