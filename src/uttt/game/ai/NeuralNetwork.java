package uttt.game.ai;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Timestamp;

import uttt.game.SimulatorInterface;

/*
 * 
 * Following code is inspired by
 * https://towardsdatascience.com/understanding-and-implementing-neural-networks-in-java-from-scratch-61421bb6352c
 * but not directly copied, instead only noted down the functions which may be useful for matrices and implemented them mostly from scratch
 * 
 * in addition this explanation was considered for the design of the neural network
 * https://www.numpyninja.com/post/neural-network-and-its-functionality
 * 
 * 
 * More information can be found in "PROG 2 UTTT-AI Concept.pdf"
 */
public class NeuralNetwork implements Serializable {

    public static final String MODEL_TO_USE = "src/uttt/game/ai/models/model_test.dat";

    // matrices to store weights and bias for the layser
    private Matrix weight_input_hidden;
    private Matrix bias_hidden;
    private Matrix weight_output_hidden;
    private Matrix bias_output;

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
        Matrix allowedMovesFilter = Matrix.convertNextBoardIndexToMatrix(sim);
        // to filter out invalid moves, multiply the filter with the hidden layer
        hiddenLayer.elementWiseMultiplication(allowedMovesFilter);

        // multiply output from hidden layer with corresponding weights
        Matrix outputLayer = Matrix.multiplyMatrices(weight_output_hidden, hiddenLayer);
        // add bias to the calculated output layer
        outputLayer.elementWiseAddition(bias_output);
        // apply softmax activation function to outputLayer
        outputLayer.softMaxFunction();

        // create prediction with outputLayer
        Prediction nextMovePrediction = new Prediction(sim, outputLayer);

        // print statistics about prediction
        System.out.println(nextMovePrediction.toString());

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
        predictNextMove(state);
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
    public static void SaveModel(NeuralNetwork model, String fileName) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        try {
            File file = new File(fileName + " (" + timestamp + ").dat");
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
