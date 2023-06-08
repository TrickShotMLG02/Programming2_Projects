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

    public static MarkInterface[] createMarkInterfaceWin() {
        MarkInterface[] marks = createMarkInterface(Symbol.EMPTY, 9);
        marks[0] = UTTTFactory.createMark(Symbol.CROSS, 0);
        marks[1] = UTTTFactory.createMark(Symbol.CROSS, 1);
        marks[2] = UTTTFactory.createMark(Symbol.CIRCLE, 2);
        marks[3] = UTTTFactory.createMark(Symbol.CROSS, 3);
        marks[4] = UTTTFactory.createMark(Symbol.CIRCLE, 4);
        marks[6] = UTTTFactory.createMark(Symbol.CIRCLE, 6);

        return marks;
    }

    public static MarkInterface[] createMarkInterfaceWin(Symbol winner) {

        Symbol looser = winner.flip();

        MarkInterface[] marks = createMarkInterface(Symbol.EMPTY, 9);
        marks[0] = UTTTFactory.createMark(looser, 0);
        marks[1] = UTTTFactory.createMark(looser, 1);
        marks[2] = UTTTFactory.createMark(winner, 2);
        marks[3] = UTTTFactory.createMark(looser, 3);
        marks[4] = UTTTFactory.createMark(winner, 4);
        marks[6] = UTTTFactory.createMark(winner, 6);

        return marks;
    }

    public static MarkInterface[] createMarkInterfaceTie() {
        MarkInterface[] marks = createMarkInterface(Symbol.EMPTY, 9);
        marks[0] = UTTTFactory.createMark(Symbol.CROSS, 0);
        marks[1] = UTTTFactory.createMark(Symbol.CIRCLE, 1);
        marks[2] = UTTTFactory.createMark(Symbol.CROSS, 2);
        marks[3] = UTTTFactory.createMark(Symbol.CROSS, 3);
        marks[4] = UTTTFactory.createMark(Symbol.CIRCLE, 4);
        marks[5] = UTTTFactory.createMark(Symbol.CROSS, 5);
        marks[6] = UTTTFactory.createMark(Symbol.CIRCLE, 6);
        marks[7] = UTTTFactory.createMark(Symbol.CROSS, 7);
        marks[8] = UTTTFactory.createMark(Symbol.CIRCLE, 8);

        return marks;
    }
}
