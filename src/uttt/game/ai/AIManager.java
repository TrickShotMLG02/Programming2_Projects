package uttt.game.ai;

import java.util.ArrayList;
import java.util.List;

import uttt.UTTTFactory;
import uttt.game.Board;
import uttt.game.BoardInterface;
import uttt.game.GameRunnerInterface;
import uttt.game.Main;
import uttt.game.PlayerInterface;
import uttt.game.Simulator;
import uttt.game.SimulatorInterface;
import uttt.game.UserInterface;
import uttt.tests.util;
import uttt.utils.Symbol;

/*
 * 
 * Class for creating, training and evolving models
 * 
 */
public class AIManager {

    // Storage for the current model
    public static NeuralNetwork model;

    // Loading a model
    private static boolean loadExistingModel = false;
    public static final String MODEL_TO_USE = "src/uttt/game/ai/models/model_game_training.dat";

    // Save model / update model
    private static boolean saveModel = false;
    public static boolean updateModel = false;

    // Training
    private static boolean iterativeTraining = true;
    private static int iterations = 100;
    private static boolean trainAIOnGame = false;

    // Test trained scenarios
    private static boolean testGameStateAI = false;

    // Play real game against ai
    private static boolean playGameVsAI = false;
    private static boolean playAIVsAI = false;
    private static boolean playAIVsRandom = false;

    public static void setServerUse() {
        loadExistingModel = false;
        saveModel = false;
        updateModel = false;
        iterativeTraining = true;
        iterations = 100;
        trainAIOnGame = false;
        testGameStateAI = false;
        playGameVsAI = false;
        playAIVsAI = false;
        playAIVsRandom = false;
    }

    /**
     * Function to create a new neural network
     */
    public static void setupModel() {
        if (loadExistingModel) {
            model = NeuralNetwork.LoadNetwork(MODEL_TO_USE);
        } else {
            model = new NeuralNetwork(0.05, 162, 81, 100);
        }
    }

    public static void main(String[] args) {

        setupModel();

        if (model == null) {
            System.out.println("model null");

            // create new model and save it
            loadExistingModel = false;

            setupModel();

            // save model to disk
            NeuralNetwork.SaveModel(model, MODEL_TO_USE, false);

            // enable load model from disk
            loadExistingModel = true;

            // restart function main

            main(args);
            return;

        } else {
            // set up test cases here for training
            Symbol aiSymbol = Symbol.CIRCLE;

            if (!loadExistingModel) {
                // create predictions to train model on
                List<Prediction> predictions = new ArrayList<Prediction>();
                predictions.add(createPredictionBoard2ToWin(aiSymbol));
                predictions.add(createPredictionBoard3ToWin(aiSymbol));
                // predictions.add(createPredictionBoard5ToWin(aiSymbol));

                // extract simulators from predictions
                List<SimulatorInterface> simulators = new ArrayList<SimulatorInterface>();
                predictions.forEach(prediction -> {
                    simulators.add((SimulatorInterface) prediction.simulator);
                });

                if (iterativeTraining) {
                    // train model on simulators and predictions
                    model.iterativeTraining(
                            (SimulatorInterface[]) simulators.toArray(new SimulatorInterface[simulators.size()]),
                            (Prediction[]) predictions.toArray(new Prediction[predictions.size()]),
                            iterations);

                }

                // afterwards save model to disk
                if (saveModel) {
                    NeuralNetwork.SaveModel(model, "src/uttt/game/ai/models/model", true);
                } else if (updateModel) {
                    NeuralNetwork.UpdateModel(model);
                }

                System.out.println("\n");
            }

            if (testGameStateAI) {

                NeuralNetwork.verbose = true;

                // printing expected output, actual predicted output as well as if it was
                // correctly
                Prediction pTest = createPredictionBoard2ToWin(aiSymbol);
                System.out.println(pTest.expectedToString());
                System.out.println(
                        "Successful prediction: "
                                + pTest.predictionCorrect(model.predictNextMove(pTest.simulator)) + "\n");
            } else if (playGameVsAI) {
                // set timeout for ai player
                AIPlayer.timeoutInMs = 500;

                // start a game with human player and ai
                Main.main(new String[] { "true", "false" });
            } else if (trainAIOnGame) {
                // train the ai on a game being played between 2 human players
                // thus start a new game between 2 players, after each valid move,
                // create new prediction of the current game state
                // and train model on state before move with new move as expected output

                NeuralNetwork.verbose = true;

                // start game with player 1 human and player 2 AI
                AIPlayer.timeoutInMs = 1000;
                LaunchGame(true, false);
            } else if (playAIVsAI) {
                AIPlayer.timeoutInMs = 500;
                LaunchGame(false, false);
            } else if (playAIVsRandom) {
                // create random player

            }

        }

    }

