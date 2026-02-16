package kr.co.haulic.product.category.application.dto;

import java.util.List;

public record CategoryTreeResult(String id, String name, List<CategoryTreeResult> children) {
}
