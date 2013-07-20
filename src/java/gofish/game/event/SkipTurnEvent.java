package gofish.game.event;

import gofish.game.player.Player;

public class SkipTurnEvent extends Event {
    
    public Player player;

    public SkipTurnEvent(Player player) {
        this.player = player;
    }

}
