package gofish.game.event;

import gofish.game.player.Player;

public class AskCardEvent extends Event {
    
    public Player player;
    
    public Player askFrom;
    
    public String cardName;

    public AskCardEvent(Player player, Player askFrom, String cardName) {
        this.player = player;
        this.askFrom = askFrom;
        this.cardName = cardName;
    }

}
