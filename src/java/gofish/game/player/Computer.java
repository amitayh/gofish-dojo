package gofish.game.player;

import gofish.game.Engine;
import gofish.game.card.Card;
import gofish.game.card.CardsCollection;
import gofish.game.card.Series;
import gofish.game.engine.GameStatusException;
import gofish.game.engine.PlayerActionException;
import gofish.game.player.action.Action;
import gofish.game.player.action.AskCardAction;
import gofish.game.player.action.DropSeriesAction;
import gofish.game.player.action.QuitGameAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Computer extends Player {
    
    private static Random randomGenerator = new Random();

    public Computer(String name) {
        super(Type.COMPUTER, name);
    }

    public void play(Engine engine) {
        Action action;        
        try {
            dropAllCompleteSeries(engine);
            // TODO: check if still playing after dropping series
            
            Player askFrom = playerToAsk(engine);
            String cardName = cardNameToAsk(engine);
            action = new AskCardAction(this, askFrom, cardName);
        } catch (Exception e) {
            action = new QuitGameAction(this, e.getMessage());
        }
        
        try {
            engine.performPlayerAction(action);
        } catch (GameStatusException | PlayerActionException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void dropAllCompleteSeries(Engine engine) throws GameStatusException, PlayerActionException {
        Series series = getCompleteSeries();
        while (series != null) {
            Action action = new DropSeriesAction(this, series);
            engine.performPlayerAction(action);
            series = getCompleteSeries();
        }
    }

    private Player playerToAsk(Engine engine) throws Exception {
        List<Player> otherPlayers = otherPlayers(engine.getPlayers());
        if (otherPlayers.isEmpty()) {
            throw new Exception("No other players");
        }
        int randomIndex = randomGenerator.nextInt(otherPlayers.size());
        return otherPlayers.get(randomIndex);
    }

    private String cardNameToAsk(Engine engine) throws Exception {
        CardsCollection hand = getHand();
        for (String property : hand.properties()) {
            Set<Card> cards = engine.findCards(property);
            if (cards.size() > hand.seriesSize(property)) {
                for (Card card : cards) {
                    if (!hand.contains(card)) {
                        return card.getName();
                    }
                }
            }
        }
        throw new Exception("No cards left");
    }
    
    /**
     * @param allPlayers all players in game
     * @return a list of other players (excluding self) that are still playing
     */
    private List<Player> otherPlayers(Collection<Player> allPlayers) {
        List<Player> otherPlayers = new ArrayList<>(allPlayers.size());
        for (Player player : allPlayers) {
            if (player != this && player.isPlaying()) {
                otherPlayers.add(player);
            }
        }
        return otherPlayers;
    }
    
}
