package gofish.game.event;

import gofish.game.player.Player;

public class PlayerJoinEvent extends Event {
    
    public Player player;

    public PlayerJoinEvent(Player player) {
        this.player = player;
    }

}
