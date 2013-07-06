package gofish.servlet.observer;

import gofish.game.event.Event;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class EventsQueueObserver implements Observer {
    
    private List<Event> queue = new ArrayList<>();

    @Override
    public void update(Observable obj, Object event) {
        queue.add((Event) event);
    }
    
    public List<Event> getEvents(int startIndex) {
        return queue.subList(startIndex, queue.size());
    }
    
    public int getTotalNumEvents() {
        return queue.size();
    }
    
}