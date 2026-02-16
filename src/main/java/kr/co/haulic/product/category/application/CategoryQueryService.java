package kr.co.haulic.product.category.application;

import kr.co.haulic.product.category.application.dto.CategoryResult;
import kr.co.haulic.product.category.application.dto.CategoryTreeResult;
import kr.co.haulic.product.category.domain.Category;
import kr.co.haulic.product.category.domain.CategoryRepository;
import kr.co.haulic.product.category.domain.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryService implements GetCategoriesUseCase, GetCategoryTreeUseCase {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResult> getAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryResult::from)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResult getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return CategoryResult.from(category);
    }

    @Override
    public List<CategoryTreeResult> execute() {
        List<Category> categories = categoryRepository.findAllOrderByDepthAndId();

        Map<Long, CategoryTreeResult> nodeMap = new LinkedHashMap<>();
        Map<Long, List<CategoryTreeResult>> childrenMap = new HashMap<>();

        for (Category category : categories) {
            List<CategoryTreeResult> children = new ArrayList<>();
            CategoryTreeResult node = new CategoryTreeResult(
                    String.valueOf(category.getId()),
                    category.getName(),
                    children
            );
            nodeMap.put(category.getId(), node);
            childrenMap.put(category.getId(), children);

            if (category.getParentId() != null) {
                List<CategoryTreeResult> parentChildren = childrenMap.get(category.getParentId());
                if (parentChildren != null) {
                    parentChildren.add(node);
                }
            }
        }

        return categories.stream()
                .filter(Category::isRoot)
                .map(c -> nodeMap.get(c.getId()))
                .collect(Collectors.toList());
    }
}
