package uttt.game.ai;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import uttt.game.SimulatorInterface;
import uttt.utils.Move;

/*
 * 
 * Following code is inspired by
 * https://towardsdatascience.com/understanding-and-implementing-neural-networks-in-java-from-scratch-61421bb6352c
 * but not directly copied, instead only noted down the functions which may be useful for matrices and implemented them mostly from scratch
 * 
 * in addition this explanation was considered for the design of the neural network
 * https://www.numpyninja.com/post/neural-network-and-its-functionality
 * 
 * Also the script from Marius Smytzek of lecture Neural Networks (2019) was used for deeper understanding of neural networks and their implementation
 * 
 * 
 * More information can be found in "PROG 2 UTTT-AI Concept.pdf"
 */
public class NeuralNetwork implements Serializable {

    public static final String MODEL_TO_USE = "src/uttt/game/ai/models/model_test.dat";
    public static boolean verbose = true;

    // matrices to store weights and bias for the layer
    /*
     * 
     * TODO:
     * Since file read is not supported on the server, just print finished matrices
     * and manually define their values below, but make sure, they don't get
     * overridden by random values
     * 
     */
    private Matrix weight_input_hidden;
    private Matrix bias_hidden;
    private Matrix weight_output_hidden;
    private Matrix bias_output;

    private static double penaltyForBadMove = 0.01;
    private static double rewardForGoodMove = 1;

    // specify learning rate
    private double learningRate;

    /**
     * creates a new neural network
     * 
     * @param rate             specifies the learning rate
     * @param inputNodes       specifies the amount of input nodes
     * @param outputNodes      specifies the amount of output nodes
     * @param hiddenLayerNodes might be an array or a single int specifying the
     *                         amount of nodes in each hidden layer
     * @throws Exception
     */
    public NeuralNetwork(double rate, int inputNodes, int outputNodes, int... hiddenLayerNodes) {

        /*
         * TODO:
         * Edit code for dynamic amount of hidden Layers
         * 
         * Base implementation is already done, just need to create a list of weights
         * and biases for each layer and calculate them in some kind of loop dynamically
         */

        if (hiddenLayerNodes.length > 1)
            throw new IllegalArgumentException("Use of more than 1 hidden layer not yet implemented");

        // create weights and bias
        weight_input_hidden = new Matrix(hiddenLayerNodes[0], inputNodes);
        bias_hidden = new Matrix(hiddenLayerNodes[0], hiddenLayerNodes.length);
        weight_output_hidden = new Matrix(outputNodes, hiddenLayerNodes[0]);
        bias_output = new Matrix(outputNodes, hiddenLayerNodes.length);

        // apply learning rate
        learningRate = rate;
    }

    /**
     * predicts the next move
     * 
     * @param sim the simulator containing the current game
     * @return the prediction made by respecting the current game state
     */
    public Prediction predictNextMove(SimulatorInterface sim) {

        // create matrix of current game
        Matrix inputData = Matrix.convertGameStateToMatrix(sim);

        // multiply input with the corresponding weights
        Matrix hiddenLayer = Matrix.multiplyMatrices(weight_input_hidden, inputData);

        // add bias to the calculated and filtered hidden Layer
        hiddenLayer.elementWiseAddition(bias_hidden);
        // apply logistic activation function to hiddenLayer
        hiddenLayer.logisticFunction();

        // convert nextBoardIndex to a matrix to filter out all boards which can not be
        // chosen in this move
        Matrix allowedBoardsFilter = Matrix.convertNextBoardIndexToMatrix(sim);

        // convert all available mark positions to matrix of size 81
        Matrix allowedMarksFilter = Matrix.convertPossibleMarkIndexToMatrix(sim);

        // multiply output from hidden layer with corresponding weights
        Matrix outputLayer = Matrix.multiplyMatrices(weight_output_hidden, hiddenLayer);
        // add bias to the calculated output layer
        outputLayer.elementWiseAddition(bias_output);

        // to filter out any invalid moves, multiply the filters for boards and marks
        // with the output layer before applying softMaxDistribution
        outputLayer.elementWiseMultiplication(allowedBoardsFilter);
        outputLayer.elementWiseMultiplication(allowedMarksFilter);

        // apply softmax activation function to outputLayer
        outputLayer.softMaxFunction();

        // create prediction with outputLayer
        Prediction nextMovePrediction = new Prediction(sim, outputLayer);

        System.out.println("propability for correct output: " + outputLayer.getData()[9][0]);

        // print statistics about prediction if verbose
        if (verbose)
            System.out.println(nextMovePrediction.toString() + "\n\n");

        // return prediction
        return nextMovePrediction;
    }

