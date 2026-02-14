package kr.co.haulic.product.recommendation.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.haulic.product.recommendation.domain.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RecommendationCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${recommendation.similarity.cache-ttl:86400}")
    private long similarityCacheTtl;

    @Value("${recommendation.user-recommendations.cache-ttl:3600}")
    private long userRecommendationsCacheTtl;

    private static final String USER_RECOMMENDATIONS_KEY_PREFIX = "rec:user:";
    private static final String SIMILAR_PRODUCTS_KEY_PREFIX = "rec:similar:";
    private static final String SIMILARITY_MATRIX_KEY = "rec:matrix:similarity";

    public void saveUserRecommendations(String userId, List<Recommendation> recommendations) {
        try {
            String key = USER_RECOMMENDATIONS_KEY_PREFIX + userId;
            String value = objectMapper.writeValueAsString(recommendations);
            redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(userRecommendationsCacheTtl));
            log.debug("Cached user recommendations for userId: {}", userId);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize recommendations for userId: {}", userId, e);
        }
    }

    public List<Recommendation> getUserRecommendations(String userId) {
        try {
            String key = USER_RECOMMENDATIONS_KEY_PREFIX + userId;
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            return objectMapper.readValue(value, new TypeReference<List<Recommendation>>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize recommendations for userId: {}", userId, e);
            return null;
        }
    }

    public void saveSimilarProducts(String productId, List<Recommendation> recommendations) {
        try {
            String key = SIMILAR_PRODUCTS_KEY_PREFIX + productId;
            String value = objectMapper.writeValueAsString(recommendations);
            redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(similarityCacheTtl));
            log.debug("Cached similar products for productId: {}", productId);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize similar products for productId: {}", productId, e);
        }
    }

    public List<Recommendation> getSimilarProducts(String productId) {
        try {
            String key = SIMILAR_PRODUCTS_KEY_PREFIX + productId;
            String value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            return objectMapper.readValue(value, new TypeReference<List<Recommendation>>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize similar products for productId: {}", productId, e);
            return null;
        }
    }

    public void saveSimilarityMatrix(Map<String, Map<String, Double>> similarityMatrix) {
        try {
            String value = objectMapper.writeValueAsString(similarityMatrix);
            redisTemplate.opsForValue().set(SIMILARITY_MATRIX_KEY, value, Duration.ofSeconds(similarityCacheTtl));
            log.info("Cached similarity matrix with {} products", similarityMatrix.size());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize similarity matrix", e);
        }
    }

    public Map<String, Map<String, Double>> getSimilarityMatrix() {
        try {
            String value = redisTemplate.opsForValue().get(SIMILARITY_MATRIX_KEY);
            if (value == null) {
                return Collections.emptyMap();
            }
            return objectMapper.readValue(value, new TypeReference<Map<String, Map<String, Double>>>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize similarity matrix", e);
            return Collections.emptyMap();
        }
    }

    public void clearAllCaches() {
        redisTemplate.delete(redisTemplate.keys("rec:*"));
        log.info("Cleared all recommendation caches");
    }
}
