package gofish.servlet;

import gofish.game.Engine;
import gofish.game.config.Config;
import gofish.game.config.ValidationException;
import gofish.game.engine.AddPlayerException;
import gofish.game.engine.GameStatusException;
import gofish.game.player.Player;
import gofish.servlet.observer.EventsQueueObserver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
    
    final public Engine engine;
    
    final public EventsQueueObserver events = new EventsQueueObserver();
    
    final public Map<String, Player> humanPlayers = new HashMap<>(Engine.MAX_NUM_PLAYERS);
    
    public Game(Engine engine) {
        this.engine = engine;
        engine.addObserver(events);
    }
    
    public void configure(Config config, List<Player> players)
            throws GameStatusException, ValidationException, AddPlayerException {
        
        reset();
        engine.configure(config);
        addPlayers(players);
    }

    private void reset() {
        engine.reset();
        events.clear();
        humanPlayers.clear();
    }

    private void addPlayers(List<Player> players)
            throws GameStatusException, AddPlayerException {
        
        for (Player player : players) {
            if (player.isComputer()) {
                engine.addPlayer(player);
            } else {
                humanPlayers.put(player.getName(), player);
            }
        }
    }

}
