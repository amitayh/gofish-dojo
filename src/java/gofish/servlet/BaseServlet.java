package gofish.servlet;

import gofish.game.Engine;
import gofish.game.player.Human;
import gofish.game.player.Player;
import gofish.servlet.observer.CardsDealerObserver;
import gofish.servlet.observer.EventsQueueObserver;
import java.io.IOException;
import java.util.Map;
import java.util.Observer;
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
    
    protected Engine getEngine() {
        ServletContext application = getServletContext();
        Engine engine = (Engine) application.getAttribute("game.engine");
        if (engine == null) {
            // Create game engine
            engine = new Engine();
            
            // Add observers
            Observer cardsDealer = new CardsDealerObserver();
            Observer eventsQueue = new EventsQueueObserver();
            engine.addObserver(cardsDealer);
            engine.addObserver(eventsQueue);
            
            // Save attributes to servlet context
            application.setAttribute("game.engine", engine);
            application.setAttribute("game.events", eventsQueue);
        }
        return engine;
    }
    
    protected Map<String, Player> getHumanPlayers() {
        ServletContext application = getServletContext();
        return (Map<String, Player>) application.getAttribute("game.humanPlayers");
    }
    
    protected boolean isLoggedIn(HttpSession session) {
        return (getPlayer(session) != null);
    }
    
    protected Human getPlayer(HttpSession session) {
        return (Human) session.getAttribute("player");
    }

}
