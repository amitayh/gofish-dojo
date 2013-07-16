package gofish.game.event;

import gofish.game.card.Card;
import gofish.game.player.Player;

public class CardMovedEvent extends Event {
    
    public Integer fromPlayerId;
    
    public Integer toPlayerId;
    
    public String cardName;

    public CardMovedEvent(Player from, Player to, Card card) {
        fromPlayerId = from.getId();
        toPlayerId = to.getId();
        cardName = card.getName();
    }

}
