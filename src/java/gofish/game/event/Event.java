package gofish.game.event;

abstract public class Event {
    
    public String type;
    
    public Event() {
        type = getClass().getSimpleName();
    }

}
