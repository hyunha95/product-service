package kr.co.haulic.product.product.application;

import kr.co.haulic.product.product.application.dto.ProductDetailResult;

public interface GetProductDetailUseCase {
    ProductDetailResult getProductDetail(String id);
}
