package uttt.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

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
	public void getMarksInvalidTest() {
		// boards are initialized with empty marks at beginning thus compare symbol and
		// positions with empty mark array
		for (int i = 0; i < 9; i++) {
			assertEquals(marksAmount9Empty[i].getSymbol(), boardAmount0.getMarks()[i].getSymbol());
			assertEquals(marksAmount9Empty[i].getPosition(), boardAmount0.getMarks()[i].getPosition());
		}

		for (int i = 0; i < 9; i++) {
			assertEquals(marksAmount9Empty[i].getSymbol(), boardAmount10.getMarks()[i].getSymbol());
			assertEquals(marksAmount9Empty[i].getPosition(), boardAmount10.getMarks()[i].getPosition());
		}
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

}