    public static Prediction createPredictionBoard2ToWin(Symbol winnerSymbol) {

        BoardInterface[] boards = new BoardInterface[9];

        BoardInterface b0_won = new Board();
        b0_won.setMarks(util.createMarkInterfaceWin(winnerSymbol));
        boards[0] = b0_won;

        BoardInterface b1_won = new Board();
        b1_won.setMarks(util.createMarkInterfaceWin(winnerSymbol));
        boards[1] = b1_won;

        BoardInterface b2_toWin = new Board();
        // setup marks
        Symbol looser = winnerSymbol.flip();
        b2_toWin.setMarkAt(winnerSymbol, 0);
        b2_toWin.setMarkAt(winnerSymbol, 1);
        // let 2 empty for ai
        b2_toWin.setMarkAt(looser, 3);
        b2_toWin.setMarkAt(looser, 4);

        boards[2] = b2_toWin;

        boards[3] = new Board();
        boards[4] = new Board();
        boards[5] = new Board();
        boards[6] = new Board();
        boards[7] = new Board();
        boards[8] = new Board();

        // create simulatorInterface
        SimulatorInterface sim = new Simulator();
        sim.setBoards(boards);
        sim.setIndexNextBoard(2);
        sim.setCurrentPlayerSymbol(winnerSymbol);

        Matrix expectedOutput = new Matrix(81, 1);
        expectedOutput.setMatrix0();

        Prediction p = new Prediction(sim, expectedOutput);

        p.setAtIndex(2, 2, 1);

        return p;
    }

    public static Prediction createPredictionBoard3ToWin(Symbol winnerSymbol) {

        BoardInterface[] boards = new BoardInterface[9];

        BoardInterface b0_won = new Board();
        b0_won.setMarks(util.createMarkInterfaceWin(winnerSymbol));
        boards[0] = b0_won;

        boards[1] = new Board();
        boards[2] = new Board();

        boards[4] = new Board();
        boards[5] = new Board();

        BoardInterface b6_won = new Board();
        b6_won.setMarks(util.createMarkInterfaceWin(winnerSymbol));
        boards[6] = b6_won;

        boards[7] = new Board();
        boards[8] = new Board();

        BoardInterface b3_toWin = new Board();
        // setup marks
        Symbol looser = winnerSymbol.flip();
        b3_toWin.setMarkAt(winnerSymbol, 1);
        b3_toWin.setMarkAt(winnerSymbol, 7);
        // let 4 empty for ai
        b3_toWin.setMarkAt(looser, 0);
        b3_toWin.setMarkAt(looser, 8);

        boards[3] = b3_toWin;

        // create simulatorInterface
        SimulatorInterface sim = new Simulator();
        sim.setBoards(boards);
        sim.setIndexNextBoard(2);
        sim.setCurrentPlayerSymbol(winnerSymbol);

        Matrix expectedOutput = new Matrix(81, 1);
        expectedOutput.setMatrix0();

        Prediction p = new Prediction(sim, expectedOutput);

        p.setAtIndex(3, 4, 1);

        return p;
    }

    // TODO: Board5ToWin is same as Board3ToWin -> edit ASAP
    public static Prediction createPredictionBoard5ToWin(Symbol winnerSymbol) {

        BoardInterface[] boards = new BoardInterface[9];

        BoardInterface b0_won = new Board();
        b0_won.setMarks(util.createMarkInterfaceWin(winnerSymbol));
        boards[0] = b0_won;

        boards[1] = new Board();
        boards[2] = new Board();

        boards[4] = new Board();
        boards[5] = new Board();

        BoardInterface b6_won = new Board();
        b6_won.setMarks(util.createMarkInterfaceWin(winnerSymbol));
        boards[6] = b6_won;

        boards[7] = new Board();
        boards[8] = new Board();

        BoardInterface b3_toWin = new Board();
        // setup marks
        Symbol looser = winnerSymbol.flip();
        b3_toWin.setMarkAt(winnerSymbol, 1);
        b3_toWin.setMarkAt(winnerSymbol, 7);
        // let 4 empty for ai
        b3_toWin.setMarkAt(looser, 0);
        b3_toWin.setMarkAt(looser, 8);

        boards[3] = b3_toWin;

        // create simulatorInterface
        SimulatorInterface sim = new Simulator();
        sim.setBoards(boards);
        sim.setIndexNextBoard(2);
        sim.setCurrentPlayerSymbol(winnerSymbol);

        // create expectedOutput matrix and initialize with 0
        Matrix expectedOutput = new Matrix(81, 1);
        expectedOutput.setMatrix0();

        // create new prediction to use for training
        Prediction p = new Prediction(sim, expectedOutput);

        // set expected field to 1
        p.setAtIndex(3, 4, 1);

        return p;
    }

    public static void LaunchGame(boolean p1human, boolean p2human) {
        // Create an instance of the Ultimate TicTacToe training simulator.
        GameRunnerInterface game = new AITrainingSimulator();

        // Create an instance of the user interface that is used to communicate
        // with the user.
        UserInterface ui = UTTTFactory.createUserInterface(true);

        PlayerInterface playerOne;
        PlayerInterface playerTwo;

        // Create an instance of player one.
        if (p1human) {
            playerOne = UTTTFactory.createHumanPlayer(Symbol.CROSS);
        } else {
            playerOne = UTTTFactory.createBonusPlayer(Symbol.CROSS);
        }

        // Create an instance of player two.
        if (p2human) {
            playerTwo = UTTTFactory.createHumanPlayer(Symbol.CIRCLE);
        } else {
            playerTwo = UTTTFactory.createBonusPlayer(Symbol.CIRCLE);
        }

        System.out.println("\n\nRunning Training Simulator\n\n");

        // Simulate the game until it is over.
        game.run(playerOne, playerTwo, ui);
    }

}
