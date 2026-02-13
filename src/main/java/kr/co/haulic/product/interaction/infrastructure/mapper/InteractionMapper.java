package kr.co.haulic.product.interaction.infrastructure.mapper;

import kr.co.haulic.product.interaction.domain.UserProductInteraction;
import kr.co.haulic.product.interaction.infrastructure.entity.UserProductInteractionEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between UserProductInteraction domain model and UserProductInteractionEntity
 * Implements the anti-corruption layer between domain and infrastructure
 */
@Component
public class InteractionMapper {

    /**
     * Convert UserProductInteractionEntity (persistence) to UserProductInteraction (domain model)
     */
    public UserProductInteraction toDomain(UserProductInteractionEntity entity) {
        if (entity == null) {
            return null;
        }

        return UserProductInteraction.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .productId(entity.getProductId())
                .interactionType(entity.getInteractionType())
                .weight(entity.getWeight())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * Convert UserProductInteraction (domain model) to UserProductInteractionEntity (persistence)
     */
    public UserProductInteractionEntity toEntity(UserProductInteraction domain) {
        if (domain == null) {
            return null;
        }

        return UserProductInteractionEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .productId(domain.getProductId())
                .interactionType(domain.getInteractionType())
                .weight(domain.getWeight())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
