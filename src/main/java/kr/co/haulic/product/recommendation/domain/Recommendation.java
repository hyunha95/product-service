package kr.co.haulic.product.recommendation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation implements Serializable {
    private String productId;
    private Double score;
    private String reason;

    public static Recommendation of(String productId, Double score, String reason) {
        return Recommendation.builder()
            .productId(productId)
            .score(score)
            .reason(reason)
            .build();
    }
}
