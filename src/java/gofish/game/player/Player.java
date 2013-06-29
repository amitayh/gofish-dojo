package gofish.game.player;

abstract public class Player {
    
    public enum Type {COMPUTER, HUMAN};
    
    private static int numInstances = 0;
    
    private final int id;
    
    private Type type;
    
    private String name;

    public Player(Type type, String name) {
        id = ++numInstances;
        this.type = type;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }
    
    public boolean isHuman() {
        return (type == Type.HUMAN);
    }
    
    public boolean isComputer() {
        return (type == Type.COMPUTER);
    }

}
