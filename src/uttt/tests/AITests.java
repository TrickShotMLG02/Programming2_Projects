package uttt.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uttt.UTTTFactory;
import uttt.game.BoardInterface;
import uttt.game.PlayerInterface;
import uttt.game.SimulatorInterface;
import uttt.game.UserInterface;
import uttt.utils.Symbol;

public class AITests {

    SimulatorInterface simulator;
    BoardInterface board;

    PlayerInterface ai;
    PlayerInterface human;

    @Before
    public void setUp() throws Exception {
        // create simulator

        ai = UTTTFactory.createBonusPlayer(Symbol.CROSS);
        human = UTTTFactory.createHumanPlayer(Symbol.CIRCLE);
    }

    // test tic tac toe
    @Test
    public void oneMoveToWinTest() {

        // create board to be won by ai
        board = UTTTFactory.createBoard();

        // setup marks for looser
        Symbol looser = ai.getSymbol().flip();
        board.setMarkAt(ai.getSymbol(), 0);
        board.setMarkAt(ai.getSymbol(), 1);

        // let markIndex 2 empty for ai
        board.setMarkAt(looser, 3);
        board.setMarkAt(looser, 4);

        // DONT CREATE UI SINCE IT CAUSES SECURITY VIOLATION
        // create simple tic tac toe ui
        // UserInterface ui = UTTTFactory.createUserInterface(false);

        // should have no winner yet
        assertEquals(Symbol.EMPTY, board.getWinner());

        // grab ai move
        int mi = ai.getPlayerMove(board, null);

        // set mark at move
        board.setMarkAt(ai.getSymbol(), mi);

        // ai should have won the game
        assertEquals(ai.getSymbol(), board.getWinner());

    }

}