    /**
     * Trains the model on the current simulator while paying attention to the
     * target prediction
     * 
     * @param state       the current game state as simulator (for the boards[])
     * @param targetState the prediction (Matrix of 9x9) which should be the outcome
     */
    public void trainModel(SimulatorInterface state, Prediction targetState) {
        // convert game state to matrix
        Matrix gameState = Matrix.convertGameStateToMatrix(state);
        // create hiddenLayer matrix
        Matrix hiddenLayer = Matrix.multiplyMatrices(weight_input_hidden, gameState);
        // add bias to hidden Layer
        hiddenLayer.elementWiseAddition(bias_hidden);
        // apply logistic function
        hiddenLayer.logisticFunction();

        // create output layer matrix
        Matrix outputLayer = Matrix.multiplyMatrices(weight_output_hidden, hiddenLayer);
        // add bias to outputlayer
        outputLayer.elementWiseAddition(bias_output);
        // apply softmax to output layer
        outputLayer.softMaxFunction();

        // apply penalties for ai to outputLayer
        punishBadMove(state, outputLayer, penaltyForBadMove);

        // create new matrix for targetstate
        Matrix targetOutput = targetState.getData();

        // calculate errors in output based on differences between target and actual
        // output
        Matrix outputErrors = Matrix.subtractMatrices(targetOutput, outputLayer);

        /*
         * TODO:
         * maybe apply filter matrix for filtering out invalid moves
         * by applying penalties 0.01 for valid and 1 for invalid moves
         * 
         * If implementing:
         * check filter matrix size to match output matrix size but should be no problem
         * i guess
         * 
         * convert nextBoardIndex to a matrix to filter out all boards which can not be
         * chosen in this move
         *
         * Matrix allowedMovesFilter = Matrix.convertNextBoardIndexToMatrix(state);
         *
         * i think i have to apply filter matrix to outputErrors for correct penalty
         * calculations, but not sure about that.
         * 
         * It works at the moment without penalties while training
         *
         * outputErrors.elementWiseMultiplication(allowedMovesFilter);
         *
         * 
         * But check at some point in time if this is neccessary or not
         */

        // create matrix for calculating the derivation of the output Layer with respect
        // to the targetOutput
        Matrix outputDerivation = outputLayer;
        outputDerivation.derivativeSoftMaxFunction(targetOutput);
        // calculate error rate into derivation
        outputDerivation.elementWiseMultiplication(outputErrors);
        // adjust derivation by learning rate
        outputDerivation.elementWiseMultiplication(learningRate);

        // switch rows and columns of hiddenLayer
        Matrix switchedHiddenOutputLayers = Matrix.switchRowsAndCols(hiddenLayer);
        // calculate weight adjustments of hiddenLayer
        Matrix hiddenOutputWeightAdjustments = Matrix.multiplyMatrices(outputDerivation, switchedHiddenOutputLayers);
        // apply adjustments to weight_output_hidden
        weight_output_hidden.elementWiseAddition(hiddenOutputWeightAdjustments);
        // apply derivation to bias_output
        bias_output.elementWiseAddition(outputDerivation);

        // switch rows and columns of weightsHiddenOutput
        Matrix switchedHiddenOutputWeights = Matrix.switchRowsAndCols(weight_output_hidden);
        // calculate errors in hidden layers
        Matrix hiddenErrors = Matrix.multiplyMatrices(switchedHiddenOutputWeights, outputErrors);

        // calculate hiddenDerivation by applying derivative Logistic Function
        Matrix hiddenDerivation = hiddenLayer;
        hiddenDerivation.derivativeLogisticFunction();
        // calculate error rate into derivation
        hiddenDerivation.elementWiseMultiplication(hiddenErrors);
        // adjust derivation by learning rate
        hiddenDerivation.elementWiseMultiplication(learningRate);

        // switch rows and columnds of input nodes
        Matrix switchedInputLayer = Matrix.switchRowsAndCols(gameState);
        // calculate weight adjustments of inputLayer
        Matrix inputWeightAdjustments = Matrix.multiplyMatrices(hiddenDerivation, switchedInputLayer);
        // apply adjustments to to weight_input_hidden
        weight_input_hidden.elementWiseAddition(inputWeightAdjustments);
        // apply derivation to bias_hidden
        bias_hidden.elementWiseAddition(hiddenDerivation);

        // predict next move just for analysis how good it worked
        // predictNextMove(state);
    }

