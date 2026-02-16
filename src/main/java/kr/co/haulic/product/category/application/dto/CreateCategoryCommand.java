package kr.co.haulic.product.category.application.dto;

public record CreateCategoryCommand(String name, Long parentId) {
}
