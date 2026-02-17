package kr.co.haulic.product.product.application;

import kr.co.haulic.product.product.application.dto.ProductDetailResult;
import kr.co.haulic.product.product.application.dto.ProductSummaryResult;
import kr.co.haulic.product.product.domain.ProductRepository;
import kr.co.haulic.product.product.domain.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductQueryService implements GetProductDetailUseCase, GetProductListUseCase, GetProductBatchUseCase {

    private final ProductRepository productRepository;

    @Override
    public ProductDetailResult getProductDetail(String id) {
        return productRepository.findById(id)
                .map(ProductDetailResult::from)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public List<ProductSummaryResult> getProductList() {
        return productRepository.findAll().stream()
                .map(ProductSummaryResult::from)
                .toList();
    }

    @Override
    public List<ProductSummaryResult> getProductBatch(List<String> ids) {
        return productRepository.findAllById(ids).stream()
                .map(ProductSummaryResult::from)
                .toList();
    }
}
