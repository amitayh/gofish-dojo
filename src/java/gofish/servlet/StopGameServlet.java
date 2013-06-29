package gofish.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

@WebServlet(name = "StopGameServlet", urlPatterns = {"/stopGame"})
public class StopGameServlet extends AjaxServlet {

    @Override
    protected Object getData(HttpServletRequest request) throws Exception {
        getServletContext().removeAttribute("game.engine");
        return "OK";
    }

}
