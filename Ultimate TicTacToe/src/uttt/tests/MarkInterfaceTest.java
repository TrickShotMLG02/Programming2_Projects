package uttt.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uttt.UTTTFactory;
import uttt.game.MarkInterface;
import uttt.utils.Symbol;

public class MarkInterfaceTest {

	MarkInterface markCross;
	MarkInterface markCircle;
	MarkInterface markEmpty;
	MarkInterface markInvalid;

	@Before
	public void setUp() throws Exception {
		// create marks with valid symbols and positions
		markCross = UTTTFactory.createMark(Symbol.CROSS, 0);
		markCircle = UTTTFactory.createMark(Symbol.CIRCLE, 1);
		markEmpty = UTTTFactory.createMark(Symbol.EMPTY, 4);
		markInvalid = UTTTFactory.createMark(Symbol.EMPTY, 8);
	}

	@Test
	public void invalidIndexOnCreationTest() {
		// check for index below bounds
		assertThrows(IllegalArgumentException.class, () -> {
			markInvalid = UTTTFactory.createMark(Symbol.EMPTY, -1);
			markInvalid.getPosition();
			markInvalid.getSymbol();
		});

		// check for index above bounds
		assertThrows(IllegalArgumentException.class, () -> {
			markInvalid = UTTTFactory.createMark(Symbol.EMPTY, 9);
			markInvalid.getPosition();
			markInvalid.getSymbol();
		});
	}

	@Test
	public void invalidSetSymbolTest() {
		// check if markInvalid throws an exception on setSymbol with null
		assertThrows(IllegalArgumentException.class, () -> {
			markInvalid.setSymbol(null);
		});
	}

	@Test
	public void validSetSymbolTest() {
		markCross.setSymbol(Symbol.CROSS);
		markCircle.setSymbol(Symbol.CIRCLE);
		markEmpty.setSymbol(Symbol.EMPTY);
	}

	@Test
	public void getPositionsTest() {
		// Check if positions are equal to the ones set in setUp()
		assertEquals(0, markCross.getPosition());
		assertEquals(1, markCircle.getPosition());
		assertEquals(4, markEmpty.getPosition());
		assertEquals(8, markInvalid.getPosition());
	}

	@Test
	public void getSymbolsToStringTest() {
		// check if symbol names are correct
		assertEquals("X", markCross.getSymbol().toString());
		assertEquals("O", markCircle.getSymbol().toString());
		assertEquals(" ", markEmpty.getSymbol().toString());
		assertEquals(" ", markInvalid.getSymbol().toString());
	}

	@Test
	public void getSymbolsTest() {
		// check if symbol names are correct
		assertEquals(Symbol.CROSS, markCross.getSymbol());
		assertEquals(Symbol.CIRCLE, markCircle.getSymbol());
		assertEquals(Symbol.EMPTY, markEmpty.getSymbol());
		assertEquals(Symbol.EMPTY, markInvalid.getSymbol());
	}

}