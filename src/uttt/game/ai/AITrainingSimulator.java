package uttt.game.ai;

import uttt.game.Board;
import uttt.game.BoardInterface;
import uttt.game.PlayerInterface;
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
                setIndexNextBoard(boardIndex);
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
        return !boards[boardIndex].isClosed();
    }

    @Override
    public boolean isMovePossible(int boardIndex, int markIndex) throws IllegalArgumentException {

        if (boardIndex < 0 || boardIndex > 8 || markIndex < 0 || markIndex > 8)
            throw new IllegalArgumentException("index out of bounds");

        // check if the nextBoardIndex is -1
        if (getIndexNextBoard() == -1) {
            // check if move is possible on boardIndex and markIndex
            return boards[boardIndex].isMovePossible(markIndex);
        }

        // check if board at nextBoardIndex is not closed and if nextBoardIndex is equal
        // to boardIndex to prevent placing outside current board
        if (!boards[getIndexNextBoard()].isClosed() && getIndexNextBoard() == boardIndex) {
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

}
