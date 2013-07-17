package gofish.game.event;

import gofish.game.player.Player;

public class GoFishEvent extends Event {
    
    public Integer player1Id;
    
    public Integer player2Id;

    public GoFishEvent(Player player1, Player player2) {
        player1Id = player1.getId();
        player2Id = player2.getId();
    }

}
