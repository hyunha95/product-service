package kr.co.haulic.product.category.domain.exception;

public class CategoryHasChildrenException extends RuntimeException {

    public CategoryHasChildrenException(Long id) {
        super("Cannot delete category with id: " + id + " because it has child categories");
    }
}
