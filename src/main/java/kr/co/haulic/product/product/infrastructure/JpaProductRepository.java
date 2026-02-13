package kr.co.haulic.product.product.infrastructure;

import kr.co.haulic.product.product.infrastructure.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA Repository for ProductEntity persistence
 */
public interface JpaProductRepository extends JpaRepository<ProductEntity, Long> {
}
