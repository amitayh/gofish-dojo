package gofish.servlet;

import gofish.game.Engine;
import gofish.game.player.Human;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

abstract public class BaseServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    @Override
    public String getServletInfo() {
        return "GoFish Servlet";
    }
    
    abstract protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException;
    
    protected Game getGame() {
        ServletContext application = getServletContext();
        Game game = (Game) application.getAttribute("game");
        if (game == null) {
            Engine engine = new Engine();
            game = new Game(engine);
            application.setAttribute("game", game);
        }
        return game;
    }
    
    protected boolean isLoggedIn(HttpSession session) {
        return (getPlayer(session) != null);
    }
    
    protected Human getPlayer(HttpSession session) {
        Human player = (Human) session.getAttribute("player");
        if (player != null && !player.isPlaying()) {
            session.invalidate();
            player = null;
        }
        return player;
    }

}
