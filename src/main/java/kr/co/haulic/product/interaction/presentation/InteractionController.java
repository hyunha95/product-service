package kr.co.haulic.product.interaction.presentation;

import jakarta.validation.Valid;
import kr.co.haulic.product.interaction.application.RecordInteractionUseCase;
import kr.co.haulic.product.interaction.presentation.dto.RecordInteractionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
public class InteractionController {

    private final RecordInteractionUseCase recordInteractionUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void recordInteraction(@Valid @RequestBody RecordInteractionRequest request) {
        recordInteractionUseCase.execute(
            request.getUserId(),
            request.getProductId(),
            request.getInteractionType()
        );
    }
}
