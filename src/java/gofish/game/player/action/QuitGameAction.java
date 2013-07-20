package gofish.game.player.action;

import gofish.game.player.Player;

public class QuitGameAction extends Action {
    
    final private String reason;
    
    public QuitGameAction(Player player, String reason) {
        super(player);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public QuitGameAction(Player player) {
        this(player, "Quit");
    }

}
