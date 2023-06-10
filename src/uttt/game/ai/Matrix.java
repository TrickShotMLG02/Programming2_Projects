package uttt.game.ai;

import java.io.Serializable;
import java.util.Arrays;

import uttt.game.SimulatorInterface;
import uttt.utils.Symbol;

/*
 * 
 * Following code is inspired by
 * https://towardsdatascience.com/understanding-and-implementing-neural-networks-in-java-from-scratch-61421bb6352c
 * but not directly copied, instead only noted down the functions which may be useful for matrices and implemented them from scratch
 * 
 * Also the correct usage of the activation functions is inspired by
 * https://towardsdatascience.com/how-to-choose-the-right-activation-function-for-neural-networks-3941ff0e6f9c
 * https://www.v7labs.com/blog/neural-networks-activation-functions
 * 
 * As well as their implementation and their derivative implementations
 * https://deepai.org/machine-learning-glossary-and-terms/softmax-layer
 * https://eli.thegreenplace.net/2016/the-softmax-function-and-its-derivative/
 * https://stackoverflow.com/questions/57631507/how-can-i-take-the-derivative-of-the-softmax-output-in-back-prop
 * 
 * More information can be found in "PROG 2 UTTT-AI Concept.pdf"
 * 
 */
public class Matrix implements Serializable {
    // store amount of rows and columns of the data matrix
    private int rowCount;
    private int colCount;

    // 2d array to store the data in
    private double[][] data;

