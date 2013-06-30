package gofish.servlet;

import gofish.game.Engine;
import gofish.game.exception.DuplicateNameException;
import gofish.game.exception.EmptyNameException;
import gofish.game.exception.TooManyPlayersException;
import gofish.game.player.Human;
import gofish.game.player.Player;
import gofish.game.player.PlayersList;
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
//        if (isLoggedIn(session)) {
//            result.message = "Already logged in";
//        } else {
            Player player = new Human(name);
            try {
                engine.addPlayer(player);
                int playerId = player.getId();
                session.setAttribute("playerId", playerId);
                result.success = true;
                result.playerId = playerId;
                result.players = engine.getPlayers();
                result.totalPlayers = engine.getConfig().getTotalNumPlayers();
            } catch (EmptyNameException e) {
                result.message = "Empty player name";
            } catch (DuplicateNameException e) {
                result.message = "Duplicate player name '" + name + "'";
            } catch (TooManyPlayersException e) {
                result.message = "Game is full";
            }
//        }
        
        return result;
    }
    
    public static class LoginResult {
        
        public boolean success = false;
        
        public int playerId;
        
        public String message;
        
        public PlayersList players;
        
        public int totalPlayers;
        
    }
    
}
