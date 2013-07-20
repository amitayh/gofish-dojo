package gofish.game.event;

import gofish.game.player.Player;

public class QuitGameEvent extends Event {
    
    public Player player;

    public QuitGameEvent(Player player) {
        this.player = player;
    }

}
