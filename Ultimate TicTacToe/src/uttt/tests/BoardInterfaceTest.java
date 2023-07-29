package uttt.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uttt.UTTTFactory;
import uttt.game.BoardInterface;
import uttt.game.MarkInterface;
import uttt.utils.Symbol;

public class BoardInterfaceTest {

	BoardInterface boardAmount0;
	BoardInterface boardAmount9;
	BoardInterface boardAmount9Empty;
	BoardInterface boardAmount10;

	MarkInterface[] marksAmount0;
	MarkInterface[] marksAmount9;
	MarkInterface[] marksAmount9Empty;
	MarkInterface[] marksAmount10;

	MarkInterface[] marksAmount9Win;
	MarkInterface[] marksAmount9Tie;

	@Before
	public void setUp() throws Exception {
		// create boardInterface
		boardAmount0 = UTTTFactory.createBoard();
		boardAmount9 = UTTTFactory.createBoard();
		boardAmount9Empty = UTTTFactory.createBoard();
		boardAmount10 = UTTTFactory.createBoard();

		// create markInterfaces with random symbols and different size
		marksAmount0 = util.createMarkInterface(0);
		marksAmount9 = util.createMarkInterface(9);
		marksAmount9Empty = util.createMarkInterface(Symbol.EMPTY, 9);
		marksAmount10 = util.createMarkInterface(10);

		// create winner marks
		marksAmount9Win = util.createMarkInterfaceWin();

		// create tie marks
		marksAmount9Tie = util.createMarkInterfaceTie();
	}

	@Test
	public void boardsNotNullTest() {
		// test if board objects are not null
		assertNotNull(boardAmount0);
		assertNotNull(boardAmount9);
		assertNotNull(boardAmount9Empty);
		assertNotNull(boardAmount10);
	}

	@Test
	public void marksNotNullTest() {
		// test if mark arrays are not null
		assertNotNull(marksAmount0);
		assertNotNull(marksAmount9);
		assertNotNull(marksAmount9Empty);
		assertNotNull(marksAmount10);
	}

	@Test
	public void setMarksValidTest() {
		// Test for valid mark arrays
		boardAmount9.setMarks(marksAmount9);
		boardAmount9Empty.setMarks(marksAmount9Empty);
	}

	@Test
	public void setMarksInvalidAmountTest() {
		// test for invalid markArray size exception
		assertThrows(IllegalArgumentException.class, () -> {
			boardAmount0.setMarks(marksAmount0);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			boardAmount10.setMarks(marksAmount10);
		});
	}

	@Test
	public void getMarksValidTest() {

		// set Marks valid
		setMarksValidTest();

		assertArrayEquals(marksAmount9, boardAmount9.getMarks());
		assertArrayEquals(marksAmount9Empty, boardAmount9Empty.getMarks());
	}

	@Test
	public void setMarkAtValidTest() {

		// set Marks to empty
		boardAmount9.setMarks(marksAmount9Empty);

		// check setting mark at empty mark
		assertEquals(true, boardAmount9.setMarkAt(Symbol.CROSS, 0));
		assertEquals(true, boardAmount9.setMarkAt(Symbol.CIRCLE, 1));
		assertEquals(true, boardAmount9.setMarkAt(Symbol.CROSS, 4));
		assertEquals(true, boardAmount9.setMarkAt(Symbol.EMPTY, 8));

		// check for writing symbol into other symbol not empty
		assertEquals(false, boardAmount9.setMarkAt(Symbol.CROSS, 0));
		assertEquals(false, boardAmount9.setMarkAt(Symbol.CIRCLE, 0));

		// check for writing symbol into other symbol not empty
		assertEquals(false, boardAmount9.setMarkAt(Symbol.CROSS, 1));
		assertEquals(false, boardAmount9.setMarkAt(Symbol.CIRCLE, 1));
	}

	@Test
	public void setMarkAtInvalidTest() {
		// check for symbol null exception
		assertThrows(IllegalArgumentException.class, () -> {

			boardAmount0.setMarkAt(null, 1);

		});

		// check for index above bounds
		assertThrows(IllegalArgumentException.class, () -> {

			boardAmount0.setMarkAt(Symbol.CROSS, 9);

		});

		// check for index below bounds
		assertThrows(IllegalArgumentException.class, () -> {

			boardAmount0.setMarkAt(Symbol.CROSS, -1);

		});
	}

	@Test
	public void isClosedValidTest() {
		// check if empty board isClosed
		boardAmount9.setMarks(marksAmount9Empty);
		assertEquals(false, boardAmount9.isClosed());

		// check for closed on tie
		boardAmount9.setMarks(marksAmount9Tie);
		assertEquals(true, boardAmount9.isClosed());

		// check for closed on win
		boardAmount9.setMarks(marksAmount9Win);
		assertEquals(true, boardAmount9.isClosed());
	}

	@Test
	public void isMovePossibleValidTest() {
		// check if empty board movePossible
		boardAmount9.setMarks(marksAmount9Empty);
		assertEquals(true, boardAmount9.isMovePossible(0));

		// check for movePossible on tie
		boardAmount9.setMarks(marksAmount9Tie);
		assertEquals(false, boardAmount9.isMovePossible(4));

		// check for movePossible on win
		boardAmount9.setMarks(marksAmount9Win);
		assertEquals(false, boardAmount9.isMovePossible(6));
	}

	@Test
	public void isMovePossibleInvalidTest() {
		assertThrows(IllegalArgumentException.class, () -> {
			boardAmount9.setMarks(marksAmount9Empty);
			// check for isMovePossible below bounds
			assertEquals(true, boardAmount9.isMovePossible(-1));
		});

		assertThrows(IllegalArgumentException.class, () -> {
			boardAmount9.setMarks(marksAmount9Empty);
			// check for isMovePossible above bounds
			assertEquals(true, boardAmount9.isMovePossible(9));
		});
	}

	@Test
	public void getWinnerValidTest() {
		// check if empty board getWinner
		boardAmount9.setMarks(marksAmount9Empty);
		assertEquals(Symbol.EMPTY, boardAmount9.getWinner());

		// check for getWinner on tie
		boardAmount9.setMarks(marksAmount9Tie);
		assertEquals(Symbol.EMPTY, boardAmount9.getWinner());

		// check for getWinner on win CROSS
		marksAmount9Win = util.createMarkInterfaceWin(Symbol.CROSS);
		boardAmount9.setMarks(marksAmount9Win);
		assertEquals(Symbol.CROSS, boardAmount9.getWinner());

		// check for getWinner on win CIRCLE
		marksAmount9Win = util.createMarkInterfaceWin(Symbol.CIRCLE);
		boardAmount9.setMarks(marksAmount9Win);
		assertEquals(Symbol.CIRCLE, boardAmount9.getWinner());

	}

}
