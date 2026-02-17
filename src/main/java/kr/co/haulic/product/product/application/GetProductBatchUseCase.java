package kr.co.haulic.product.product.application;

import kr.co.haulic.product.product.application.dto.ProductSummaryResult;

import java.util.List;

public interface GetProductBatchUseCase {
    List<ProductSummaryResult> getProductBatch(List<String> ids);
}
