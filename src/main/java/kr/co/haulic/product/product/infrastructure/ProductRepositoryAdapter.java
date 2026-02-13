package kr.co.haulic.product.product.infrastructure;

import kr.co.haulic.product.product.domain.Product;
import kr.co.haulic.product.product.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;

    @Override
    public Optional<Product> findById(Long id) {
        return jpaProductRepository.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return jpaProductRepository.findAll();
    }

    @Override
    public List<Product> findAllById(List<Long> ids) {
        return jpaProductRepository.findAllById(ids);
    }

    @Override
    public Product save(Product product) {
        return jpaProductRepository.save(product);
    }
}
