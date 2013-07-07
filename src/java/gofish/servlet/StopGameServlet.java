package gofish.servlet;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@WebServlet(name = "StopGameServlet", urlPatterns = {"/stopGame"})
public class StopGameServlet extends AjaxServlet {

    @Override
    protected Object getData(HttpServletRequest request) throws Exception {
        // Destroy session
        HttpSession session = request.getSession();
        if (session != null) {
            session.invalidate();
        }
        // Delete game
        ServletContext application = getServletContext();
        application.removeAttribute("game.engine");
        application.removeAttribute("game.events");
        
        return "OK";
    }

}
