package gofish.game.player;

import gofish.game.Engine;
import gofish.game.event.ChangeTurnEvent;
import java.util.Observable;
import java.util.Observer;

public class ComputerPlayerObserver implements Observer {

    @Override
    public void update(Observable obj, Object arg) {
        if (arg instanceof ChangeTurnEvent) {
            Engine engine = (Engine) obj;
            ChangeTurnEvent event = (ChangeTurnEvent) arg;
            Player player = event.currentPlayer;
            if (player.isComputer()) {
                ((Computer) player).play(engine);
            }
        }
    }

}
