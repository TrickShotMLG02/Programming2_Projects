package uttt.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uttt.UTTTFactory;
import uttt.game.BoardInterface;
import uttt.game.SimulatorInterface;
import uttt.utils.Symbol;

public class SimulatorInterfaceTest {

	SimulatorInterface simulator0;
	SimulatorInterface simulator9;
	SimulatorInterface simulator10;

	BoardInterface[] boardAmount0;
	BoardInterface[] boardAmount9;
	BoardInterface[] boardAmount10;

	@Before
	public void setUp() throws Exception {
		// create simulator
		simulator0 = UTTTFactory.createSimulator();
		simulator9 = UTTTFactory.createSimulator();
		simulator10 = UTTTFactory.createSimulator();

		// create boards
		boardAmount0 = util.createBoardInterface(0);
		boardAmount9 = util.createBoardInterface(9);
		boardAmount10 = util.createBoardInterface(10);
	}

	@Test
	public void simulatorNotNullTest() {
		// test if simulator objects are not null
		assertNotNull(simulator0);
		assertNotNull(simulator9);
		assertNotNull(simulator10);
	}

	@Test
	public void setBoardsValidTest() {
		// test for valid board arrays
		simulator9.setBoards(boardAmount9);
	}

	@Test
	public void setBoardsInvalidTest() {
		// test for invalid boardArray size exception
		assertThrows(IllegalArgumentException.class, () -> {
			simulator0.setBoards(boardAmount0);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			simulator0.setBoards(boardAmount0);
		});

	}

	@Test
	public void getBoardsValidTest() {
		setBoardsValidTest();

		assertArrayEquals(boardAmount9, simulator9.getBoards());
	}

	@Test
	public void setCurrentPlayerSymbolValidTest() {
		simulator0.setCurrentPlayerSymbol(Symbol.CROSS);
		simulator9.setCurrentPlayerSymbol(Symbol.CIRCLE);
		simulator10.setCurrentPlayerSymbol(Symbol.EMPTY);
	}

	@Test
	public void setCurrentPlayerSymbolInvalidTest() {
		// check for symbol null exception
		assertThrows(IllegalArgumentException.class, () -> {
			simulator0.setCurrentPlayerSymbol(null);
		});
	}

	@Test
	public void getCurrentPlayerSymbolValidTest() {
		setCurrentPlayerSymbolValidTest();

		assertEquals(Symbol.CROSS, simulator0.getCurrentPlayerSymbol());
		assertEquals(Symbol.CIRCLE, simulator9.getCurrentPlayerSymbol());
		assertEquals(Symbol.EMPTY, simulator10.getCurrentPlayerSymbol());
	}

	@Test
	public void setMarkAtValidTest() {
		// test setMarkAt every index on every board with empty at beginning
		BoardInterface[] boards = util.createBoardInterface(Symbol.EMPTY, 9);
		simulator9.setBoards(boards);

		for (int board = 0; board < 9; board++) {
			for (int mark = 0; mark < 9; mark++) {
				simulator9.setIndexNextBoard(-1);
				simulator9.setCurrentPlayerSymbol(Symbol.CROSS);
				// check that simulator is not null
				assertNotNull(simulator9);
				// check that board is not null
				assertNotNull(board);
				// check that mark is not null
				assertNotNull(mark);
				// should be valid since setmark within valid indexes with nextBoardIndex -1 may
				// return true
				assertTrue(simulator9.setMarkAt(simulator9.getCurrentPlayerSymbol(), board, mark));
			}
		}

		simulator9.setBoards(util.createBoardInterface(Symbol.EMPTY, 9));
		for (int board = 0; board < 9; board++) {
			for (int mark = 0; mark < 9; mark++) {
				simulator9.setIndexNextBoard(-1);
				simulator9.setCurrentPlayerSymbol(Symbol.CIRCLE);
				assertTrue(simulator9.setMarkAt(simulator9.getCurrentPlayerSymbol(), board, mark));
			}
		}

		simulator9.setBoards(util.createBoardInterface(Symbol.EMPTY, 9));
		for (int board = 0; board < 9; board++) {
			for (int mark = 0; mark < 9; mark++) {
				simulator9.setIndexNextBoard(-1);
				simulator9.setCurrentPlayerSymbol(Symbol.EMPTY);
				assertTrue(simulator9.setMarkAt(simulator9.getCurrentPlayerSymbol(), board, mark));
			}
		}
	}

