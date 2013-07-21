package gofish.servlet;

import gofish.game.Engine;
import gofish.game.engine.AddPlayerException;
import gofish.game.player.Human;
import gofish.game.player.Player;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends AjaxServlet {

    @Override
    protected Object getData(HttpServletRequest request) throws Exception {
        Engine engine = getEngine();
        String name = request.getParameter("name");
        HttpSession session = request.getSession(true);
        
        LoginResult result = new LoginResult();
        if (isLoggedIn(session)) {
            result.message = "Already logged in";
        } else {
            try {
                Player player = getPlayer(name);
                engine.addPlayer(player);
                session.setAttribute("player", player);
                result.success = true;
            } catch (AddPlayerException e) {
                result.message = e.getMessage();
            }
        }
        
        return result;
    }
    
    private Player getPlayer(String name) {
        Player player;
        
        Map<String, Player> humanPlayers = getHumanPlayers();
        if (humanPlayers != null) {
            player = humanPlayers.get(name);
        } else {
            player = new Human(name);
        }
        
        return player;
    }
    
    public static class LoginResult {
        
        public boolean success = false;
        
        public String message;
        
    }
    
}
