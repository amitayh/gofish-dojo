package gofish.game.player.action;

import gofish.game.player.Player;

abstract public class Action {
    
    final private Player player;

    public Action(Player player) {
        this.player = player;
    }
    
    public Player getPlayer() {
        return player;
    }

}
