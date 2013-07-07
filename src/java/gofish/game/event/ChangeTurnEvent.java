package gofish.game.event;

import gofish.game.player.Player;

public class ChangeTurnEvent extends Event {
    
    public Integer currentPlayerId;

    public ChangeTurnEvent(Player currentPlayer) {
        currentPlayerId = currentPlayer.getId();
    }

}
