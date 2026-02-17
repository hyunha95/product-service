package kr.co.haulic.product.product.presentation;

import kr.co.haulic.product.config.GatewayProperties;
import kr.co.haulic.product.product.application.GetProductBatchUseCase;
import kr.co.haulic.product.product.application.GetProductDetailUseCase;
import kr.co.haulic.product.product.application.GetProductListUseCase;
import kr.co.haulic.product.product.presentation.dto.BatchProductRequest;
import kr.co.haulic.product.product.presentation.dto.ProductDetailResponse;
import kr.co.haulic.product.product.presentation.dto.ProductSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final GetProductDetailUseCase getProductDetailUseCase;
    private final GetProductListUseCase getProductListUseCase;
    private final GetProductBatchUseCase getProductBatchUseCase;
    private final GatewayProperties gatewayProperties;

    @GetMapping("/{id}")
    public ProductDetailResponse getProductDetail(@PathVariable String id) {
        return ProductDetailResponse.from(
                getProductDetailUseCase.getProductDetail(id),
                this::convertToGatewayUrl
        );
    }

    @GetMapping
    public List<ProductSummaryResponse> getProductList() {
        return getProductListUseCase.getProductList().stream()
                .map(result -> ProductSummaryResponse.from(result, this::convertToGatewayUrl))
                .toList();
    }

    @PostMapping("/batch")
    public List<ProductSummaryResponse> getProductBatch(@RequestBody BatchProductRequest request) {
        return getProductBatchUseCase.getProductBatch(request.ids()).stream()
                .map(result -> ProductSummaryResponse.from(result, this::convertToGatewayUrl))
                .toList();
    }

    private String convertToGatewayUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return imageUrl;
        }

        if (imageUrl.startsWith(gatewayProperties.getBaseUrl())) {
            return imageUrl;
        }

        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return imageUrl;
        }

        if (imageUrl.startsWith("/")) {
            return gatewayProperties.getBaseUrl() + imageUrl;
        }

        return gatewayProperties.getBaseUrl() + "/" + imageUrl;
    }
}
