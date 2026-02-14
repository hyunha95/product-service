package kr.co.haulic.product.interaction.application;

import kr.co.haulic.product.interaction.domain.InteractionType;
import kr.co.haulic.product.interaction.domain.UserProductInteraction;
import kr.co.haulic.product.interaction.domain.UserProductInteractionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordInteractionService implements RecordInteractionUseCase {

    private final UserProductInteractionRepository interactionRepository;

    @Override
    @Transactional
    public void execute(String userId, String productId, InteractionType type) {
        log.info("Recording {} interaction for user {} on product {}", type, userId, productId);

        try {
            UserProductInteraction interaction = UserProductInteraction.create(userId, productId, type);
            interactionRepository.save(interaction);
            log.info("Successfully recorded {} interaction for user {} on product {}", type, userId, productId);
        } catch (DataAccessException ex) {
            log.error("Database error while recording {} interaction for user {} on product {} - {}",
                    type, userId, productId, ex.getMostSpecificCause().getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while recording {} interaction for user {} on product {} - {}",
                    type, userId, productId, ex.getMessage(), ex);
            throw ex;
        }
    }
}
