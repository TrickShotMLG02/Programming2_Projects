package uttt.tests;

import java.util.Random;

import uttt.UTTTFactory;
import uttt.game.MarkInterface;
import uttt.utils.Symbol;

public class util {

    // return MarkInterface[] with size size and random symbols
    public static MarkInterface[] createMarkInterface(int size) {
        MarkInterface[] marks = new MarkInterface[size];
        for (int i = 0; i < size; i++) {
            marks[i] = UTTTFactory.createMark(rndSymbol(), i % 9);
        }

        return marks;
    }

    public static MarkInterface[] createMarkInterface(Symbol symbol, int size) {
        MarkInterface[] marks = new MarkInterface[9];
        for (int i = 0; i < 9; i++) {
            marks[i] = UTTTFactory.createMark(Symbol.EMPTY, i);
        }

        return marks;
    }

    // create random intger between min and max
    // https://stackoverflow.com/questions/2444019/how-do-i-generate-a-random-integer-between-min-and-max-in-java
    public static int rndInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max + 1 - min) + min;
    }

    // create random Symbol
    public static Symbol rndSymbol() {
        // 0: Empty, 1: Cross, 2: Circle
        int rnd = rndInt(0, 2);

        switch (rnd) {
            case 0:
                return Symbol.EMPTY;
            case 1:
                return Symbol.CROSS;
            case 2:
                return Symbol.CIRCLE;
            default:
                return null;
        }
    }
}
