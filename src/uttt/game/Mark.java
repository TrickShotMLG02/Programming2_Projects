package uttt.game;

import uttt.utils.Symbol;

public class Mark implements MarkInterface {

    private Symbol symbol;
    private int position;

    public Mark(Symbol symbol, int position) {
        setSymbol(symbol);
        setPosition(position);
    }

    @Override
    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setSymbol(Symbol symbol) throws IllegalArgumentException {
        // check if symbol is null -> set it to Empty, otherwise apply symbol
        if (symbol == null)
            this.symbol = Symbol.EMPTY;
        else
            this.symbol = symbol;
    }

    private void setPosition(int position) {
        // set position
        if (position < 0 || position > 8)
            throw new IllegalArgumentException("Position must be between 0 and 8");
        else
            this.position = position;
    }

}
