package kr.co.haulic.product.category.presentation;

import jakarta.validation.Valid;
import kr.co.haulic.product.category.application.*;
import kr.co.haulic.product.category.application.dto.CreateCategoryCommand;
import kr.co.haulic.product.category.application.dto.UpdateCategoryCommand;
import kr.co.haulic.product.category.presentation.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final GetCategoriesUseCase getCategoriesUseCase;
    private final GetCategoryTreeUseCase getCategoryTreeUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@Valid @RequestBody CreateCategoryRequest request) {
        log.debug("Create category request: name={}, parentId={}", request.getName(), request.getParentId());

        var command = new CreateCategoryCommand(request.getName(), request.getParentId());
        var result = createCategoryUseCase.execute(command);
        return CategoryResponse.from(result);
    }

    @GetMapping
    public List<CategoryResponse> getAll() {
        return getCategoriesUseCase.getAll().stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/tree")
    public List<CategoryTreeResponse> getTree() {
        return getCategoryTreeUseCase.execute().stream()
                .map(CategoryTreeResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CategoryResponse getById(@PathVariable Long id) {
        var result = getCategoriesUseCase.getById(id);
        return CategoryResponse.from(result);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest request) {
        log.debug("Update category request: id={}, name={}", id, request.getName());

        var command = new UpdateCategoryCommand(request.getName());
        var result = updateCategoryUseCase.execute(id, command);
        return CategoryResponse.from(result);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.debug("Delete category request: id={}", id);
        deleteCategoryUseCase.execute(id);
    }
}
