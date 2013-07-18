package gofish.servlet;

import gofish.game.Engine;
import gofish.game.config.Config;
import gofish.game.event.Event;
import gofish.game.player.Player;
import gofish.game.player.PlayersList;
import gofish.servlet.observer.EventsQueueObserver;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@WebServlet(name = "CheckStatusServlet", urlPatterns = {"/checkStatus"})
public class CheckStatusServlet extends AjaxServlet {

    @Override
    protected Object getData(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession(true);
        Player player = getPlayer(session);
        Engine engine = getEngine();
        
        // Always include game status and player ID from session
        CheckStatusResult result = new CheckStatusResult();
        result.status = engine.getStatus();
        if (player != null) {
            result.playerId = player.getId();
        }
        
        // Add additional information as needed
        Config config = engine.getConfig();
        if (config != null && ServletUtils.getBoolean(request, "includePlayers")) {
            result.players = engine.getPlayers();
            result.totalPlayers = config.getTotalNumPlayers();
        }
        
        Integer lastEvent = ServletUtils.getInteger(request, "lastEvent");
        if (lastEvent != null) {
            EventsQueueObserver eventsQueue = getEventsQueue();
            result.events = eventsQueue.getEvents(lastEvent);
            result.totalEvents = eventsQueue.getTotalNumEvents();
        }
        
        // Save time to check timeouts
        session.setAttribute("lastSeen", new Date());
        
        return result;
    }
    
    private EventsQueueObserver getEventsQueue() {
        ServletContext application = getServletContext();
        return (EventsQueueObserver) application.getAttribute("game.events");
    }
    
    private static class CheckStatusResult {
        
        public Engine.Status status;
        
        public Integer playerId;
        
        public PlayersList players;
        
        public Integer totalPlayers;
        
        public List<Event> events;
        
        public Integer totalEvents;
        
    }
    
}
