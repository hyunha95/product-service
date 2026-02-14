package kr.co.haulic.product.recommendation.infrastructure;

import kr.co.haulic.product.interaction.domain.UserProductInteraction;
import kr.co.haulic.product.interaction.domain.UserProductInteractionRepository;
import kr.co.haulic.product.product.domain.Product;
import kr.co.haulic.product.product.domain.ProductRepository;
import kr.co.haulic.product.recommendation.domain.Recommendation;
import kr.co.haulic.product.recommendation.domain.RecommendationEngine;
import kr.co.haulic.product.recommendation.domain.SimilarityCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemBasedCollaborativeFilteringEngine implements RecommendationEngine {

    private final UserProductInteractionRepository interactionRepository;
    private final ProductRepository productRepository;
    private final SimilarityCalculator similarityCalculator;
    private final RecommendationCacheRepository cacheRepository;

    private Map<String, Map<String, Double>> similarityMatrix = new HashMap<>();
    private volatile boolean isModelBuilt = false;

    @Override
    public List<Recommendation> generatePersonalizedRecommendations(String userId, int limit) {
        log.info("Generating personalized recommendations for user: {}", userId);

        List<Recommendation> cachedRecommendations = cacheRepository.getUserRecommendations(userId);
        if (cachedRecommendations != null && !cachedRecommendations.isEmpty()) {
            log.info("Found cached recommendations for user: {}", userId);
            return cachedRecommendations.stream()
                .limit(limit)
                .collect(Collectors.toList());
        }

        if (!isModelBuilt) {
            rebuildRecommendationModel();
        }

        List<UserProductInteraction> userInteractions = interactionRepository.findByUserId(userId);
        if (userInteractions.isEmpty()) {
            log.info("No user interactions found, returning popular products");
            return getPopularProducts(limit);
        }

        Map<String, Double> candidateScores = new HashMap<>();
        Set<String> interactedProductIds = userInteractions.stream()
            .map(UserProductInteraction::getProductId)
            .collect(Collectors.toSet());

        for (UserProductInteraction interaction : userInteractions) {
            String productId = interaction.getProductId();
            Double interactionWeight = interaction.getWeight();

            Map<String, Double> similarProducts = similarityMatrix.get(productId);
            if (similarProducts == null) continue;

            for (Map.Entry<String, Double> entry : similarProducts.entrySet()) {
                String candidateId = entry.getKey();
                Double similarity = entry.getValue();

                if (interactedProductIds.contains(candidateId)) continue;

                double score = similarity * interactionWeight;
                candidateScores.merge(candidateId, score, Double::sum);
            }
        }

        List<Recommendation> recommendations = candidateScores.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(limit)
            .map(e -> Recommendation.of(e.getKey(), e.getValue(), "Based on similar products"))
            .collect(Collectors.toList());

        if (!recommendations.isEmpty()) {
            cacheRepository.saveUserRecommendations(userId, recommendations);
        }

        log.info("Generated {} recommendations for user: {}", recommendations.size(), userId);
        return recommendations;
    }

    @Override
    public List<Recommendation> generateSimilarProducts(String productId, int limit) {
        log.info("Generating similar products for product: {}", productId);

        List<Recommendation> cachedSimilar = cacheRepository.getSimilarProducts(productId);
        if (cachedSimilar != null && !cachedSimilar.isEmpty()) {
            log.info("Found cached similar products for product: {}", productId);
            return cachedSimilar.stream()
                .limit(limit)
                .collect(Collectors.toList());
        }

        if (!isModelBuilt) {
            rebuildRecommendationModel();
        }

        Map<String, Double> similarProducts = similarityMatrix.get(productId);
        if (similarProducts == null || similarProducts.isEmpty()) {
            log.info("No similar products found for product: {}", productId);
            return Collections.emptyList();
        }

        List<Recommendation> recommendations = similarProducts.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(limit)
            .map(e -> Recommendation.of(e.getKey(), e.getValue(), "Similar product"))
            .collect(Collectors.toList());

        if (!recommendations.isEmpty()) {
            cacheRepository.saveSimilarProducts(productId, recommendations);
        }

        log.info("Generated {} similar products for product: {}", recommendations.size(), productId);
        return recommendations;
    }

    @Override
    public synchronized void rebuildRecommendationModel() {
        log.info("Starting recommendation model rebuild...");

        List<UserProductInteraction> allInteractions = interactionRepository.findAll();
        if (allInteractions.isEmpty()) {
            log.warn("No interactions found, model build skipped");
            return;
        }

        Map<String, Map<String, Double>> userProductMatrix = new HashMap<>();
        for (UserProductInteraction interaction : allInteractions) {
            userProductMatrix
                .computeIfAbsent(interaction.getUserId(), k -> new HashMap<>())
                .merge(interaction.getProductId(), interaction.getWeight(), Double::sum);
        }

        Set<String> allProductIds = allInteractions.stream()
            .map(UserProductInteraction::getProductId)
            .collect(Collectors.toSet());

        Map<String, Map<String, Double>> newSimilarityMatrix = new HashMap<>();
        List<String> productIdList = new ArrayList<>(allProductIds);

        for (int i = 0; i < productIdList.size(); i++) {
            String productId1 = productIdList.get(i);
            Map<String, Double> similarities = new HashMap<>();

            for (int j = 0; j < productIdList.size(); j++) {
                if (i == j) continue;

                String productId2 = productIdList.get(j);
                double similarity = similarityCalculator.calculateCosineSimilarity(
                    productId1, productId2, userProductMatrix
                );

                if (similarity > 0.1) {
                    similarities.put(productId2, similarity);
                }
            }

            if (!similarities.isEmpty()) {
                newSimilarityMatrix.put(productId1, similarities);
            }
        }

        this.similarityMatrix = newSimilarityMatrix;
        this.isModelBuilt = true;

        cacheRepository.saveSimilarityMatrix(newSimilarityMatrix);

        log.info("Recommendation model rebuild completed. Processed {} products", allProductIds.size());
    }

    private List<Recommendation> getPopularProducts(int limit) {
        return productRepository.findAll().stream()
            .filter(p -> p.getReviewCount() != null)
            .sorted(Comparator.comparingLong(Product::getReviewCount).reversed())
            .limit(limit)
            .map(p -> Recommendation.of(p.getId(), (double) p.getReviewCount(), "Popular product"))
            .collect(Collectors.toList());
    }
}
