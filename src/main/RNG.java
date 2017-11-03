package main;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * @author Anthony Morrell
 * @since 11/3/2017
 */
public final class RNG {

    private static Random random = new Random();

    private RNG() {}

    public static boolean chance(double p) {
        return random.nextDouble() < p;
    }

    public static void chance(double p, Runnable func) {
        if (chance(p)) {
            func.run();
        }
    }

    public static <E> E choose(List<E> domain) {
        assert !domain.isEmpty();
        return domain.get(random.nextInt(domain.size()));
    }

    public static <E> Optional<E> chooseNullable(List<E> domain) {
        if (domain.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(choose(domain));
        }
    }

    public static double range(double max) {
        return range(0, max);
    }

    public static double range(double low, double high) {
        return random.nextDouble() * (high - low) + low;
    }

    public static long range(long low, long high) {
        return (long) (random.nextDouble() * (high - low) + low);
    }

}
