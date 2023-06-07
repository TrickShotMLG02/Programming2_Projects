package uttt.game;

import uttt.utils.Move;
import uttt.utils.Symbol;

public class Player implements PlayerInterface {

    private Symbol playerSymbol;

    public Player(Symbol symbol) {
        try {
            playerSymbol = symbol;
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

        return ui.getUserMove();

    }

}
