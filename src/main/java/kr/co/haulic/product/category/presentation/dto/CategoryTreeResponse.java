package kr.co.haulic.product.category.presentation.dto;

import kr.co.haulic.product.category.application.dto.CategoryTreeResult;

import java.util.List;
import java.util.stream.Collectors;

public record CategoryTreeResponse(String id, String name, List<CategoryTreeResponse> children) {

    public static CategoryTreeResponse from(CategoryTreeResult result) {
        return new CategoryTreeResponse(
                result.id(),
                result.name(),
                result.children().stream()
                        .map(CategoryTreeResponse::from)
                        .collect(Collectors.toList())
        );
    }
}
