package gofish.game.event;

import gofish.game.card.Card;
import gofish.game.player.Player;
import gofish.game.player.PlayersList;
import java.util.HashMap;
import java.util.Map;

public class StartGameEvent extends Event {
    
    public Map<Integer, Card[]> playersCards = new HashMap<>();

    public StartGameEvent(PlayersList players) {
        for (Player player : players) {
            playersCards.put(player.getId(), player.getHand().toArray());
        }
    }
    
}
