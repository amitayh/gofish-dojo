package gofish.game.event;

import gofish.game.player.Player;

public class PlayerOutEvent extends Event {
    
    public Integer playerId;

    public PlayerOutEvent(Player player) {
        playerId = player.getId();
    }

}
