package kr.co.haulic.product.category.application;

import kr.co.haulic.product.category.application.dto.CategoryResult;

import java.util.List;

public interface GetCategoriesUseCase {
    List<CategoryResult> getAll();
    CategoryResult getById(Long id);
}
