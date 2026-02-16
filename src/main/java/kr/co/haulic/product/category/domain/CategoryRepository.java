package kr.co.haulic.product.category.domain;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(Long id);

    List<Category> findAll();

    List<Category> findAllOrderByDepthAndId();

    List<Category> findByParentId(Long parentId);

    boolean existsByNameAndParentId(String name, Long parentId);

    boolean existsByNameAndParentIdIsNull(String name);

    void deleteById(Long id);
}
