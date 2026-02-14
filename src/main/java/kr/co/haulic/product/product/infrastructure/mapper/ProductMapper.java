package kr.co.haulic.product.product.infrastructure.mapper;

import kr.co.haulic.product.product.domain.Product;
import kr.co.haulic.product.product.infrastructure.entity.ProductEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductMapper {

    public Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }

        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .category(entity.getCategory())
                .price(entity.getPrice())
                .stock(entity.getStock())
                .status(entity.getStatus())
                .badge(entity.getBadge())
                .image(entity.getImage())
                .additionalImages(entity.getAdditionalImages() != null
                        ? List.copyOf(entity.getAdditionalImages())
                        : List.of())
                .detailDescriptionImage(entity.getDetailDescriptionImage())
                .originalPrice(entity.getOriginalPrice())
                .isActive(entity.getIsActive())
                .rating(entity.getRating())
                .reviewCount(entity.getReviewCount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public ProductEntity toEntity(Product domain) {
        if (domain == null) {
            return null;
        }

        return ProductEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .category(domain.getCategory())
                .price(domain.getPrice())
                .stock(domain.getStock())
                .status(domain.getStatus())
                .badge(domain.getBadge())
                .image(domain.getImage())
                .additionalImages(domain.getAdditionalImages() != null
                        ? new ArrayList<>(domain.getAdditionalImages())
                        : new ArrayList<>())
                .detailDescriptionImage(domain.getDetailDescriptionImage())
                .originalPrice(domain.getOriginalPrice())
                .isActive(domain.getIsActive())
                .rating(domain.getRating())
                .reviewCount(domain.getReviewCount())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
