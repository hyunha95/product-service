package kr.co.haulic.product.recommendation.application;

import kr.co.haulic.product.recommendation.domain.Recommendation;

import java.util.List;

public interface GetSimilarProductsUseCase {
    List<Recommendation> getSimilarProducts(String productId, int limit);
}
