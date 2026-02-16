package kr.co.haulic.product.category.application.dto;

import kr.co.haulic.product.category.domain.Category;

import java.time.LocalDateTime;

public record CategoryResult(Long id, String name, int depth, Long parentId, LocalDateTime createdAt) {

    public static CategoryResult from(Category category) {
        return new CategoryResult(
                category.getId(),
                category.getName(),
                category.getDepth(),
                category.getParentId(),
                category.getCreatedAt()
        );
    }
}
