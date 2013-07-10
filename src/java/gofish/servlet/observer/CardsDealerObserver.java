package gofish.servlet.observer;

import gofish.game.Engine;
import gofish.game.card.Card;
import gofish.game.card.Deck;
import gofish.game.engine.GameStatusException;
import gofish.game.engine.StartGameException;
import gofish.game.event.PlayerJoinEvent;
import gofish.game.player.Player;
import gofish.game.player.PlayersList;
import java.util.Observable;
import java.util.Observer;

public class CardsDealerObserver implements Observer {

    @Override
    public void update(Observable obj, Object event) {
        if (event instanceof PlayerJoinEvent) {
            Engine engine = (Engine) obj;
            if (engine.gameIsFull()) {
                try {
                    dealCards(engine);
                    engine.startGame();
                } catch (GameStatusException | StartGameException e) {
                    // This shouldn't happen
                }
            }
        }
    }

    private void dealCards(Engine engine) {
        PlayersList players = engine.getPlayers();
        Deck deck = new Deck();
        deck.shuffle();
        
        int index = 0;
        while (deck.size() > 0) {
            Player player = players.get(index);
            Card card = deck.deal();
            player.addCard(card);
            index = (index + 1) % players.size();
        }
    }
    
}