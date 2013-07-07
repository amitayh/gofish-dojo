package gofish.game.player;

import gofish.game.Engine;
import gofish.game.card.Card;
import gofish.game.card.CardsCollection;
import gofish.game.card.Series;
import java.util.Collection;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

abstract public class Player {
    
    public enum Type {COMPUTER, HUMAN};
    
    private static int numInstances = 0;
    
    private final int id;
    
    private Type type;
    
    private String name;
    
    private boolean playing = true;
    
    private CardsCollection hand = new CardsCollection();
    
    private Collection<Series> completeSeries = new LinkedList<>();

    public Player(Type type, String name) {
        id = ++numInstances;
        this.type = type;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }
    
    public boolean isPlaying() {
        return playing;
    }
    
    public boolean isHuman() {
        return (type == Type.HUMAN);
    }
    
    public boolean isComputer() {
        return (type == Type.COMPUTER);
    }
    
    public CardsCollection getHand() {
        return hand;
    }
    
    /**
     * Add card to hand
     * 
     * @param card
     * @return if the added card completed a series, the series is returned, null otherwise
     */
    public Series addCard(Card card) {
        if (!hand.contains(card)) {
            hand.add(card);
            return checkComplete();
        }
        return null;
    }
    
    public void removeCard(Card card) {
        if (!hand.contains(card)) {
            throw new NoSuchElementException();
        }
        hand.remove(card);
    }
    
    /**
     * Check if there's a complete series in hand
     * 
     * @return the completed series if it exists, null otherwise
     */
    private Series checkComplete() {
        for (String property : hand.properties()) {
            if (hand.seriesSize(property) == Engine.COMPLETE_SERIES_SIZE) {
                Set<Card> cards = hand.removeByProperty(property);
                Series series = new Series(property, cards);
                completeSeries.add(series);
                return series;
            }
        }
        return null;
    }

}
