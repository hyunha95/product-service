package kr.co.haulic.product.product.presentation.dto;

import java.util.List;

public record BatchProductRequest(
        List<String> ids
) {
}
