package uttt.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uttt.UTTTFactory;
import uttt.game.BoardInterface;
import uttt.game.MarkInterface;

public class BoardInterfaceTest {

	BoardInterface board;
	BoardInterface boardAmount9;
	BoardInterface boardAmount0;
	BoardInterface boardAmount_1;
	BoardInterface boardAmount10;

	MarkInterface[] marks;

	@Before
	public void setUp() throws Exception {
		// create boardInterface
		board = UTTTFactory.createBoard();
		boardAmount9 = UTTTFactory.createBoard();
		boardAmount0 = UTTTFactory.createBoard();
		boardAmount_1 = UTTTFactory.createBoard();
		boardAmount10 = UTTTFactory.createBoard();

		// get marks from board which should be empty by default
		marks = board.getMarks();

		// set marks on board with 9 marks array

		MarkInterface[] marksAmount9 = new MarkInterface[9];
		for (int i = 0; i < 9; i++) {
			marksAmount9[i] = UTTTFactory.createMark(uttt.utils.Symbol.CROSS, i);
		}
		boardAmount9.setMarks(marksAmount9);

		// set marks on board with 0 marks array
		MarkInterface[] marksAmount0 = new MarkInterface[0];
		for (int i = 0; i < 0; i++) {
			marksAmount0[i] = UTTTFactory.createMark(uttt.utils.Symbol.CROSS, i);
		}

		// set marks on board with -1 marks array
		MarkInterface[] marksAmount_1 = new MarkInterface[-1];
		for (int i = 0; i < -1; i++) {
			marksAmount_1[i] = UTTTFactory.createMark(uttt.utils.Symbol.CROSS, i);
		}

		// set marks on board with 10 marks array
		MarkInterface[] marksAmount10 = new MarkInterface[10];
		for (int i = 0; i < 10; i++) {
			marksAmount10[i] = UTTTFactory.createMark(uttt.utils.Symbol.CROSS, i);
		}
	}

	@Test
	public void simpleSetPieceTest() {

		// test if objects are not null
		assertNotNull(board);
		assertNotNull(marks);
		assertNotNull(boardAmount9);
		assertNotNull(boardAmount0);
		assertNotNull(boardAmount_1);
		assertNotNull(boardAmount10);

		// check if marks are correctly set
		// TODO: all other tests

	}

}
