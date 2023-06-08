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
		markCross = UTTTFactory.createMark(Symbol.CROSS, 0);
		markCircle = UTTTFactory.createMark(Symbol.CIRCLE, 1);
		markInvalid = UTTTFactory.createMark(Symbol.EMPTY, 0);
		markEmpty = UTTTFactory.createMark(Symbol.EMPTY, 0);
	}

	@Test
	public void simpleSetPieceTest() {

		assertThrows(Exception.class, () -> {
			markInvalid = UTTTFactory.createMark(Symbol.EMPTY, -1);
		});

		assertThrows(Exception.class, () -> {
			markInvalid = UTTTFactory.createMark(Symbol.EMPTY, 10);
		});

		// check if marks are not null
		assertNotNull(markCross);
		assertNotNull(markCircle);

		// check if markInvalid throws an exception on setSymbol with null
		assertThrows(Exception.class, () -> {
			markInvalid.setSymbol(null);
		});

		// Check if positions are equal to the ones set in setUp()
		assertEquals(0, markCross.getPosition());
		assertEquals(1, markCircle.getPosition());
		assertEquals(0, markEmpty.getPosition());
		assertEquals(0, markInvalid.getPosition());

		// check if symbol names are correct
		assertEquals("X", markCross.getSymbol().toString());
		assertEquals("O", markCircle.getSymbol().toString());
		assertEquals(" ", markEmpty.getSymbol().toString());
		assertEquals(" ", markInvalid.getSymbol().toString());
	}

}
