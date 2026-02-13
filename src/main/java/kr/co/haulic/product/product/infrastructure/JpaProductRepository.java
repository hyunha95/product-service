package kr.co.haulic.product.product.infrastructure;

import kr.co.haulic.product.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductRepository extends JpaRepository<Product, Long> {
}
