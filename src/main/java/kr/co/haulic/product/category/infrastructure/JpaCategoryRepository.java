package kr.co.haulic.product.category.infrastructure;

import kr.co.haulic.product.category.infrastructure.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, Long> {

    List<CategoryEntity> findAllByOrderByDepthAscIdAsc();

    List<CategoryEntity> findByParentId(Long parentId);

    boolean existsByNameAndParentId(String name, Long parentId);

    boolean existsByNameAndParentIsNull(String name);
}
