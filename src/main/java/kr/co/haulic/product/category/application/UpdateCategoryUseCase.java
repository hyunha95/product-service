package kr.co.haulic.product.category.application;

import kr.co.haulic.product.category.application.dto.CategoryResult;
import kr.co.haulic.product.category.application.dto.UpdateCategoryCommand;

public interface UpdateCategoryUseCase {
    CategoryResult execute(Long id, UpdateCategoryCommand command);
}
