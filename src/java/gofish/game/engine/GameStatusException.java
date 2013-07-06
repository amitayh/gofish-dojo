package gofish.game.engine;

public class GameStatusException extends Exception {

    public GameStatusException() {
        super("Operation is illegal for current game status");
    }

}
