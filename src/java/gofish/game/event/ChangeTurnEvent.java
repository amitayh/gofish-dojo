package gofish.game.event;

import gofish.game.player.Player;

public class ChangeTurnEvent extends Event {
    
    public Player currentPlayer;

    public ChangeTurnEvent(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

}
