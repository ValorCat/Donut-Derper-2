package main.model.ingredient;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @since 12/15/2017
 */
public class IngredientSupplier {

    public static void main(String[] args) {
        for (IngredientSupplier supplier : suppliers) {
            for (IngredientOffer offer : supplier.getOffers(IngredientType.byName("Eggs"), 1, 50)) {
                IngredientBatch batch = offer.getContent();
                System.out.printf("%s: %s (%.1fx) for $%.2f\n", batch.getBrand().getName(), batch.amountTextProperty().get(), batch.getQuality(), offer.getPrice());
            }
        }
        System.out.println("------------------");
        for (IngredientOffer offer : getAllOffers(IngredientType.byName("Eggs"), 1, 50)) {
            IngredientBatch batch = offer.getContent();
            System.out.printf("%s: %s (%.1fx) for $%.2f\n", batch.getBrand().getName(), batch.amountTextProperty().get(), batch.getQuality(), offer.getPrice());
        }
    }

    public static final IngredientSupplier LOCAl = create("Local");

    private static final List<IngredientSupplier> suppliers = List.of(
            create("Bakery Bros.")
                    .defaultValues(1.2, 1.1)
                    .product("All-Purpose Flour").quality(15, 15, 75)
                    .product("Sugar").quality(10, 10, 90),
            create("Floormart")
                .product("All-Purpose Flour")
                    .quality(.7, .9, 5, 5, 25)
                    .quality(.9, 1.05, 1, 1, 15)
                .product("Butter")
                    .quality(.9, .9, 1, 1, 8)
                .product("Eggs")
                    .quality(.7, .8, 1, 1, 8)
                .product("Sugar")
                    .quality(.6, .8, 3, 3, 24)
                .product("Whole Milk")
                    .quality(.8, .85, 1, 1, 8),
            create("Food United")
                .defaultValues(1, 1)
                .product("Eggs").quality(10, 10, 100)
                .product("Whole Milk").quality(5, 5, 30)
    );

    private StringProperty name;
    private Map<IngredientType, Map<Double, OfferData>> offers;
    private IngredientType currentAddingType;
    private double defaultQuality, defaultCostFactor;

    public IngredientSupplier(String name) {
        this.name = new SimpleStringProperty(name);
        this.offers = new HashMap<>();
    }

    public IngredientSupplier defaultValues(double quality, double costFactor) {
        defaultQuality = quality;
        defaultCostFactor = costFactor;
        return this;
    }

    public IngredientSupplier product(String ingredient) {
        currentAddingType = IngredientType.byName(ingredient);
        return this;
    }

    public IngredientSupplier quality(int amountInterval, int minAmount, int maxAmount) {
        return quality(defaultQuality, defaultCostFactor, amountInterval, minAmount, maxAmount);
    }

    public IngredientSupplier quality(double quality, double costFactor,
                                      int amountInterval, int minAmount, int maxAmount) {
        if (currentAddingType == null) {
            throw new IllegalStateException("Must specify an ingredient before adding quality data");
        }
        offers.computeIfAbsent(currentAddingType, x -> new HashMap<>());
        offers.get(currentAddingType).put(quality,
                new OfferData(currentAddingType, quality, costFactor, amountInterval, minAmount, maxAmount));
        return this;
    }

    public List<IngredientOffer> getOffers(IngredientType ingredient, int minAmount, int maxAmount) {
        return offers.containsKey(ingredient)
                ? generate(offers.get(ingredient), minAmount, maxAmount)
                : List.of();
    }

    public String getName() {
        return name.get();
    }

    public final StringProperty nameProperty() {
        return name;
    }

    public String toString() {
        return getName();
    }

    public static List<IngredientOffer> getAllOffers(IngredientType ingredient, int minAmount, int maxAmount) {
        return suppliers.stream()
                .map(supplier -> supplier.getOffers(ingredient, minAmount, maxAmount))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static IngredientSupplier create(String name) {
        return new IngredientSupplier(name);
    }

    private static List<IngredientOffer> generate(Map<Double, OfferData> qualityData, int offerMin, int offerMax) {
        return qualityData.entrySet().stream()
                .map(entry -> entry.getValue().generate(offerMin, offerMax))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private final class OfferData {

        private IngredientType type;
        private double quality, costFactor;
        private int minAmount, maxAmount, amountInterval;

        private OfferData(IngredientType type, double quality, double costFactor, int amountInterval, int minAmount, int maxAmount) {
            this.type = type;
            this.quality = quality;
            this.costFactor = quality * costFactor;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.amountInterval = amountInterval;
        }

        private List<IngredientOffer> generate(int offerMin, int offerMax) {
            List<IngredientOffer> offers = new ArrayList<>();
            double baseCost = type.getBaseValue() * costFactor;
            int baseAmount = type.getBaseAmount();
            int start = Math.max(offerMin, minAmount);
            int end = Math.min(offerMax, maxAmount);
            for (int amount = start; amount <= end; amount += amountInterval) {
                offers.add(new IngredientOffer(new IngredientBatch(type, baseAmount * amount, quality,
                        IngredientSupplier.this), baseCost * amount));
            }
            return offers;
        }

    }

}
