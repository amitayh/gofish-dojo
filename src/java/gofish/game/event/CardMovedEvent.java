package gofish.game.event;

import gofish.game.card.Card;
import gofish.game.player.Player;

public class CardMovedEvent extends Event {
    
    public Player from;
    
    public Player to;
    
    public Card card;

    public CardMovedEvent(Player from, Player to, Card card) {
        this.from = from;
        this.to = to;
        this.card = card;
    }

}
