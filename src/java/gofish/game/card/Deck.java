package gofish.game.card;

import gofish.game.player.Player;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Standard deck implementation
 */
public class Deck {
    
    final private static String[] RANKS = {
        "2", "3", "4", "5", "6", "7", "8",
        "9", "10", "J", "Q", "K", "A"
    };
    
    final private static String[] SUITS = {
        "Diamonds", "Clubs", "Hearts", "Spades"
    };
    
    private LinkedList<Card> cards = new LinkedList<>();
    
    public Deck() {
        for (String rank : RANKS) {
            for (String suit : SUITS) {
                String name = rank + " of " + suit;
                cards.add(new Card(name, rank));
            }
        }
    }
    
    public Card deal() {
        return cards.pop();
    }
    
    public void shuffle() {
        Collections.shuffle(cards);
    }

    public int size() {
        return cards.size();
    }
    
    public static void deal(List<Player> players) {
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
