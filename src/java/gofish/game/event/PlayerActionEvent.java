package gofish.game.event;

import gofish.game.player.action.Action;

public class PlayerActionEvent extends Event {
    
    public Action action;

    public PlayerActionEvent(Action action) {
        this.action = action;
    }

}
