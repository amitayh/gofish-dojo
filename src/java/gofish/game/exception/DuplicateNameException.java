package gofish.game.exception;

public class DuplicateNameException extends Exception {
    
    private String name;

    public DuplicateNameException(String name) {
        super("Player with name '" + name + "' was added twice");
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
