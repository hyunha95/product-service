package kr.co.haulic.product.category.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Category {

    private final Long id;
    private final String name;
    private final int depth;
    private final Long parentId;
    private final LocalDateTime createdAt;

    public static Category create(String name, Long parentId, int depth) {
        return Category.builder()
                .name(name)
                .parentId(parentId)
                .depth(depth)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public boolean isRoot() {
        return parentId == null;
    }
}
