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
    
    private final Type type;
    
    private final String name;
    
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
    
    public void quitGame() {
        playing = false;
        hand.clear();
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
    
    public void addCard(Card card) {
        if (!hand.contains(card)) {
            hand.add(card);
        }
    }
    
    public void removeCard(Card card) {
        if (!hand.contains(card)) {
            throw new NoSuchElementException();
        }
        hand.remove(card);
    }
    
    public Series getCompleteSeries() {
        for (String property : hand.properties()) {
            if (hand.seriesSize(property) == Engine.COMPLETE_SERIES_SIZE) {
                Set<Card> cards = hand.getByProperty(property);
                return new Series(property, cards);
            }
        }
        return null;
    }
    
    public Collection<Series> getAllCompleteSeries() {
        return completeSeries;
    }
    
    public boolean hasCompleteSeries() {
        return (getCompleteSeries() != null);
    }
    
    public void dropCompleteSeries(Series series) {
        hand.removeAll(series.getCards());
        completeSeries.add(series);
    }

}
