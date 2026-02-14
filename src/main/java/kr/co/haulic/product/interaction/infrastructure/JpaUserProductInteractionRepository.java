package kr.co.haulic.product.interaction.infrastructure;

import kr.co.haulic.product.interaction.infrastructure.entity.UserProductInteractionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * JPA Repository for UserProductInteractionEntity persistence
 */
public interface JpaUserProductInteractionRepository extends JpaRepository<UserProductInteractionEntity, Long> {
    List<UserProductInteractionEntity> findByUserId(String userId);
    List<UserProductInteractionEntity> findByProductId(String productId);
}
