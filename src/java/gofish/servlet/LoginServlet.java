package gofish.servlet;

import gofish.game.Engine;
import gofish.game.engine.AddPlayerException;
import gofish.game.player.Human;
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
                Human player = new Human(name);
                engine.addPlayer(player);
                session.setAttribute("player", player);
                result.success = true;
            } catch (AddPlayerException e) {
                result.message = e.getMessage();
            }
        }
        
        return result;
    }
    
    public static class LoginResult {
        
        public boolean success = false;
        
        public String message;
        
    }
    
}
