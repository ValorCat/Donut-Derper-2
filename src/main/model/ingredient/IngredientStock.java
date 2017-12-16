package main.model.ingredient;

/**
 * @since 12/10/2017
 */
public class IngredientStock extends IngredientBatch {

    public IngredientStock(IngredientBatch batch) {
        super(batch.getType(), batch.getAmount(), batch.getQuality(), batch.getBrand());
    }

    public void increase(int amount) {
        this.amount.set(getAmount() + amount);
    }

    public void decrease(int amount) {
        this.amount.set(Math.max(0, getAmount() - amount));
    }

}
