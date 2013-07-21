package gofish.servlet;

import gofish.game.Engine;
import gofish.game.config.Config;
import gofish.game.event.Event;
import gofish.game.player.Player;
import gofish.game.player.PlayersList;
import java.util.List;
import java.util.Set;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@WebServlet(name = "CheckStatusServlet", urlPatterns = {"/checkStatus"})
public class CheckStatusServlet extends AjaxServlet {

    @Override
    protected Object getData(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession(true);
        Player player = getPlayer(session);
        
        Game game = getGame();
        Engine engine = game.engine;
        
        // Always include game status and player ID from session
        CheckStatusResult result = new CheckStatusResult();
        result.status = engine.getStatus();
        if (player != null) {
            result.playerId = player.getId();
        }
        
        if (!game.humanPlayers.isEmpty()) {
            result.humanPlayersNames = game.humanPlayers.keySet();
        }
        
        // Add additional information as needed
        Config config = engine.getConfig();
        if (config != null && ServletUtils.getBoolean(request, "includePlayers")) {
            result.players = engine.getPlayers();
            result.totalPlayers = config.getTotalNumPlayers();
        }
        
        Integer lastEvent = ServletUtils.getInteger(request, "lastEvent");
        if (lastEvent != null) {
            result.events = game.events.getEvents(lastEvent);
            result.totalEvents = game.events.getTotalNumEvents();
        }
        
        return result;
    }
    
    private static class CheckStatusResult {
        
        public Engine.Status status;
        
        public Integer playerId;
        
        public PlayersList players;
        
        public Set<String> humanPlayersNames;
        
        public Integer totalPlayers;
        
        public List<Event> events;
        
        public Integer totalEvents;
        
    }
    
}
