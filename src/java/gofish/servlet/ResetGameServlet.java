package gofish.servlet;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ResetGameServlet", urlPatterns = {"/resetGame"})
public class ResetGameServlet extends AjaxServlet {

    @Override
    protected Object getData(HttpServletRequest request) throws Exception {
        // Destroy session
        HttpSession session = request.getSession();
        if (session != null) {
            session.invalidate();
        }
        
        // Delete game
        ServletContext application = getServletContext();
        application.removeAttribute("game");
        
        return true;
    }

}
