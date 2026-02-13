package kr.co.haulic.product.recommendation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
public class Recommendation implements Serializable {
    private Long productId;
    private Double score;
    private String reason;

    public static Recommendation of(Long productId, Double score, String reason) {
        return Recommendation.builder()
            .productId(productId)
            .score(score)
            .reason(reason)
            .build();
    }
}