    /**
     * Create a new matrix with specific amount of rows and columns and initialize
     * all values with random double between -1 and 1
     * 
     * @param rows number of rows (Nodes) of the matrix
     * @param cols number of columns (Depth) of the matrix
     */
    public Matrix(int rows, int cols) {
        data = new double[rows][cols];
        rowCount = rows;
        colCount = cols;

        // iterate over the whole matrix
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // populate whole matrix with random values between -1 and 1
                data[r][c] = Util.rndVal(-1.0, 1.0);
            }
        }
    }

    public void setMatrix0() {

        // iterate over the whole matrix
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                // populate whole matrix with random values between -1 and 1
                data[r][c] = 0;
            }
        }
    }

    /**
     * Add a specific value to each entry in the matrix
     * 
     * @param valueToAdd
     */
    public void elementWiseAddition(double valueToAdd) {
        // iterate over the whole matrix
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                // add value to current value
                data[r][c] += valueToAdd;
            }
        }
    }

    /**
     * Add a matrix with same size to current matrix
     * 
     * @param valueToAdd
     */
    public void elementWiseAddition(Matrix matrix) {
        // check if matrix size is equal to current size
        if (matrix.colCount != colCount || matrix.rowCount != rowCount)
            throw new IllegalArgumentException("Matrix doesn't have the same size as the target");

        // iterate over the whole matrix
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                // add value of matrix to current value
                data[row][col] += matrix.data[row][col];
            }
        }
    }

    /**
     * Calculate the result of adding matrix m1 and matrix m2
     * 
     * @param m1 Matrix m1
     * @param m2 Matrix m2
     * @return a new matrix containing the result of the addition of the two
     *         matrices m1 and m2
     */
    public static Matrix addMatrices(Matrix m1, Matrix m2) {
        // check if matrix size is equal to current size
        if (m1.colCount != m2.colCount || m1.rowCount != m2.rowCount)
            throw new IllegalArgumentException("Matrices doesn't have the same size");

        // create new matrix for storing result of addition
        Matrix result = new Matrix(m1.rowCount, m1.colCount);

        // iterate over the whole matrix
        for (int row = 0; row < m1.rowCount; row++) {
            for (int col = 0; col < m1.colCount; col++) {
                // add value from m1 to value from m2
                result.data[row][col] = m1.data[row][col] + m2.data[row][col];
            }
        }

        return result;
    }

    /**
     * Subtract a specific value from each entry in the matrix
     * 
     * @param valueToAdd
     */
    public void elementWiseSubtraction(double valueToAdd) {
        // iterate over the whole matrix
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                // subtract value from current value
                data[r][c] -= valueToAdd;
            }
        }
    }

    /**
     * Subtract a matrix with same size from current matrix
     * 
     * @param valueToAdd
     */
    public void elementWiseSubtraction(Matrix matrix) {

        // iterate over the whole matrix
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                // subtract value from matrix from current value
                data[row][col] -= matrix.data[row][col];
            }
        }
    }

    /**
     * Calculate the result of subtracting matrix m1 and matrix m2
     * 
     * @param m1 Matrix m1
     * @param m2 Matrix m2
     * @return a new matrix containing the result of the subtracting matrix m2 from
     *         matrix m1
     */
    public static Matrix subtractMatrices(Matrix m1, Matrix m2) {
        // create new matrix for storing result of addition
        Matrix result = new Matrix(m1.rowCount, m1.colCount);

        // iterate over the whole matrix
        for (int row = 0; row < m1.rowCount; row++) {
            for (int col = 0; col < m1.colCount; col++) {
                // subtract value from m2 from value from m1
                result.data[row][col] = m1.data[row][col] - m2.data[row][col];
            }
        }

        return result;
    }

    /**
     * Switch the rows and columns of a matrix
     * 
     * @param matrix
     * @return a new matrix where the value from [row|col] is now located at
     *         [col|row]
     */
    public static Matrix switchRowsAndCols(Matrix matrix) {
        // create new matrix to store result where rows and columns are switched, since
        // we want to "rotate" the matrix
        Matrix result = new Matrix(matrix.colCount, matrix.rowCount);

        // iterate over the whole matrix
        for (int row = 0; row < matrix.rowCount; row++) {
            for (int col = 0; col < matrix.colCount; col++) {
                // store value to swapped index of result matrix
                result.data[col][row] = matrix.data[row][col];
            }
        }
        return result;
    }

    /**
     * multiply a specific value with each entry in the matrix
     * 
     * @param valueToAdd
     */
    public void elementWiseMultiplication(double valueToAdd) {
        // iterate over the whole matrix
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                // multiply value with current value
                data[r][c] *= valueToAdd;
            }
        }
    }

    /**
     * multiply a matrix with same size with current matrix
     * 
     * @param valueToAdd
     */
    public void elementWiseMultiplication(Matrix matrix) {
        // iterate over the whole matrix
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < colCount; col++) {
                // multiply value of matrix with current value
                data[row][col] *= matrix.data[row][col];
            }
        }
    }

    /**
     * Calculate the dot product matrix m1 and matrix m2
     * 
     * @param m1 Matrix m1
     * @param m2 Matrix m2
     * @return a new matrix containing the result of the dot product of the two
     *         matrices m1 and m2
     */
    public static Matrix multiplyMatrices(Matrix m1, Matrix m2) {
        Matrix temp = new Matrix(m1.rowCount, m2.colCount);
        for (int i = 0; i < temp.rowCount; i++) {
            for (int j = 0; j < temp.colCount; j++) {
                double sum = 0;
                for (int k = 0; k < m1.colCount; k++) {
                    sum += m1.data[i][k] * m2.data[k][j];
                }
                temp.data[i][j] = sum;
            }
        }
        return temp;
    }

    /**
     * Applies the logistic function "1 / (1 + exp(-x))" to the matrix
     */
    public void logisticFunction() {
        // iterate over all elements in matrix
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                // logistic function
                data[r][c] = 1 / (1 + Math.exp(-data[r][c]));
            }
        }
    }

    /**
     * Applies the derivative logistic function "x * (1 - x)" to the matrix
     */
    public void derivativeLogisticFunction() {
        // iterate over all elements in matrix
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                // derivative logistic function
                data[r][c] = data[r][c] * (1 - data[r][c]);
            }
        }
    }

    /**
     * Applies the softMax function"exp(x_i) / sum(exp(x_j)) for j in 1 to n"
     * to the matrix which results in the propability
     * of each individual move
     */
    public void softMaxFunction() {

        double sum = 0.0;

        // sum all exponentiations of values up
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                sum += Math.exp(data[r][0]);
            }
        }

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                // softmax function
                data[r][c] = Math.exp(data[r][c]) / sum;
            }
        }
    }

    /**
     * Applies the derivative softMax function which is reached by subtracting the
     * actual output from the target value
     * as mentioned here
     * https://stackoverflow.com/questions/57631507/how-can-i-take-the-derivative-of-the-softmax-output-in-back-prop
     * 
     * @param targetValues the matrix containing the targetValues of the
     *                     corresponding inputs
     */
    public void derivativeSoftMaxFunction(Matrix targetValues) {
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                // derivative softmax function
                data[r][c] = targetValues.data[r][c] - data[r][c];
            }
        }
    }

    /**
     * Convert a game state (the board array from the simulator) to a matrix
     * representing the game state
     * 
     * @param sim the simulator containing the current game
     * @return a matrix with 162 nodes since there are 81 (9x9) possible fields for
     *         cross and for circle each. The fields contain 1 if the mark found
     *         matches the mark of the player (ai), -1 if the mark belongs to the
     *         oppnent and 0 if it is empty
     * 
     */
    public static Matrix convertGameStateToMatrix(SimulatorInterface sim) {

        int boardCount = sim.getBoards().length;
        int markCount = sim.getBoards()[0].getMarks().length;

        // Define symbols for cross and circle
        Symbol cross = Symbol.CROSS;
        Symbol circle = Symbol.CIRCLE;

        double ownMarkValue = 1;
        double opponentMarkValue = -1;
        double neutralValue = 0;

        // store current player symbol
        Symbol player = sim.getCurrentPlayerSymbol();

        // create matrix of size 162x1 which contains in the first 81 rows the states of
        // cross and circle
        // where the symbol of the current player is always represented as 1 and the
        // opponent as -1. Empty cells are 0
        Matrix gameState = new Matrix(boardCount * markCount * 2, 1);

        for (int b = 0; b < boardCount; b++) {
            for (int m = 0; m < markCount; m++) {
                if (sim.getBoards()[b].getMarks()[m].getSymbol() == cross) {
                    // check if player is cross or circle
                    if (player == cross) {
                        // set node in first 81 nodes to 1 if ai is cross
                        gameState.data[b * boardCount + m][0] = ownMarkValue;
                        // set node in second 81 nodes to 0 since we are circle
                        gameState.data[boardCount * markCount + (b * boardCount + m)][0] = neutralValue;
                    } else if (player == circle) {
                        // set node in first 81 nodes to -1 since circle is from opponent
                        gameState.data[b * boardCount + m][0] = neutralValue;
                        // set node in second 81 nodes to 0 since circles are stored in second 81 nodes
                        gameState.data[boardCount * markCount + (b * boardCount + m)][0] = opponentMarkValue;
                    }

                } else if (sim.getBoards()[b].getMarks()[m].getSymbol() == circle) {
                    // check if player is cross or circle
                    if (player == cross) {
                        // set node in first 81 nodes to 0 since crosses are stored in first 81 nodes
                        gameState.data[b * boardCount + m][0] = neutralValue;
                        // set node in second 81 nodes to -1 since circle is from opponent
                        gameState.data[boardCount * markCount + (b * boardCount + m)][0] = opponentMarkValue;
                    } else if (player == circle) {
                        // set node in first 81 nodes to 0 since circles are stored in second 81 nodes
                        gameState.data[b * boardCount + m][0] = ownMarkValue;
                        // set node in second 81 nodes to 0 since we are circle
                        gameState.data[boardCount * markCount + (b * boardCount + m)][0] = neutralValue;
                    }
                } else {
                    // field was empty, so set it to 0.5 in the matrix to recognize it later
                    gameState.data[b * boardCount + m][0] = neutralValue;
                    gameState.data[boardCount * markCount + (b * boardCount + m)][0] = neutralValue;
                }
            }
        }

        return gameState;
    }

    public static Matrix convertGameStateToMatrixSmall(SimulatorInterface sim) {

        int boardCount = sim.getBoards().length;
        int markCount = sim.getBoards()[0].getMarks().length;

        // Define symbols for cross and circle
        Symbol cross = Symbol.CROSS;
        Symbol circle = Symbol.CIRCLE;

        double ownMarkValue = 1;
        double opponentMarkValue = -1;
        double neutralValue = 0;

        // store current player symbol
        Symbol currPlayer = sim.getCurrentPlayerSymbol();

        // create matrix of size 81x1 which contains in the first 81 rows the states of
        // cross and circle
        // where the symbol of the current player is always represented as 1 and the
        // opponent as -1. Empty cells are 0
        Matrix gameState = new Matrix(boardCount * markCount, 1);

        for (int b = 0; b < boardCount; b++) {
            for (int m = 0; m < markCount; m++) {
                if (sim.getBoards()[b].getMarks()[m].getSymbol() == cross) {
                    // check if player is cross or circle
                    if (currPlayer == cross) {
                        // set node in first 81 nodes to 1 if ai is cross
                        gameState.data[b * boardCount + m][0] = ownMarkValue;
                    } else if (currPlayer == circle) {
                        // set node in first 81 nodes to -1 since circle is from opponent
                        gameState.data[b * boardCount + m][0] = opponentMarkValue;
                    }

                } else if (sim.getBoards()[b].getMarks()[m].getSymbol() == circle) {
                    // check if player is cross or circle
                    if (currPlayer == cross) {
                        // set node in first 81 nodes to 0 since crosses are stored in first 81 nodes
                        gameState.data[b * boardCount + m][0] = opponentMarkValue;
                    } else if (currPlayer == circle) {
                        // set node in first 81 nodes to 0 since circles are stored in second 81 nodes
                        gameState.data[b * boardCount + m][0] = ownMarkValue;
                    }
                } else {
                    // field was empty, so set it to 0.5 in the matrix to recognize it later
                    gameState.data[b * boardCount + m][0] = neutralValue;
                }
            }
        }

        return gameState;
    }

    /**
     * print the matrix to the console
     */
    public void printMatrix() {
        // grab all rows of the 2d array
        Arrays.stream(data).forEach(r -> {
            // grab for each row each element
            Arrays.stream(r).forEach(elem -> {
                // print elements of row
                System.out.print(elem + "\t");
            });
            // begin new row
            System.out.print("\n");
        });
    }

    /**
     * convert the nextBoardIndex into a matrix of size 162x1 where possible boards
     * to play on are marked with 1 and all others marked with 0
     * 
     * @param sim simulator containing current game
     * @return matrix containing possible boards to play on
     */
    public static Matrix convertNextBoardIndexToMatrix(SimulatorInterface sim) {
        int nextBoard = sim.getIndexNextBoard();

        // factorForbidden is used to cancel out invalid moves before applying softmax
        // function
        double factorForbidden = 0;
        // factorAllowed is used to keep valid moves unmodified
        double factorAllowed = 1;

        int boardCount = sim.getBoards().length;
        int markCount = sim.getBoards()[0].getMarks().length;

        Matrix result = new Matrix(boardCount * markCount * 2, 1);

        if (nextBoard != -1) {
            // iterate over the whole board (9x9 tiles)
            for (int i = 0; i < boardCount * markCount; i++) {
                // check if current position divided by 9 is nextBoardIndex

                if (i / boardCount == nextBoard) {
                    result.data[i][0] = factorAllowed;
                    result.data[i + boardCount * markCount][0] = factorAllowed;
                } else {
                    // set tile in cross and in circle section to 0 if not in next board
                    result.data[i][0] = factorForbidden;
                    result.data[i + boardCount * markCount][0] = factorForbidden;
                }
            }
        } else {
            // iterate over all boards
            for (int i = 0; i < boardCount; i++) {
                // check if board at index is not closed
                if (!sim.getBoards()[i].isClosed()) {

                    // iterate over matrix fields of corresponding board
                    for (int j = i * boardCount; j < i * boardCount + markCount; j++) {
                        // set board to 1 since it is open
                        result.data[j][0] = factorAllowed;
                    }
                } else {
                    // iterate over matrix fields of corresponding board
                    for (int j = i * boardCount; j < i * boardCount + markCount; j++) {
                        // set board to 0 since it is already closed
                        result.data[j][0] = factorForbidden;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Creates a matrix that represents only values which are not equal and cancels
     * out all other values
     * 
     * @param sim1 simulator state 1
     * @param sim2 simulator state 2
     * @return a matrix where equal values are cancelled out
     */
    public static Matrix calculateDifferences(SimulatorInterface sim1, SimulatorInterface sim2) {

        double differenceValue = 1;
        double equalityValue = 0;

        int boardCount = sim1.getBoards().length;
        int markCount = sim1.getBoards()[0].getMarks().length;

        Matrix m1 = Matrix.convertGameStateToMatrixSmall(sim1);
        Matrix m2 = Matrix.convertGameStateToMatrixSmall(sim2);

        // create Matrix with same size as output matrix of neural network
        Matrix result = new Matrix(boardCount * markCount, 1);

        for (int row = 0; row < result.rowCount; row++) {
            for (int col = 0; col < result.colCount; col++) {
                if (m1.data[row][col] == m2.data[row][col]) {
                    result.data[row][col] = equalityValue;
                } else {
                    result.data[row][col] = differenceValue;
                    System.out.println("found difference at boardIndex " + row / 9 + " and markIndex " + row % 9);
                }
            }
        }

        return result;
    }

    /**
     * Returns the boardindex and markindex of the biggest value
     * 
     * @param matrix matrix to evaluate
     * @return array of size 2 where result[0]: boardIndex and result[1]: markIndex
     */
    public static int[] getBiggestValueIndexes(SimulatorInterface sim, Matrix matrix) {
        // create double to hold max value
        int maxValRow = 0;
        int maxValCol = 0;

        int boardCount = sim.getBoards().length;
        int markCount = sim.getBoards()[0].getMarks().length;

        // iterate over matrix
        for (int r = 0; r < matrix.rowCount; r++) {
            for (int c = 0; c < matrix.colCount; c++) {
                // check if current value is larger than maxValue
                if (matrix.data[r][c] > matrix.data[maxValRow][maxValCol]) {
                    maxValRow = r;
                    maxValCol = c;
                }
            }
        }

        // convert row and col into boardindex and markindex
        int boardIndex = maxValRow / boardCount;
        int markIndex = maxValRow % markCount;

        // create array to hold boardindex and markindex
        int[] indexes = new int[2];
        indexes[0] = boardIndex;
        indexes[1] = markIndex;

        // return indexes
        return indexes;
    }

    public double[][] getData() {
        return data;
    }

    public String getSize() {
        return rowCount + " x " + colCount;
    }
}
