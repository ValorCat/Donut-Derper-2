package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Anthony Morrell
 * @since 11/3/2017
 */
public final class RNG {

    private static final String NAMES = "names.txt";
    private static Random random = new Random();
    private static List<String> names;

    static {
        try (InputStream stream = RNG.class.getResourceAsStream(NAMES)) {
            names = new BufferedReader(new InputStreamReader(stream)).lines().collect(Collectors.toList());
        } catch (IOException e) {
            System.err.printf("Failed to read %s for %s\n", NAMES, RNG.class.getName());
            e.printStackTrace();
            names = List.of("?");
        }
    }

    private RNG() {}

    public static void chance(double p, Runnable func) {
        if (chance(p)) {
            func.run();
        }
    }

    public static boolean chance(double p) {
        return random.nextDouble() < p;
    }

    public static <E> Optional<E> chooseNullable(List<E> domain) {
        if (domain.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(choose(domain));
        }
    }

    public static <E> E choose(List<E> domain) {
        assert !domain.isEmpty();
        return domain.get(random.nextInt(domain.size()));
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

    public static String name() {
        return choose(names);
    }

}
