package gofish.game.exception;

public class TooManyPlayersException extends Exception {

    private int max;

    public TooManyPlayersException(int max) {
        super("Too many players (maximum: " + max + ")");
        this.max = max;
    }

    public int getMax() {
        return max;
    }

}
