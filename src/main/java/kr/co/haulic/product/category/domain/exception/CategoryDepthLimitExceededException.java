package kr.co.haulic.product.category.domain.exception;

public class CategoryDepthLimitExceededException extends RuntimeException {

    public CategoryDepthLimitExceededException(int maxDepth) {
        super("Category depth cannot exceed " + maxDepth);
    }
}
