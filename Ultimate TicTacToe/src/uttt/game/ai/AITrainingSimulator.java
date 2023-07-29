package uttt.game.ai;

import java.util.ArrayList;
import java.util.List;

import uttt.game.Board;
import uttt.game.BoardInterface;
import uttt.game.PlayerInterface;
import uttt.game.Simulator;
import uttt.game.SimulatorInterface;
import uttt.game.UserInterface;
import uttt.utils.Move;
import uttt.utils.Symbol;

public class AITrainingSimulator implements SimulatorInterface, Cloneable {

    private Symbol currentPlayerSymbol;
    private BoardInterface[] boards;
    private int nextBoardIndex;

    private int iteration = 0;
    private int saveAfterXIterations = 5;

    public AITrainingSimulator() {

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
        setCurrentPlayerSymbol(playerOne.getSymbol());

        // define currentPlayer
        PlayerInterface currentPlayer = playerOne;
        // run game while game is not over
        while (!isGameOver() && getWinner() == Symbol.EMPTY) {

            // increase current iteration
            iteration = iteration + 1;

            System.out.println("Current iteration: " + iteration);

            // create simulator object to hold current state as clone
            SimulatorInterface sim = null;

            // Clone current game state for ai learning
            try {
                sim = (SimulatorInterface) this.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            // get current player move
            // if currentPLayer is ai, predict next move
            Move move = currentPlayer.getPlayerMove(this, ui);

            // check if move is possible
            if (isMovePossible(move.getBoardIndex(), move.getMarkIndex())) {

                /*
                 * Train AI model on valid move based on current game state
                 * Maybe save GameState to file for later training purposes
                 */

                // train model on grabbed result if simulator is not null
                if (sim != null && currentPlayer.getClass() != AIPlayer.class) {
                    if (NeuralNetwork.verbose)
                        System.out.println("Training model on move to current state");

                    // create matrix containing expected values by comparing sim from state before
                    // move with sim from state after move
                    Prediction expected = new Prediction(this, Matrix.calculateDifferences(sim, this));

                    // train model on sim state before move with the expected result of after the
                    // move
                    AIManager.model.trainModel(sim, expected);

                }

                // set current player mark at move
                setMarkAt(currentPlayerSymbol, move.getBoardIndex(), move.getMarkIndex());

                setIndexNextBoard(move.getMarkIndex());

                // flip current player symbol
                currentPlayerSymbol = currentPlayerSymbol.flip();

                // switch players
                currentPlayer = flipPlayers(playerOne, playerTwo, currentPlayer);

                // update screen
                ui.updateScreen(this);
            } else {
                // move not possible, if from ai, choose random valid position and train ai on
                // that situation
                if (currentPlayer.getClass() == AIPlayer.class) {
                    // generate random valid position

                    /*
                     * TODO:
                     * Code here
                     * to generate a random valid Move
                     */

                    System.out.println("Generating  random move, since ai failed\n");

                    // increment failed predictions of ai player
                    // ((AIPlayer) playerOne).failedPredictions += 1;
                    System.out.println(move.toString());
                    move = Util.generateRandomValidMove(this);

                    /*
                     * Do all other game logic with valid move
                     */
                    if (isMovePossible(move.getBoardIndex(), move.getMarkIndex())) {

                        setMarkAt(currentPlayerSymbol, move.getBoardIndex(), move.getMarkIndex());

                        setIndexNextBoard(move.getMarkIndex());

                        // flip current player symbol
                        currentPlayerSymbol = currentPlayerSymbol.flip();

                        // switch players
                        currentPlayer = flipPlayers(playerOne, playerTwo, currentPlayer);

                        // train ai on random move
                        // TODO: Punish ai since it chose invalid index

                        // update screen
                        ui.updateScreen(this);
                    }
                }
            }

            // save current state to file if iteration % saveAfterXIterations ==
            // saveAfterXIterations - 1
            if (iteration % saveAfterXIterations == saveAfterXIterations - 1) {
                // save state to file for later training
                // TODO: implement saving
            }

        }
        // print winner
        // ui.showGameOverScreen(getWinner());
        System.out.println("\n\nWinner is " + getWinner().toString() + "\n\n");

        if (playerOne.getClass() == AIPlayer.class) {
            // System.out.println("Player One AI failed " + ((AIPlayer)
            // playerOne).failedPredictions + " times");
        }

        if (playerTwo.getClass() == AIPlayer.class) {
            // System.out.println("PLayer Two AI failed " + ((AIPlayer)
            // playerTwo).failedPredictions + " times");
        }

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
        }

        if (AIManager.updateModel) {
            NeuralNetwork.UpdateModel(AIManager.model);
        }
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
            throw new IllegalArgumentException("There must be 9 boards");
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
            throw new IllegalArgumentException("symbol is null");
        else
            currentPlayerSymbol = symbol;
    }

