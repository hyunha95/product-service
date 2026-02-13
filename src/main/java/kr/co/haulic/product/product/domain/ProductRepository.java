package kr.co.haulic.product.product.domain;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findAllById(List<Long> ids);
    Product save(Product product);
}
