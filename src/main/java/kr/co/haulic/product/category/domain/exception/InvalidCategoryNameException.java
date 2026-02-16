package kr.co.haulic.product.category.domain.exception;

public class InvalidCategoryNameException extends RuntimeException {

    public InvalidCategoryNameException(String reason) {
        super("Invalid category name: " + reason);
    }
}
