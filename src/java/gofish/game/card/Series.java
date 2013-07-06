package gofish.game.card;

import java.util.Set;

public class Series {
    
    private String property;
    
    private Set<Card> cards;

    public Series(String property, Set<Card> cards) {
        this.property = property;
        this.cards = cards;
    }

    public String getProperty() {
        return property;
    }

    public Set<Card> getCards() {
        return cards;
    }

    @Override
    public String toString() {
        return "Series{property=" + property + "}";
    }

}
