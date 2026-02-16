package kr.co.haulic.product.category.infrastructure;

import kr.co.haulic.product.category.domain.Category;
import kr.co.haulic.product.category.domain.CategoryRepository;
import kr.co.haulic.product.category.infrastructure.entity.CategoryEntity;
import kr.co.haulic.product.category.infrastructure.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepository {

    private final JpaCategoryRepository jpaRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Category save(Category category) {
        CategoryEntity parentEntity = null;
        if (category.getParentId() != null) {
            parentEntity = jpaRepository.findById(category.getParentId()).orElse(null);
        }
        CategoryEntity entity = categoryMapper.toEntity(category, parentEntity);
        CategoryEntity savedEntity = jpaRepository.save(entity);
        return categoryMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return jpaRepository.findById(id).map(categoryMapper::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll().stream()
                .map(categoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Category> findAllOrderByDepthAndId() {
        return jpaRepository.findAllByOrderByDepthAscIdAsc().stream()
                .map(categoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Category> findByParentId(Long parentId) {
        return jpaRepository.findByParentId(parentId).stream()
                .map(categoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByNameAndParentId(String name, Long parentId) {
        return jpaRepository.existsByNameAndParentId(name, parentId);
    }

    @Override
    public boolean existsByNameAndParentIdIsNull(String name) {
        return jpaRepository.existsByNameAndParentIsNull(name);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
