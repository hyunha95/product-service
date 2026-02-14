package kr.co.haulic.product.interaction.domain;

import java.util.List;

public interface UserProductInteractionRepository {
    UserProductInteraction save(UserProductInteraction interaction);
    List<UserProductInteraction> findByUserId(String userId);
    List<UserProductInteraction> findByProductId(String productId);
    List<UserProductInteraction> findAll();
}
