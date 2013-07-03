package gofish.servlet;

import gofish.game.Engine;
import gofish.game.event.Event;
import gofish.game.player.PlayersList;
import java.util.Date;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@WebServlet(name = "CheckStatusServlet", urlPatterns = {"/checkStatus"})
public class CheckStatusServlet extends AjaxServlet {

    @Override
    protected Object getData(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession(true);
        Engine engine = getEngine();
        
        // Always include game status and player ID from session
        CheckStatusResult result = new CheckStatusResult();
        result.status = engine.getStatus();
        result.playerId = (Integer) session.getAttribute("playerId");
        
        // Add additional information as needed
        if (ServletUtils.getBoolean(request, "includePlayers")) {
            result.players = engine.getPlayers();
            result.totalPlayers = engine.getConfig().getTotalNumPlayers();
        }
        
        Integer lastEvent = ServletUtils.getInteger(request, "lastEvent");
        if (lastEvent != null) {
            result.events = engine.getEvents(lastEvent);
            result.totalEvents = engine.getTotalNumEvents();
        }
        
        // Save time to check timeouts
        session.setAttribute("lastSeen", new Date());
        
        return result;
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
