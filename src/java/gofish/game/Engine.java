package gofish.game;

import gofish.game.config.Config;
import gofish.game.config.ValidationException;
import gofish.game.engine.AddPlayerException;
import gofish.game.engine.GameStatusException;
import gofish.game.engine.StartGameException;
import gofish.game.event.Event;
import gofish.game.event.PlayerJoinEvent;
import gofish.game.event.StartGameEvent;
import gofish.game.player.Player;
import gofish.game.player.PlayersList;
import java.util.Observable;

public class Engine extends Observable {
    
    final public static int COMPLETE_SERIES_SIZE = 4;
    
    final public static int MIN_NUM_PLAYERS = 3;
        
    final public static int MAX_NUM_PLAYERS = 6;

    final public static int MIN_NUM_CARDS = 28;
    
    public enum Status {
        IDLE,
        CONFIGURED,
        STARTED,
        ENDED
    }
    
    private Status status = Status.IDLE;
    
    private Config config;
    
    private PlayersList players;
    
    public void configure(Config config) throws GameStatusException, ValidationException {
        ensureStatus(Status.IDLE);
        config.validate();
        this.config = config;
        this.players = new PlayersList(config.getTotalNumPlayers());
        status = Status.CONFIGURED;
    }
    
    public void reset() {
        config = null;
        players = null;
        status = Status.IDLE;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public Config getConfig() {
        return config;
    }
    
    public PlayersList getPlayers() {
        return players;
    }
    
    public void addPlayer(Player player) throws GameStatusException, AddPlayerException {
        ensureStatus(Status.CONFIGURED);
        
        String name = player.getName();
        if (name.isEmpty()) {
            throw new AddPlayerException("Empty player name");
        }
        if (players.containsName(name)) {
            throw new AddPlayerException("Duplicate player name '" + name + "'");
        }
        if (
            (player.isHuman() && players.getNumHumanPlayers() == config.getNumHumanPlayers()) ||
            (player.isComputer() && players.getNumComputerPlayers() == config.getNumComputerPlayers())
        ) {
            throw new AddPlayerException("Game is full");
        }
        
        players.add(player);
        dispatchEvent(new PlayerJoinEvent(player));
    }
    
    public boolean gameIsFull() {
        return (players.size() == config.getTotalNumPlayers());
    }
    
    public void startGame() throws GameStatusException, StartGameException {
        ensureStatus(Status.CONFIGURED);
        if (!gameIsFull()) {
            throw new StartGameException("Game is not full");
        }
        // TODO: check cards
        
        status = Status.STARTED;
        dispatchEvent(new StartGameEvent());
    }
    
    private void ensureStatus(Status requiredStatus) throws GameStatusException {
        if (status != requiredStatus) {
            throw new GameStatusException();
        }
    }
    
    private void dispatchEvent(Event event) {
        setChanged();
        notifyObservers(event);
    }

}
