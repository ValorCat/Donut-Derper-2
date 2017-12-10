package main.model.donut;

/**
 * @since 12/6/2017
 */
public class DonutBatch {

    private final DonutType type;
    private final int amount;

    public DonutBatch(DonutType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public DonutType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

}
