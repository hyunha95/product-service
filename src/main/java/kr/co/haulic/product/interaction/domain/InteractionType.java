package kr.co.haulic.product.interaction.domain;

public enum InteractionType {
    VIEW(1.0),
    CART(2.0),
    PURCHASE(3.0);

    private final double weight;

    InteractionType(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
}
