package kr.co.haulic.product.interaction.infrastructure;

import kr.co.haulic.product.interaction.domain.UserProductInteraction;
import kr.co.haulic.product.interaction.domain.UserProductInteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserProductInteractionRepositoryAdapter implements UserProductInteractionRepository {

    private final JpaUserProductInteractionRepository jpaRepository;

    @Override
    public UserProductInteraction save(UserProductInteraction interaction) {
        return jpaRepository.save(interaction);
    }

    @Override
    public List<UserProductInteraction> findByUserId(String userId) {
        return jpaRepository.findByUserId(userId);
    }

    @Override
    public List<UserProductInteraction> findByProductId(Long productId) {
        return jpaRepository.findByProductId(productId);
    }

    @Override
    public List<UserProductInteraction> findAll() {
        return jpaRepository.findAll();
    }
}
