package kr.co.haulic.product.recommendation.application;

import kr.co.haulic.product.recommendation.domain.Recommendation;

import java.util.List;

public interface GetPopularProductsUseCase {
    List<Recommendation> getPopularProducts(int limit);
}
