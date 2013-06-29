package gofish.game;

import gofish.game.config.Config;
import gofish.game.config.ValidationException;
import gofish.game.event.Event;
import gofish.game.event.PlayerJoinEvent;
import gofish.game.event.StartGameEvent;
import gofish.game.exception.EmptyNameException;
import gofish.game.exception.DuplicateNameException;
import gofish.game.exception.TooManyPlayersException;
import gofish.game.player.Player;
import gofish.game.player.PlayersList;
import java.util.ArrayList;
import java.util.List;

public class Engine {
    
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
    
    private List<Event> eventQueue = new ArrayList<>();
    
    private Config config;
    
    private PlayersList players;
    
    public void configure(Config config) throws ValidationException {
        config.validate();
        this.config = config;
        this.players = new PlayersList(config.getTotalNumPlayers());
        status = Status.CONFIGURED;
    }
    
    public void reset() {
        config = null;
        players = null;
        status = Status.IDLE;
        eventQueue.clear();
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
    
    public List<Event> getEvents(int startIndex) {
        return eventQueue.subList(startIndex, eventQueue.size());
    }
    
    public void addPlayer(Player player) throws EmptyNameException, DuplicateNameException, TooManyPlayersException {
        String name = player.getName();
        if (name.isEmpty()) {
            throw new EmptyNameException();
        }
        if (players.containsName(name)) {
            throw new DuplicateNameException(name);
        }
        if (
            (player.isHuman() && players.getNumHumanPlayers() == config.getNumHumanPlayers()) ||
            (player.isComputer() && players.getNumComputerPlayers() == config.getNumComputerPlayers())
        ) {
            throw new TooManyPlayersException(config.getTotalNumPlayers());
        }
        players.add(player);
        eventQueue.add(new PlayerJoinEvent(player));
        
        if (players.size() == config.getTotalNumPlayers()) {
            // All players joined, start game
            dealCards();
            startGame();
        }
    }
    
    private void startGame() {
        status = Status.STARTED;
        eventQueue.add(new StartGameEvent());
    }

    private void dealCards() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
