package gofish.game.event;

import gofish.game.player.Player;

public class AskCardEvent extends Event {
    
    public Integer playerId;
    
    public Integer askFromPlayerId;
    
    public String cardName;

    public AskCardEvent(Player player, Player askFrom, String cardName) {
        playerId = player.getId();
        askFromPlayerId = askFrom.getId();
        this.cardName = cardName;
    }

}
