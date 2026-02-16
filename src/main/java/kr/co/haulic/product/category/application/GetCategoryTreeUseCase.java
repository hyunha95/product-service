package kr.co.haulic.product.category.application;

import kr.co.haulic.product.category.application.dto.CategoryTreeResult;

import java.util.List;

public interface GetCategoryTreeUseCase {
    List<CategoryTreeResult> execute();
}
