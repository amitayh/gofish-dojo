package gofish.servlet;

import gofish.game.Engine;
import gofish.game.card.Card;
import gofish.game.card.CardsCollection;
import gofish.game.card.Series;
import gofish.game.engine.GameStatusException;
import gofish.game.engine.PlayerActionException;
import gofish.game.player.Player;
import gofish.game.player.action.Action;
import gofish.game.player.action.AskCardAction;
import gofish.game.player.action.DropSeriesAction;
import gofish.game.player.action.QuitGameAction;
import gofish.game.player.action.SkipTurnAction;
import java.util.Set;
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
                Game game = getGame();
                Action action = createAction(player, request);
                game.engine.performPlayerAction(action);
                result.success = true;
            } catch (GameStatusException | PlayerActionException e) {
                result.message = e.getMessage();
            }
        }
        
        return result;
    }

    private Action createAction(Player player, HttpServletRequest request)
            throws PlayerActionException {
        
        Action action = null;
        
        switch (request.getParameter("action")) {
            case "askCard":
                action = createAskCardAction(player, request);
                break;
                
            case "dropSeries":
                action = createDropSeriesAction(player, request);
                break;
                
            case "skipTurn":
                action = new SkipTurnAction(player);
                break;
            
            case "quitGame":
                action = new QuitGameAction(player);
                break;
        }
        
        return action;
    }
    
    private Action createAskCardAction(Player player, HttpServletRequest request) {
        Game game = getGame();
        Integer playerId = new Integer(request.getParameter("askFrom"));
        Player askFrom = game.engine.getPlayer(playerId);
        String cardName = request.getParameter("cardName");
        
        return new AskCardAction(player, askFrom, cardName);
    }

    private Action createDropSeriesAction(Player player, HttpServletRequest request)
            throws PlayerActionException {
        
        Series series = null;
        
        CardsCollection hand = player.getHand();
        String[] cardNames = request.getParameter("cards").split(",");
        Set<Card> selectedCards = hand.getAllCards(cardNames);
        for (String property : hand.properties()) {
            Set<Card> cards = hand.getByProperty(property);
            if (cards.equals(selectedCards)) {
                series = new Series(property, cards);
                break;
            }
        }
        
        if (series == null) {
            throw new PlayerActionException("Can't drop series - invalid request");
        }
        
        return new DropSeriesAction(player, series);
    }
    
    public static class PerformPlayerActionResult {
        
        public boolean success = false;
        
        public String message;
        
    }
    
}
