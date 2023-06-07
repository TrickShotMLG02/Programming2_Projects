package uttt.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.lang.model.util.ElementScanner14;

import uttt.utils.Symbol;

public class Board implements BoardInterface {

    private MarkInterface[] marks;

    // +---+---+---+
    // | 0 | 1 | 2 |
    // +---+---+---+
    // | 3 | 4 | 5 |
    // +---+---+---+
    // | 6 | 7 | 8 |
    // +---+---+---+

    public Board() {
        // create marks array with 9 empty marks while index of array matches mark
        // position
        marks = new MarkInterface[9];
        for (int i = 0; i < marks.length; i++) {
            marks[i] = new Mark(Symbol.EMPTY, i);
        }
    }

    @Override
    public MarkInterface[] getMarks() {
        return marks;
    }

    @Override
    public void setMarks(MarkInterface[] marks) throws IllegalArgumentException {
        if (marks.length != 9)
            throw new IllegalArgumentException("There must be 9 marks");

        // check if every index in the marks is different by comparing each one with all
        // others
        for (int i = 0; i < marks.length; i++) {
            for (int j = 0; j < marks.length; j++) {
                if (i != j && marks[i].getPosition() == marks[j].getPosition())
                    throw new IllegalArgumentException("at least 2 marks share same position");
            }
        }

        // sort marks by position so that mark with pos 0 is at index 0 and so on
        ArrayList<MarkInterface> marksList = new ArrayList<MarkInterface>(Arrays.asList(marks));
        Collections.sort(marksList, (o1, o2) -> Integer.compare(o1.getPosition(), o2.getPosition()));
        this.marks = marksList.toArray(new MarkInterface[marksList.size()]);
    }

    @Override
    public boolean setMarkAt(Symbol symbol, int markIndex) throws IllegalArgumentException {
        // check if there is an empty mark at index markIndex
        if (marks[markIndex].getSymbol() == Symbol.EMPTY) {
            marks[markIndex].setSymbol(symbol);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isClosed() {
        // check if all marks are not empty or if there is a winner
        for (int i = 0; i < marks.length; i++) {
            if (isMovePossible(i)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMovePossible(int markIndex) throws IllegalArgumentException {

        // check if there is a winner
        if (hasWinner() != Symbol.EMPTY) {
            // no move possible
            return false;
        }

        // check if current index is empty symbol
        if (marks[markIndex].getSymbol() == Symbol.EMPTY) {
            // move possible since current symbol is empty
            return true;
        } else {
            // move not possible since current symbol is not empty
            return false;
        }
    }

    /**
     * Returns the symbol of the winner if there is one. otherwise return empty
     * symbol. Also return empty if there are still moves possible
     * 
     * @return Symbol of winner or empty symbol
     */
    @Override
    public Symbol getWinner() {

        // if board is closed there is a winner
        if (isClosed()) {
            // return winner
            return hasWinner();
        }

        // otherwise return empty symbol
        return Symbol.EMPTY;

    }

    /**
     * Returns the symbol of the winner if there is one. otherwise return empty
     * symbol. Also return empty if there are still moves possible
     * 
     * @return Symbol of winner or empty symbol
     */
    private Symbol hasWinner() {
        Symbol winner;

        // iterate over all rows
        for (int i = 0; i < 3; i += 3) {
            winner = checkForEquality(i, i + 1, i + 2);
            if (winner != Symbol.EMPTY) {
                return winner;
            }
        }

        // iterate over all columns
        for (int i = 0; i < 3; i++) {
            winner = checkForEquality(i, i + 3, i + 6);
            if (winner != Symbol.EMPTY) {
                return winner;
            }
        }

        // check diagonals
        for (int i = 0; i < 2; i += 2) {
            winner = checkForEquality(i, 4, 8 - i);
            if (winner != Symbol.EMPTY) {
                return winner;
            }
        }

        // otherwise return empty symbol since there is no winner
        return Symbol.EMPTY;
    }

    /**
     * Checks if symbols at index a,b,c are equal and not empty
     * 
     * @param a index of first symbol
     * @param b index of second symbol
     * @param c index of third symbol
     * @return Symbol of winner or empty symbol
     */
    private Symbol checkForEquality(int a, int b, int c) {

        // check if symbols at index a,b,c are equal and not empty
        if (marks[a].getSymbol() == marks[b].getSymbol() && marks[b].getSymbol() == marks[c].getSymbol()
                && marks[a].getSymbol() != Symbol.EMPTY) {
            return marks[a].getSymbol();
        }
        return Symbol.EMPTY;

    }

}
