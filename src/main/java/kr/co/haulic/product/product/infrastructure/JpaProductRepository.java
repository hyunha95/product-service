package kr.co.haulic.product.product.infrastructure;

import kr.co.haulic.product.product.infrastructure.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductRepository extends JpaRepository<ProductEntity, String> {
}
