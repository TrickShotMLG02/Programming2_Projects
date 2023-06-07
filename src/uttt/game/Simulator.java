package uttt.game;

import uttt.utils.Move;
import uttt.utils.Symbol;

public class Simulator implements SimulatorInterface {

    private Symbol currentPlayerSymbol;
    private BoardInterface[] boards;
    private int nextBoardIndex;

    public Simulator() {

        // create boards
        boards = new BoardInterface[9];
        for (int i = 0; i < 9; i++) {
            boards[i] = new Board();
        }

        // set current player symbol to EMPTY?
        currentPlayerSymbol = Symbol.EMPTY;

        // set next board index to -1 since first board can be chosen
        nextBoardIndex = -1;
    }

    @Override
    public void run(PlayerInterface playerOne, PlayerInterface playerTwo, UserInterface ui)
            throws IllegalArgumentException {

        // set currentPlayerSymbol to playerOne's symbol
        currentPlayerSymbol = playerOne.getSymbol();

        // define currentPlayer
        PlayerInterface currentPlayer = playerOne;

        // run game while game is not over
        while (!isGameOver() && getWinner() == Symbol.EMPTY) {
            // get current player move
            Move move = currentPlayer.getPlayerMove(this, ui);

            // check if move is possible
            if (isMovePossible(move.getBoardIndex(), move.getMarkIndex())) {
                // set current player mark at move
                setMarkAt(currentPlayerSymbol, move.getBoardIndex(), move.getMarkIndex());

                // flip current player symbol
                currentPlayerSymbol = currentPlayerSymbol.flip();

                // switch players
                currentPlayer = flipPlayers(playerOne, playerTwo, currentPlayer);

                // update screen
                ui.updateScreen(this);
            } else {
                System.out.println("Move not possible");
            }

        }

        // print winner
        ui.showGameOverScreen(getWinner());

    }

    private PlayerInterface flipPlayers(PlayerInterface playerOne, PlayerInterface playerTwo, PlayerInterface current) {
        // return playerTwo if current player is playerOne
        return current == playerOne ? playerTwo : playerOne;
    }

    @Override
    public BoardInterface[] getBoards() {
        return boards;
    }

    @Override
    public void setBoards(BoardInterface[] boards) throws IllegalArgumentException {
        // check if board array is null
        if (boards == null) {
            throw new IllegalArgumentException("Boards array is null");
        }

        // check if boards has length 9
        if (boards.length != 9) {
            throw new IllegalArgumentException("There must be 9 marks");
        }

        this.boards = boards;
    }

    @Override
    public Symbol getCurrentPlayerSymbol() {
        return currentPlayerSymbol;
    }

    @Override
    public void setCurrentPlayerSymbol(Symbol symbol) throws IllegalArgumentException {
        // check if symbol is null -> set it to Empty, otherwise apply symbol
        if (symbol == null)
            currentPlayerSymbol = Symbol.EMPTY;
        else
            currentPlayerSymbol = symbol;
    }

    @Override
    public boolean setMarkAt(Symbol symbol, int boardIndex, int markIndex) throws IllegalArgumentException {
        try {
            if ((getIndexNextBoard() == -1 || getIndexNextBoard() == boardIndex) && symbol == currentPlayerSymbol) {
                boards[boardIndex].setMarkAt(symbol, markIndex);
                setIndexNextBoard(boardIndex);
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid mark");
        }
    }

    @Override
    public int getIndexNextBoard() {

        // check if boardIndex is -1
        if (nextBoardIndex == -1)
            return nextBoardIndex;

        // check if current board is closed
        if (boards[nextBoardIndex].isClosed()) {
            // player can freely choose next index
            return -1;
        } else {
            // otherwise return currentBoardIndex
            return nextBoardIndex;
        }

    }

    @Override
    public void setIndexNextBoard(int index) throws IllegalArgumentException {
        // check if index between -1 and 8
        if (index < -1 || index > 8) {
            throw new IllegalArgumentException("Index out of bounds");
        }

        nextBoardIndex = index;
    }

    @Override
    public boolean isGameOver() {

        for (int i = 0; i < boards.length; i++) {
            if (!boards[i].isClosed()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isMovePossible(int boardIndex) throws IllegalArgumentException {
        return !boards[boardIndex].isClosed();
    }

    @Override
    public boolean isMovePossible(int boardIndex, int markIndex) throws IllegalArgumentException {
        if (getIndexNextBoard() == -1 || boardIndex == getIndexNextBoard()) {
            return boards[boardIndex].isMovePossible(markIndex);
        } else {
            return false;
        }
    }

    @Override
    public Symbol getWinner() {

        // create new board and store winners of all boards in it
        BoardInterface evalBoard = new Board();

        // iterate over all boards
        for (int i = 0; i < boards.length; i++) {
            // get winner of each board
            Symbol winner = boards[i].getWinner();
            // set winner of each board in evalBoard
            evalBoard.setMarkAt(winner, i);
        }

        // check if evalBoard has a winner
        Symbol evalWinner = evalBoard.getWinner();
        return evalWinner;

    }

}
