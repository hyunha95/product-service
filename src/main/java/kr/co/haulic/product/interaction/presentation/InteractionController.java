package kr.co.haulic.product.interaction.presentation;

import jakarta.validation.Valid;
import kr.co.haulic.product.interaction.application.RecordInteractionUseCase;
import kr.co.haulic.product.interaction.presentation.dto.RecordInteractionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
public class InteractionController {

    private final RecordInteractionUseCase recordInteractionUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void recordInteraction(@Valid @RequestBody RecordInteractionRequest request) {
        log.debug("Received interaction request: userId={}, productId={}, type={}",
                request.getUserId(), request.getProductId(), request.getInteractionType());

        recordInteractionUseCase.execute(
            request.getUserId(),
            request.getProductId(),
            request.getInteractionType()
        );
    }
}
