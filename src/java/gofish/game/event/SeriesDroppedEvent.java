package gofish.game.event;

import gofish.game.card.Series;
import gofish.game.player.Player;

public class SeriesDroppedEvent extends Event {
    
    public Player player;
    
    public Series series;

    public SeriesDroppedEvent(Player player, Series series) {
        this.player = player;
        this.series = series;
    }

}
