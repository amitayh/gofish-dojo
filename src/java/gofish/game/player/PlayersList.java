package gofish.game.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PlayersList extends ArrayList<Player> {
    
    private int numHumanPlayers = 0;
    
    private int numComputerPlayers = 0;
    
    private Set<String> names = new HashSet<>();

    public PlayersList(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public boolean add(Player player) {
        super.add(player);
        names.add(player.getName());
        if (player.isHuman()) {
            numHumanPlayers++;
        } else {
            numComputerPlayers++;
        }
        return true;
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
