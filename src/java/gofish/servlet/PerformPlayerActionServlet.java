package gofish.servlet;

import gofish.game.Engine;
import gofish.game.engine.GameStatusException;
import gofish.game.engine.PlayerActionException;
import gofish.game.player.Player;
import gofish.game.player.action.Action;
import gofish.game.player.action.AskCardAction;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@WebServlet(name="PerformPlayerActionServlet", urlPatterns={"/performPlayerAction"})
public class PerformPlayerActionServlet extends AjaxServlet {

    @Override
    protected Object getData(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        Player player = getPlayer(session);
        PerformPlayerActionResult result = new PerformPlayerActionResult();
        
        if (player != null) {
            try {
                Engine engine = getEngine();
                Action action = createAction(engine, player, request);
                engine.performPlayerAction(action);
                result.success = true;
            } catch (GameStatusException | PlayerActionException e) {
                result.message = e.getMessage();
            }
        }
        
        return result;
    }

    private Action createAction(Engine engine, Player player, HttpServletRequest request) {
        Action action = null;
        
        String actionName = request.getParameter("action");
        if ("askCard".equals(actionName)) {
            Integer playerId = new Integer(request.getParameter("askFrom"));
            Player askFrom = engine.getPlayer(playerId);
            String cardName = request.getParameter("cardName");
            action = new AskCardAction(player, askFrom, cardName);
        }
        
        return action;
    }
    
    public static class PerformPlayerActionResult {
        
        public boolean success = false;
        
        public String message;
        
    }
    
}
