package gofish.servlet;

import gofish.game.Engine;
import gofish.game.player.PlayersList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@WebServlet(name = "CheckStatusServlet", urlPatterns = {"/checkStatus"})
public class CheckStatusServlet extends AjaxServlet {

    @Override
    protected Object getData(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession(true);
        return new CheckStatusResult(getEngine(), session);
    }
    
    private static class CheckStatusResult {
        
        public Engine.Status status;
        
        public Integer playerId;
        
        public PlayersList players;
        
        public int totalPlayers;
        
        public CheckStatusResult(Engine engine, HttpSession session) {
            status = engine.getStatus();
            playerId = (Integer) session.getAttribute("playerId");
            if (status != Engine.Status.IDLE) {
                players = engine.getPlayers();
                totalPlayers = engine.getConfig().getTotalNumPlayers();
            }
        }
        
    }
    
}
