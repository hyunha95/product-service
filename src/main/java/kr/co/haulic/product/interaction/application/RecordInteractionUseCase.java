package kr.co.haulic.product.interaction.application;

import kr.co.haulic.product.interaction.domain.InteractionType;

public interface RecordInteractionUseCase {
    void execute(String userId, Long productId, InteractionType type);
}
