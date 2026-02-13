package kr.co.haulic.product.product.infrastructure.mapper;

import kr.co.haulic.product.product.domain.Product;
import kr.co.haulic.product.product.infrastructure.entity.ProductEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Product domain model and ProductEntity
 * Implements the anti-corruption layer between domain and infrastructure
 */
@Component
public class ProductMapper {

    /**
     * Convert ProductEntity (persistence) to Product (domain model)
     */
    public Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }

        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .categoryId(entity.getCategoryId())
                .viewCount(entity.getViewCount())
                .purchaseCount(entity.getPurchaseCount())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convert Product (domain model) to ProductEntity (persistence)
     */
    public ProductEntity toEntity(Product domain) {
        if (domain == null) {
            return null;
        }

        return ProductEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .price(domain.getPrice())
                .imageUrl(domain.getImageUrl())
                .categoryId(domain.getCategoryId())
                .viewCount(domain.getViewCount())
                .purchaseCount(domain.getPurchaseCount())
                .isActive(domain.getIsActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    /**
     * Update existing ProductEntity with data from Product domain model
     * Preserves entity identity and timestamps
     */
    public ProductEntity updateEntity(ProductEntity entity, Product domain) {
        if (entity == null || domain == null) {
            return entity;
        }

        return ProductEntity.builder()
                .id(entity.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .price(domain.getPrice())
                .imageUrl(domain.getImageUrl())
                .categoryId(domain.getCategoryId())
                .viewCount(domain.getViewCount())
                .purchaseCount(domain.getPurchaseCount())
                .isActive(domain.getIsActive())
                .createdAt(entity.getCreatedAt())  // Preserve original creation time
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