    /**
     * TODO: implement second part which applies penalty if ai blocks opponent
     * instead of winning
     * Punishes a bad move which let the enemy win the current board or if it blocks
     * enemy instead of winning a board
     * 
     * @param state       the state before the ai move
     * @param outputLayer the output layer of the network, where we want to apply
     *                    punishment to before backpropagating
     * @param penalty     the value of the penalty which is applied
     */
    private void punishBadMove(SimulatorInterface state, Matrix outputLayer, double penalty) {
        // Get the move that let the enemy win the game and punish it
        List<Move> possibleMoves = AITrainingSimulator.getPossibleMoves(state);
        Move badMove;

        // TODO: check if move from ai let the opponent player win
        // For that extract the move from the outputlayer (the index of the element with
        // highest propability), which represents the move, which will be done by the ai
        int[] indexes = Matrix.getBiggestValueIndexes(state, outputLayer);
        int boardIndex = indexes[0];
        int markIndex = indexes[1];

        // create copy of Simulator
        SimulatorInterface simulateNextStep = AITrainingSimulator.copySimulator(state);
        // simulate move on copied simulator which was predicted by ai
        AITrainingSimulator.simulateMove(simulateNextStep, new Move(boardIndex, markIndex));

        // check if ai blocked the opponent from winning with next move, if so, then do
        // nothing. else, punish every move which didn't block the ai

        if (AITrainingSimulator.isWinPossible(simulateNextStep)) {
            // ai blocked successfully
            System.out.println("AI blocked opponent from winning");
            // apply reward to move in matrix
            int index = boardIndex * 9 + markIndex;
            outputLayer.getData()[index][0] *= rewardForGoodMove;
            return;
        }

        for (int i = 0; i < possibleMoves.size(); i++) {
            // create copy of Simulator
            simulateNextStep = AITrainingSimulator.copySimulator(state);
            // simulate move on copied simulator
            AITrainingSimulator.simulateMove(simulateNextStep, possibleMoves.get(i));

            // check if opponent will be able to win the game after applying move from list
            if (AITrainingSimulator.isWinPossible(simulateNextStep)) {
                // store current move as bad move since it let the opponent win
                badMove = possibleMoves.get(i);

                // calculate position of move in output layer matrix
                int matrixIndex = badMove.getBoardIndex() * 9 + badMove.getMarkIndex();
                // TODO: punish move by subtracting or multiplying penalty

                // print penalty information
                System.out.println("punished ai (" + state.getCurrentPlayerSymbol().toString()
                        + ") for move which didn't block opponent ("
                        + simulateNextStep.getCurrentPlayerSymbol().toString() + ") at BoardIndex: "
                        + badMove.getBoardIndex() + " MarkIndex: " + badMove.getMarkIndex() + "\nFrom "
                        + outputLayer.getData()[matrixIndex][0] + " to "
                        + (outputLayer.getData()[matrixIndex][0] * penalty));

                outputLayer.getData()[matrixIndex][0] *= penalty;
            }
        }

    }

    /**
     * iteratively trains a model on the specific data sets provided
     * 
     * @param states       an array of game state as simulator (for the boards[])
     * @param targetStates an array of the corresponding predictions (Matrix of 9x9)
     *                     which should be the outcome
     * @param iterations   number of iterations the model should be trained on the
     *                     input data
     */
    public void iterativeTraining(SimulatorInterface[] states, Prediction[] targetStates, int iterations) {
        for (int i = 0; i < iterations; i++) {
            // get random index in states array
            int rnd = Util.rndInt(0, states.length - 1);
            System.out.println("Training iteration " + (i + 1) + " of " + iterations);
            trainModel(states[rnd], targetStates[rnd]);
        }
        System.out.println("Finished training on " + iterations + " iterations.");
    }

    /**
     * Saves the current model to the disk as "model *timestamp*"
     * 
     * @param model    the model which was created and should be saved
     * @param fileName the relative path of the location to save file to
     * 
     */
    public static void SaveModel(NeuralNetwork model, String fileName, boolean appendTimestamp) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        try {
            File file;
            if (appendTimestamp) {
                file = new File(fileName + " (" + timestamp + ").dat");
            } else {
                file = new File(fileName);
            }

            file.createNewFile();
            System.out.println("File created at " + file.getAbsolutePath());
            FileOutputStream fileOutput = new FileOutputStream(file);
            ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);

            // Write objects to file
            objectOutput.writeObject(model);

            // close streams
            objectOutput.close();
            fileOutput.close();
            System.out.println("File saved at " + file.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Error saving model");
            e.printStackTrace();
        }
    }

    /**
     * Updates the current model on the disk
     * 
     * @param model the model which was evolved and should be saved
     * 
     */
    public static void UpdateModel(NeuralNetwork model) {

        try {
            File file = new File(AIManager.MODEL_TO_USE);
            file.delete();
            file.createNewFile();
            System.out.println("Model updated at " + file.getAbsolutePath());
            FileOutputStream fileOutput = new FileOutputStream(file);
            ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);

            // Write objects to file
            objectOutput.writeObject(model);

            // close streams
            objectOutput.close();
            fileOutput.close();
            System.out.println("File saved at " + file.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Error saving model");
            e.printStackTrace();
        }
    }

    /**
     * Loads a model from a file
     * 
     * @param fileName the relative path of the location to load file from
     * @return the neural network obtained from the loaded file
     * 
     */
    public static NeuralNetwork LoadNetwork(String fileName) {
        try {
            FileInputStream fileInput = new FileInputStream(new File(fileName));
            ObjectInputStream objectInput = new ObjectInputStream(fileInput);

            // Read objects
            NeuralNetwork model = (NeuralNetwork) objectInput.readObject();

            // close streams
            objectInput.close();
            fileInput.close();

            return model;
        } catch (Exception e) {
            System.out.println("Error loading model");
        }

        return null;
    }
}
