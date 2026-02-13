package kr.co.haulic.product.interaction.application;

import kr.co.haulic.product.interaction.domain.InteractionType;
import kr.co.haulic.product.interaction.domain.UserProductInteraction;
import kr.co.haulic.product.interaction.domain.UserProductInteractionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordInteractionService implements RecordInteractionUseCase {

    private final UserProductInteractionRepository interactionRepository;

    @Override
    @Transactional
    public void execute(String userId, Long productId, InteractionType type) {
        UserProductInteraction interaction = UserProductInteraction.create(userId, productId, type);
        interactionRepository.save(interaction);
        log.info("Recorded {} interaction for user {} on product {}", type, userId, productId);
    }
}
