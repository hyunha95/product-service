package kr.co.haulic.product.category.application;

import kr.co.haulic.product.category.application.dto.CategoryResult;
import kr.co.haulic.product.category.application.dto.CreateCategoryCommand;

public interface CreateCategoryUseCase {
    CategoryResult execute(CreateCategoryCommand command);
}
