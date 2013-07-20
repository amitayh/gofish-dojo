package gofish.game.event;

import gofish.game.player.Player;

public class GameOverEvent extends Event {
    
    public Player winner;
    
    public int winnerCompleteSeries;

    public GameOverEvent(Player winner) {
        this.winner = winner;
        winnerCompleteSeries = winner.getAllCompleteSeries().size();
    }

}
