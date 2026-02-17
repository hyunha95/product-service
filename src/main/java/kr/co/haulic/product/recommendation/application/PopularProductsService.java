package kr.co.haulic.product.recommendation.application;

import kr.co.haulic.product.interaction.domain.UserProductInteractionRepository;
import kr.co.haulic.product.recommendation.domain.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopularProductsService implements GetPopularProductsUseCase {

    private final UserProductInteractionRepository interactionRepository;

    @Override
    public List<Recommendation> getPopularProducts(int limit) {
        List<String> productIds = interactionRepository.findPopularProductIds(limit);
        log.info("Found {} popular products", productIds.size());

        return productIds.stream()
                .map(id -> Recommendation.of(id, 0.0, "인기 상품"))
                .toList();
    }
}