    @Override
    public boolean setMarkAt(Symbol symbol, int boardIndex, int markIndex) throws IllegalArgumentException {

        if (symbol == null)
            throw new IllegalArgumentException("Symbol is null");

        if (boardIndex < 0 || boardIndex > 8 || markIndex < 0 || markIndex > 8)
            throw new IllegalArgumentException("Index out of bounds");

        if ((getIndexNextBoard() == -1 || getIndexNextBoard() == boardIndex) && symbol == currentPlayerSymbol) {
            if (boards[boardIndex].setMarkAt(symbol, markIndex)) {
                setIndexNextBoard(markIndex);
                return true;
            }
            return false;

        } else {
            return false;
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

        if (getWinner() != Symbol.EMPTY)
            return true;

        for (int i = 0; i < boards.length; i++) {
            if (!boards[i].isClosed()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isMovePossible(int boardIndex) throws IllegalArgumentException {
        if (boardIndex < 0 || boardIndex > 8)
            throw new IllegalArgumentException("Index out of bounds");

        return !boards[boardIndex].isClosed() && !isGameOver()
                && (getIndexNextBoard() == -1 || getIndexNextBoard() == boardIndex);
    }

    @Override
    public boolean isMovePossible(int boardIndex, int markIndex) throws IllegalArgumentException {

        if (boardIndex < 0 || boardIndex > 8 || markIndex < 0 || markIndex > 8)
            throw new IllegalArgumentException("index out of bounds");

        // check if the nextBoardIndex is -1
        if (!isGameOver() && getIndexNextBoard() == -1) {
            // check if move is possible on boardIndex and markIndex
            return boards[boardIndex].isMovePossible(markIndex);
        }

        // check if board at nextBoardIndex is not closed and if nextBoardIndex is equal
        // to boardIndex to prevent placing outside current board
        if (!isGameOver() && !boards[getIndexNextBoard()].isClosed() && getIndexNextBoard() == boardIndex) {
            // then check if move is possible there and return result
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

    /**
     * Function used for ai training to simulate a specific move in the simulator
     * 
     * @param state the simulator state where the move should be simulated in
     * @param move  move which should be simulated
     */
    public static void simulateMove(SimulatorInterface state, Move move) {

        // get indexes from move
        int boardIndex = move.getBoardIndex();
        int markIndex = move.getMarkIndex();

        // set mark at indexes to simulate move
        state.getBoards()[boardIndex].getMarks()[markIndex].setSymbol(state.getCurrentPlayerSymbol());

        // System.out.println("Simulated place of " +
        // state.getCurrentPlayerSymbol().toString() + " at position " + boardIndex +
        // "|" + markIndex);

        state.setCurrentPlayerSymbol(state.getCurrentPlayerSymbol().flip());

    }

    public static boolean isWinPossible(SimulatorInterface state) {
        // check if there are 2 equal symbols and one empty symbol in a row/col/diagonal
        // where the 2 symbols are the symbolToTestWin
        // if so, return true else false

        // symbol to test for
        Symbol symbolToTestWin = state.getCurrentPlayerSymbol();

        // iterate over all boards
        for (int board = 0; board < 9; board++) {

            // check if board is valid
            if (board == state.getIndexNextBoard() || state.getIndexNextBoard() == -1) {

                boolean winPossible = false;

                // check all winning patterns
                winPossible = winPossible || Util.contains2SymbolsAndEmpty(state,
                        symbolToTestWin, board, 0, 1, 2);
                winPossible = winPossible || Util.contains2SymbolsAndEmpty(state,
                        symbolToTestWin, board, 3, 4, 5);
                winPossible = winPossible || Util.contains2SymbolsAndEmpty(state,
                        symbolToTestWin, board, 6, 7, 8);
                winPossible = winPossible || Util.contains2SymbolsAndEmpty(state,
                        symbolToTestWin, board, 0, 3, 6);
                winPossible = winPossible || Util.contains2SymbolsAndEmpty(state,
                        symbolToTestWin, board, 1, 4, 7);
                winPossible = winPossible || Util.contains2SymbolsAndEmpty(state,
                        symbolToTestWin, board, 2, 5, 8);
                winPossible = winPossible || Util.contains2SymbolsAndEmpty(state,
                        symbolToTestWin, board, 0, 4, 8);
                winPossible = winPossible || Util.contains2SymbolsAndEmpty(state,
                        symbolToTestWin, board, 6, 4, 2);

                // return true as soon as a win is possible
                if (winPossible) {
                    return winPossible;
                }
            }
        }

        // return false if no win is possible
        return false;

    }

    public static SimulatorInterface copySimulator(SimulatorInterface sim) {
        // create new Simulator
        SimulatorInterface simCpy = new Simulator();

        // copy values

        BoardInterface[] boards = new BoardInterface[9];

        for (int i = 0; i < 9; i++) {
            boards[i] = new Board();
            for (int j = 0; j < 9; j++) {
                Symbol tmp = sim.getBoards()[i].getMarks()[j].getSymbol();
                boards[i].getMarks()[j].setSymbol(tmp);
            }
        }

        simCpy.setBoards(boards);
        simCpy.setCurrentPlayerSymbol(sim.getCurrentPlayerSymbol());
        simCpy.setIndexNextBoard(sim.getIndexNextBoard());

        // return copy
        return simCpy;
    }

    public static List<Move> getPossibleMoves(SimulatorInterface sim) {

        // create list to store possible moves in
        List<Move> possibleMoves = new ArrayList<Move>();

        // grab index which specifies next board
        int nextBoard = sim.getIndexNextBoard();

        for (int board = 0; board < 9; board++) {
            for (int mark = 0; mark < 9; mark++) {

                // only get moves from board which can be played
                if (nextBoard == -1 || board == nextBoard) {
                    // test if move is possible
                    if (sim.isMovePossible(board, mark)) {
                        // add new move with indexes to list
                        possibleMoves.add(new Move(board, mark));
                    }
                }

            }
        }

        return possibleMoves;

    }
}
