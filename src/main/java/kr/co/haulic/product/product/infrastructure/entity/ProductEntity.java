package kr.co.haulic.product.product.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private String status;

    private String badge;

    private String image;

    @Builder.Default
    @ElementCollection
    @CollectionTable(
            name = "product_additional_images",
            joinColumns = @JoinColumn(
                    name = "product_id",
                    foreignKey = @ForeignKey(name = "fk_product_additional_images_product")
            )
    )
    @OrderColumn(name = "sort_order")
    @Column(name = "image_url", nullable = false, length = 500)
    private List<String> additionalImages = new ArrayList<>();

    private String detailDescriptionImage;

    private Long originalPrice;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = false;

    @Builder.Default
    @Column(nullable = false)
    private Double rating = 0.0;

    @Builder.Default
    @Column(nullable = false)
    private Long reviewCount = 0L;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(nullable = false)
    private String createdBy = "system";

    @Builder.Default
    @Column(nullable = false)
    private String updatedBy = "system";
}
