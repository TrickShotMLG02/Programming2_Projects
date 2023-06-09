package uttt.game.ai;

import uttt.game.SimulatorInterface;
import uttt.utils.Move;

/**
 * representing a prediction by storing the output of the nodes from the output
 * layer in a 81x1 matrix and used for extracting the move predicted by the
 * model as well as its propability for playing the next move
 */
public class Prediction {

    // store data from outputLayer
    private Matrix outputData;

    // store predicted board and mark indexes
    private int boardIndex;
    private int markIndex;

    // the simulator containing the boards ect
    SimulatorInterface simulator;

    // the resulting move predicted
    private Move predictedMove;

    /**
     * Create new prediction
     * 
     * @param sim         simulator currently running
     * @param outputLayer the outputlayer created by predictNextMove of neural
     *                    network
     */
    public Prediction(SimulatorInterface sim, Matrix outputLayer) {
        simulator = sim;
        outputData = outputLayer;

        // calculate indexes from biggest propability in
        int[] indexes = Matrix.getBiggestValueIndexes(sim, outputData);
        boardIndex = indexes[0];
        markIndex = indexes[1];

        // create Move with boardindex and markindex
        predictedMove = new Move(boardIndex, markIndex);
    }

    public String toString() {
        return "Propability of choice: " + getPropability() + "\nBoardIndex: " + boardIndex + "\nMarkIndex: "
                + markIndex + "\n";
    }

    public double getPropability() {
        return outputData.getData()[boardIndex * 9 + markIndex][0];
    }

    public Matrix getData() {
        return outputData;
    }

    public Move getPredictedMove() {
        return predictedMove;
    }

    /**
     * used for easier model training since we are now able to set a specific field
     * as expected target
     * 
     * @param boardIndex index of the board where expected field is located
     * @param markIndex  index of the mark which is expected to be placed
     * @param value      value of the expected field. most likely 1
     */
    public void setAtIndex(int boardIndex, int markIndex, double value) {
        outputData.getData()[boardIndex * 9 + markIndex][0] = value;
    }

}
