package kr.co.haulic.product.interaction.infrastructure;

import kr.co.haulic.product.interaction.domain.UserProductInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaUserProductInteractionRepository extends JpaRepository<UserProductInteraction, Long> {
    List<UserProductInteraction> findByUserId(String userId);
    List<UserProductInteraction> findByProductId(Long productId);
}
