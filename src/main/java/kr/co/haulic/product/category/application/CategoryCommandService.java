package kr.co.haulic.product.category.application;

import kr.co.haulic.product.category.application.dto.CategoryResult;
import kr.co.haulic.product.category.application.dto.CreateCategoryCommand;
import kr.co.haulic.product.category.application.dto.UpdateCategoryCommand;
import kr.co.haulic.product.category.domain.Category;
import kr.co.haulic.product.category.domain.CategoryRepository;
import kr.co.haulic.product.category.domain.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryCommandService implements CreateCategoryUseCase, UpdateCategoryUseCase, DeleteCategoryUseCase {

    private static final int MAX_DEPTH = 4;

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResult execute(CreateCategoryCommand command) {
        String name = validateAndTrimName(command.name());
        Long parentId = command.parentId();
        int depth = 0;

        if (parentId != null) {
            Category parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new CategoryNotFoundException(parentId));
            depth = parent.getDepth() + 1;

            if (depth >= MAX_DEPTH) {
                throw new CategoryDepthLimitExceededException(MAX_DEPTH);
            }

            if (categoryRepository.existsByNameAndParentId(name, parentId)) {
                throw new DuplicateCategoryNameException(name);
            }
        } else {
            if (categoryRepository.existsByNameAndParentIdIsNull(name)) {
                throw new DuplicateCategoryNameException(name);
            }
        }

        Category category = Category.create(name, parentId, depth);
        Category saved = categoryRepository.save(category);

        log.info("Created category: id={}, name={}, depth={}, parentId={}", saved.getId(), saved.getName(), saved.getDepth(), saved.getParentId());
        return CategoryResult.from(saved);
    }

    @Override
    @Transactional
    public CategoryResult execute(Long id, UpdateCategoryCommand command) {
        String name = validateAndTrimName(command.name());

        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (existing.getParentId() != null) {
            if (categoryRepository.existsByNameAndParentId(name, existing.getParentId())) {
                if (!existing.getName().equals(name)) {
                    throw new DuplicateCategoryNameException(name);
                }
            }
        } else {
            if (categoryRepository.existsByNameAndParentIdIsNull(name)) {
                if (!existing.getName().equals(name)) {
                    throw new DuplicateCategoryNameException(name);
                }
            }
        }

        Category updated = existing.toBuilder().name(name).build();
        Category saved = categoryRepository.save(updated);

        log.info("Updated category: id={}, name={}", saved.getId(), saved.getName());
        return CategoryResult.from(saved);
    }

    @Override
    @Transactional
    public void execute(Long id) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        List<Category> children = categoryRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new CategoryHasChildrenException(id);
        }

        categoryRepository.deleteById(id);
        log.info("Deleted category: id={}, name={}", existing.getId(), existing.getName());
    }

    private String validateAndTrimName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidCategoryNameException("name must not be blank");
        }
        String trimmed = name.trim();
        if (trimmed.length() > 100) {
            throw new InvalidCategoryNameException("name must not exceed 100 characters");
        }
        return trimmed;
    }
}
