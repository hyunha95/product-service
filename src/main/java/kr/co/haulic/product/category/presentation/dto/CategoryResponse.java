package kr.co.haulic.product.category.presentation.dto;

import kr.co.haulic.product.category.application.dto.CategoryResult;

import java.time.LocalDateTime;

public record CategoryResponse(Long id, String name, int depth, Long parentId, LocalDateTime createdAt) {

    public static CategoryResponse from(CategoryResult result) {
        return new CategoryResponse(
                result.id(),
                result.name(),
                result.depth(),
                result.parentId(),
                result.createdAt()
        );
    }
}
