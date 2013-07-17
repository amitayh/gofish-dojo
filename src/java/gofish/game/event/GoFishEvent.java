package gofish.game.event;

import gofish.game.player.Player;

public class GoFishEvent extends Event {
    
    public Player player1;
    
    public Player player2;

    public GoFishEvent(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

}
