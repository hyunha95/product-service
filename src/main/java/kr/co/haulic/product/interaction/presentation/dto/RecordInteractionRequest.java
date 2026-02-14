package kr.co.haulic.product.interaction.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.haulic.product.interaction.domain.InteractionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecordInteractionRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotNull(message = "Interaction type is required")
    private InteractionType interactionType;
}
