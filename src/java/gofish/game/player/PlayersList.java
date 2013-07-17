package gofish.game.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayersList extends ArrayList<Player> {
    
    private int numHumanPlayers = 0;
    
    private int numComputerPlayers = 0;
    
    private Set<String> names = new HashSet<>();
    
    private Map<Integer, Player> players = new HashMap<>();

    public PlayersList(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public boolean add(Player player) {
        super.add(player);
        names.add(player.getName());
        players.put(player.getId(), player);
        if (player.isHuman()) {
            numHumanPlayers++;
        } else {
            numComputerPlayers++;
        }
        return true;
    }
    
    public Player getPlayerById(Integer playerId) {
        return players.get(playerId);
    }

    public int getNumHumanPlayers() {
        return numHumanPlayers;
    }

    public int getNumComputerPlayers() {
        return numComputerPlayers;
    }
    
    public boolean containsName(String name) {
        return names.contains(name);
    }

}
