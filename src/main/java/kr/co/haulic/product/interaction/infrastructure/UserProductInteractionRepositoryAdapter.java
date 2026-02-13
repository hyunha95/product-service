package kr.co.haulic.product.interaction.infrastructure;

import kr.co.haulic.product.interaction.domain.UserProductInteraction;
import kr.co.haulic.product.interaction.domain.UserProductInteractionRepository;
import kr.co.haulic.product.interaction.infrastructure.entity.UserProductInteractionEntity;
import kr.co.haulic.product.interaction.infrastructure.mapper.InteractionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter implementation of UserProductInteractionRepository
 * Translates between domain models and persistence entities using InteractionMapper
 */
@Repository
@RequiredArgsConstructor
public class UserProductInteractionRepositoryAdapter implements UserProductInteractionRepository {

    private final JpaUserProductInteractionRepository jpaRepository;
    private final InteractionMapper interactionMapper;

    @Override
    public UserProductInteraction save(UserProductInteraction interaction) {
        UserProductInteractionEntity entity = interactionMapper.toEntity(interaction);
        UserProductInteractionEntity savedEntity = jpaRepository.save(entity);
        return interactionMapper.toDomain(savedEntity);
    }

    @Override
    public List<UserProductInteraction> findByUserId(String userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(interactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserProductInteraction> findByProductId(Long productId) {
        return jpaRepository.findByProductId(productId).stream()
                .map(interactionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserProductInteraction> findAll() {
        return jpaRepository.findAll().stream()
                .map(interactionMapper::toDomain)
                .collect(Collectors.toList());
    }
}
