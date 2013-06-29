package gofish.servlet;

import gofish.game.Engine;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
            engine = new Engine();
            application.setAttribute("game.engine", engine);
        }
        return engine;
    }

}
