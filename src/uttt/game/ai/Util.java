package uttt.game.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uttt.game.BoardInterface;
import uttt.game.MarkInterface;
import uttt.game.SimulatorInterface;
import uttt.utils.Move;
import uttt.utils.Symbol;

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

    public static Move generateRandomValidMove(SimulatorInterface sim) {
        int nextBoardIndex = sim.getIndexNextBoard();
        BoardInterface[] boards = sim.getBoards();

        // Create a list to store the indices of valid moves
        List<Integer> validBoards = new ArrayList<>();
        List<Integer> validMarks = new ArrayList<>();

        // Iterate over all boards
        for (int i = 0; i < boards.length; i++) {
            // Check if the board is closed
            if (boards[i].isClosed()) {
                continue; // Skip closed boards
            }

            // Check if nextBoardIndex allows placing a symbol on this board
            if (nextBoardIndex == -1 || nextBoardIndex == i) {
                validBoards.add(i);
            }
        }

        // If there are no valid moves, return -1 or handle the case appropriately
        if (validBoards.isEmpty()) {
            return null; // No valid moves
        }

        // Generate a random index from the valid moves list
        int randomIndex = (int) (Math.random() * validBoards.size());

        // Return the randomly selected valid move
        int rndBoardIndex = validBoards.get(randomIndex);

        // iterate through all marks on that board and check for valid mark positions
        for (int i = 0; i < boards[rndBoardIndex].getMarks().length; i++) {
            if (boards[rndBoardIndex].getMarks()[i].getSymbol() == Symbol.EMPTY) {
                // add mark index to valid marks
                validMarks.add(i);
            }
        }

        randomIndex = (int) (Math.random() * validMarks.size());
        int rndMarkIndex = validMarks.get(randomIndex);

        Move rndMove = new Move(rndBoardIndex, rndMarkIndex);
        return rndMove;

    }

    public static boolean contains2SymbolsAndEmpty(SimulatorInterface sim, Symbol symbolToTest, int boardIndex,
            int indexA, int indexB, int indexC) {
        BoardInterface board = sim.getBoards()[boardIndex];
        MarkInterface[] marks = board.getMarks();

        Symbol symA = marks[indexA].getSymbol();
        Symbol symB = marks[indexB].getSymbol();
        Symbol symC = marks[indexC].getSymbol();

        // check if two symbols are equal and one is empty
        if ((symA == symB && symC == Symbol.EMPTY) || (symA == symC && symB == Symbol.EMPTY)
                || (symB == symC && symA == Symbol.EMPTY)) {
            // return true if those symbols are the symbol to test
            return symA == symbolToTest || symB == symbolToTest || symC == symbolToTest;
        }
        return false;

    }
}
