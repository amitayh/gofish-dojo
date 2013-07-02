package gofish.servlet;

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
        getServletContext().removeAttribute("game.engine");
        
        return "OK";
    }

}