	@Test
	public void setMarkAtInvalidTest() {
		// test setMarkAt every index on every board with other symbol at beginning
		simulator9.setBoards(util.createBoardInterface(Symbol.CIRCLE, 9));
		for (int board = 0; board < 9; board++) {
			for (int mark = 0; mark < 9; mark++) {
				simulator9.setIndexNextBoard(-1);
				simulator9.setCurrentPlayerSymbol(Symbol.CROSS);
				assertFalse(simulator9.setMarkAt(simulator9.getCurrentPlayerSymbol(), board, mark));
			}
		}

		simulator9.setBoards(util.createBoardInterface(Symbol.CROSS, 9));
		for (int board = 0; board < 9; board++) {
			for (int mark = 0; mark < 9; mark++) {
				simulator9.setIndexNextBoard(-1);
				simulator9.setCurrentPlayerSymbol(Symbol.CIRCLE);
				assertFalse(simulator9.setMarkAt(simulator9.getCurrentPlayerSymbol(), board, mark));
			}
		}

		simulator9.setBoards(util.createBoardInterface(Symbol.CROSS, 9));
		for (int board = 0; board < 9; board++) {
			for (int mark = 0; mark < 9; mark++) {
				simulator9.setIndexNextBoard(-1);
				simulator9.setCurrentPlayerSymbol(Symbol.EMPTY);
				assertFalse(simulator9.setMarkAt(simulator9.getCurrentPlayerSymbol(), board, mark));
			}
		}

		simulator9.setBoards(util.createBoardInterface(Symbol.CIRCLE, 9));
		for (int board = 0; board < 9; board++) {
			for (int mark = 0; mark < 9; mark++) {
				simulator9.setIndexNextBoard(-1);
				simulator9.setCurrentPlayerSymbol(Symbol.CROSS);
				assertFalse(simulator9.setMarkAt(simulator9.getCurrentPlayerSymbol(), board, mark));
			}
		}

		assertThrows(IllegalArgumentException.class, () -> {
			simulator9.setMarkAt(null, 0, 0);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			simulator9.setMarkAt(Symbol.CIRCLE, -1, 0);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			simulator9.setMarkAt(Symbol.CIRCLE, 0, -1);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			simulator9.setMarkAt(Symbol.CIRCLE, -1, -1);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			simulator9.setMarkAt(Symbol.CIRCLE, 9, 8);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			simulator9.setMarkAt(Symbol.CIRCLE, 8, 9);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			simulator9.setMarkAt(Symbol.CIRCLE, 9, 9);
		});
	}

	@Test
	public void setIndexNextBoardValidTest() {
		simulator9.setIndexNextBoard(-1);
		simulator9.setIndexNextBoard(0);
		simulator9.setIndexNextBoard(8);
	}

	@Test
	public void setIndexNextBoardInvalidTest() {
		assertThrows(IllegalArgumentException.class, () -> {
			simulator9.setIndexNextBoard(-2);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			simulator9.setIndexNextBoard(9);
		});
	}

	@Test
	public void getIndexNextBoardValidTest() {
		// check on empty board
		simulator9.setBoards(util.createBoardInterface(Symbol.EMPTY, 9));
		simulator9.setIndexNextBoard(-1);
		assertEquals(-1, simulator9.getIndexNextBoard());

		// check on closed board
		simulator9.setBoards(util.createBoardInterface(Symbol.CROSS, 9));
		simulator9.setIndexNextBoard(2);
		assertEquals(-1, simulator9.getIndexNextBoard());

	}

	@Test
	public void isGameOverValidTest() {
		// check if all boards are closed
		simulator9.setBoards(util.createBoardInterface(Symbol.EMPTY, 9));
		assertFalse(simulator9.isGameOver());

		simulator9.setBoards(util.createBoardInterface(Symbol.CROSS, 9));
		assertTrue(simulator9.isGameOver());

		simulator9.setBoards(util.createBoardInterface(Symbol.CIRCLE, 9));
		assertTrue(simulator9.isGameOver());
	}

