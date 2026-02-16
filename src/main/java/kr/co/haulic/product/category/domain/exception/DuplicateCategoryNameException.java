package kr.co.haulic.product.category.domain.exception;

public class DuplicateCategoryNameException extends RuntimeException {

    public DuplicateCategoryNameException(String name) {
        super("Category with name '" + name + "' already exists in the same level");
    }
}
