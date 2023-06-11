package uttt.game.ai;

import uttt.game.BoardInterface;
import uttt.game.PlayerInterface;
import uttt.game.SimulatorInterface;
import uttt.game.UserInterface;
import uttt.utils.Move;
import uttt.utils.Symbol;

public class AIPlayer implements PlayerInterface {

    private Symbol playerSymbol;
    private NeuralNetwork utttModel;
    private NeuralNetwork tttModel;

    public int failedPredictions = 0;

    public static long timeoutInMs = 0;

    /**
     * creates a new AIPlayer with specific symbol
     * 
     * @param symbol symbol assigned to the player
     */
    public AIPlayer(Symbol symbol) {
        try {
            playerSymbol = symbol;

            // load neural network models
            utttModel = NeuralNetwork.LoadNetwork(AIManager.MODEL_TO_USE);
            tttModel = null;

        } catch (Exception e) {
            throw new IllegalArgumentException("Player Symbol is invalid");
        }
    }

    @Override
    public Symbol getSymbol() {
        return playerSymbol;
    }

    @Override
    public Move getPlayerMove(SimulatorInterface game, UserInterface ui) throws IllegalArgumentException {

        // check if game is null
        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }

        // check if ui is null ONLY FOR PLAYERS (NOT AI)
        if (ui == null) {
            throw new IllegalArgumentException("GUI not found");
        }

        if (utttModel == null) {
            throw new IllegalArgumentException("neural network not found");
        }

        // predict move using model
        Prediction prediction = utttModel.predictNextMove(game);
        Move predictedMove = prediction.getPredictedMove();

        // just for visualization, disabled by default
        if (timeoutInMs > 0) {
            // wait for visual feedback
            try {
                Thread.sleep(timeoutInMs);
            } catch (Exception e) {

            }
        }

        return predictedMove;
    }

    @Override
    public int getPlayerMove(BoardInterface board, UserInterface ui) throws IllegalArgumentException {

        // TODO: implement functionality for getPlayerMove

        // check if ui is null ONLY FOR PLAYERS (NOT AI)
        if (ui == null) {
            throw new IllegalArgumentException("GUI not found");
        }

        if (utttModel == null) {
            throw new IllegalArgumentException("neural network not found");
        }

        // predict move using model
        Prediction prediction = tttModel.predictNextMove(null);
        Move predictedMove = prediction.getPredictedMove();

        // just for visualization, disabled by default
        if (timeoutInMs > 0) {
            // wait for visual feedback
            try {
                Thread.sleep(timeoutInMs);
            } catch (Exception e) {

            }
        }

        return -1;
    }

}
