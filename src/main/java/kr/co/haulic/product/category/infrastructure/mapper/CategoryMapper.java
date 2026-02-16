package kr.co.haulic.product.category.infrastructure.mapper;

import kr.co.haulic.product.category.domain.Category;
import kr.co.haulic.product.category.infrastructure.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toDomain(CategoryEntity entity) {
        if (entity == null) {
            return null;
        }

        return Category.builder()
                .id(entity.getId())
                .name(entity.getName())
                .depth(entity.getDepth())
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public CategoryEntity toEntity(Category domain, CategoryEntity parentEntity) {
        if (domain == null) {
            return null;
        }

        return CategoryEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .depth(domain.getDepth())
                .parent(parentEntity)
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
