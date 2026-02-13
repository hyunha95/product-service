package kr.co.haulic.product.product.infrastructure;

import kr.co.haulic.product.product.domain.Product;
import kr.co.haulic.product.product.domain.ProductRepository;
import kr.co.haulic.product.product.infrastructure.entity.ProductEntity;
import kr.co.haulic.product.product.infrastructure.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementation of ProductRepository
 * Translates between domain models and persistence entities using ProductMapper
 */
@Repository
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;
    private final ProductMapper productMapper;

    @Override
    public Optional<Product> findById(Long id) {
        return jpaProductRepository.findById(id)
                .map(productMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return jpaProductRepository.findAll().stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findAllById(List<Long> ids) {
        return jpaProductRepository.findAllById(ids).stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity;

        if (product.getId() != null) {
            // Update existing entity
            entity = jpaProductRepository.findById(product.getId())
                    .map(existing -> productMapper.updateEntity(existing, product))
                    .orElseGet(() -> productMapper.toEntity(product));
        } else {
            // Create new entity
            entity = productMapper.toEntity(product);
        }

        ProductEntity savedEntity = jpaProductRepository.save(entity);
        return productMapper.toDomain(savedEntity);
    }
}