	@Test
	public void isMovePossibleSimpleValidTest() {
		// test isMovePossible(int boardIndex) function
		simulator9.setIndexNextBoard(0);
		simulator9.setBoards(util.createBoardInterface(Symbol.CROSS, 9));
		assertFalse(simulator9.isMovePossible(0));

		simulator9.setIndexNextBoard(8);
		// not possible since winner exists
		simulator9.getBoards()[8].getMarks()[8].setSymbol(Symbol.EMPTY);
		assertFalse(simulator9.isMovePossible(8));

		simulator9.setIndexNextBoard(8);
		// possible since no winner exists and empty marks
		simulator9.getBoards()[8].getMarks()[0].setSymbol(Symbol.EMPTY);
		simulator9.getBoards()[8].getMarks()[2].setSymbol(Symbol.EMPTY);
		simulator9.getBoards()[8].getMarks()[4].setSymbol(Symbol.EMPTY);
		simulator9.getBoards()[8].getMarks()[5].setSymbol(Symbol.EMPTY);
		simulator9.getBoards()[8].getMarks()[8].setSymbol(Symbol.EMPTY);
		assertTrue(simulator9.isMovePossible(8));
	}

	@Test
	public void isMovePossibleSimpleInvalidTest() {
		// test isMovePossible(int boardIndex) function
		simulator9.setBoards(util.createBoardInterface(Symbol.CROSS, 9));

		// check lower bound
		assertThrows(IllegalArgumentException.class, () -> {
			simulator9.isMovePossible(-1);
		});

		// check upper bound
		assertThrows(IllegalArgumentException.class, () -> {
			simulator9.isMovePossible(9);
		});
	}

	@Test
	public void isMovePossibleAdvancedValidTest() {
		// test isMovePossible(int boardIndex, int markIndex) function
		simulator9.setBoards(util.createBoardInterface(Symbol.CROSS, 9));
		assertFalse(simulator9.isMovePossible(0, 0));

		// not possible since winner exists
		simulator9.getBoards()[8].getMarks()[8].setSymbol(Symbol.EMPTY);
		assertFalse(simulator9.isMovePossible(8, 8));

		// possible since no winner exists and empty marks
		simulator9.getBoards()[8].getMarks()[0].setSymbol(Symbol.EMPTY);
		simulator9.getBoards()[8].getMarks()[2].setSymbol(Symbol.EMPTY);
		simulator9.getBoards()[8].getMarks()[4].setSymbol(Symbol.EMPTY);
		simulator9.getBoards()[8].getMarks()[5].setSymbol(Symbol.EMPTY);
		simulator9.getBoards()[8].getMarks()[8].setSymbol(Symbol.EMPTY);
		assertTrue(simulator9.isMovePossible(8, 8));

	}

	@Test
	public void isMovePossibleAdvancedInvalidTest() {
		// test isMovePossible(int boardIndex) function
		simulator9.setBoards(util.createBoardInterface(Symbol.CROSS, 9));

		// check lower bound
		assertThrows(IllegalArgumentException.class, () -> {
			simulator9.isMovePossible(0, -1);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			simulator9.isMovePossible(-1, 0);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			simulator9.isMovePossible(-1, -1);
		});

		// check upper bound
		assertThrows(IllegalArgumentException.class, () -> {
			simulator9.isMovePossible(8, 9);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			simulator9.isMovePossible(9, 8);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			simulator9.isMovePossible(9, 9);
		});
	}

	@Test
	public void getWinnerValidTest() {
		simulator9.setBoards(util.createBoardInterface(Symbol.EMPTY, 9));
		assertEquals(Symbol.EMPTY, simulator9.getWinner());

		simulator9.setBoards(util.createBoardInterface(Symbol.CROSS, 9));
		assertEquals(Symbol.CROSS, simulator9.getWinner());

		simulator9.setBoards(util.createBoardInterface(Symbol.CIRCLE, 9));
		assertEquals(Symbol.CIRCLE, simulator9.getWinner());
	}
}

// TODO:
// check in isMovePossibleTests for boardIndex not equal to getIndexNextBoard
