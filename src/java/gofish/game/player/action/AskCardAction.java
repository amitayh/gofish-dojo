package gofish.game.player.action;

import gofish.game.player.Player;

public class AskCardAction extends Action {
    
    final private Player askFrom;
    
    final private String cardName;

    public AskCardAction(Player player, Player askFrom, String cardName) {
        super(player);
        this.askFrom = askFrom;
        this.cardName = cardName;
    }

    public Player getAskFrom() {
        return askFrom;
    }

    public String getCardName() {
        return cardName;
    }

}
