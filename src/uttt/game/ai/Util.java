package uttt.game.ai;

import java.util.Random;

public class Util {
    /**
     * @param min lower bound
     * @param max upper bound
     * @return random double value between lower bound and upper bound
     */
    public static double rndVal(double min, double max) {
        Random random = new Random();
        double rnd = random.nextDouble() * (max - min) + min;
        return rnd;
    }

    /**
     * @param min lower bound
     * @param max upper bound
     * @return random integer value between lower bound and upper bound
     */
    public static int rndInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max + 1 - min) + min;
    }
}
