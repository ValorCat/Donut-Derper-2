package main.model;

import main.Game;
import main.RNG;

/**
 * @since 12/14/2017
 */
public abstract class Time {

    public static long now, last;

    private static final long NANO = (long) Math.pow(10, 9);

    protected long nanoTime;

    private Time(long nanoTime) {
        this.nanoTime = nanoTime;
    }

    public static Moment blankTime() {
        return new Moment(0);
    }

    public static Moment now() {
        return new Moment(now);
    }

    public static Period duration(double seconds) {
        return new Period((long) (seconds * NANO));
    }

    public static Period randomDuration(double low, double high) {
        return duration(RNG.range(low, high));
    }

    public static void tick(long now) {
        Time.now = now;
        Moment timeStamp = new Moment(now);
        Period delta = timeStamp.since(last);
        update(timeStamp, delta);
        last = now;
    }

    private static void update(Moment now, Period delta) {
        boolean isInterestDay = Account.readyForInterestDeposit();
        boolean isPayday = Account.readyForPayday(delta);
        for (Location l : Game.game.getLocations()) {
            l.update(now, delta, isInterestDay, isPayday);
        }
    }

    public static class Moment extends Time {

        public Moment(long nanoTime) {
            super(nanoTime);
        }

        public boolean hasPassed() {
            return now >= nanoTime;
        }

        public void pushBack(Period length) {
            nanoTime = Math.max(nanoTime, now) + length.nanoTime;
        }

        public Period since(Moment other) {
            if (nanoTime < other.nanoTime) {
                throw new IllegalArgumentException("Cannot use since when first time occurred after the second");
            }
            return new Period(nanoTime - other.nanoTime);
        }

        private Period since(long other) {
            return new Period(nanoTime - other);
        }

    }

    public static class Period extends Time {

        public Period(long nanoTime) {
            super(nanoTime);
        }

        public Moment fromNow() {
            return new Moment(now + nanoTime);
        }

        public Timer asTimer() {
            return new Timer(nanoTime);
        }

        public boolean lessThan(Period other) {
            return nanoTime < other.nanoTime;
        }

    }

    public static class Timer extends Time {

        private final long start;

        public Timer(long nanoTime) {
            super(nanoTime);
            start = nanoTime;
        }

        public void update(Period delta) {
            if (nanoTime > 0) {
                nanoTime -= delta.nanoTime;
            }
        }

        public void reset() {
            nanoTime = start;
        }

        public boolean isDone() {
            return nanoTime <= 0;
        }

        public double getProgress() {
            return (double) (start - nanoTime) / start;
        }

        public int getSeconds() {
            return (int) Math.round((double) nanoTime / NANO);
        }

    }

}
