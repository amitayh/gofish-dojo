package gofish.game.player.action;

import gofish.game.card.Series;
import gofish.game.player.Player;

public class DropSeriesAction extends Action {
    
    final private Series series;

    public DropSeriesAction(Player player, Series sereis) {
        super(player);
        this.series = sereis;
    }

}
