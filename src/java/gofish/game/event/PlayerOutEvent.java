package gofish.game.event;

import gofish.game.player.Player;

public class PlayerOutEvent extends Event {
    
    public Player player;

    public PlayerOutEvent(Player player) {
        this.player = player;
    }

}
