package gofish.servlet;

import gofish.game.card.Card;
import gofish.game.card.CardsCollection;
import gofish.game.player.Player;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@WebServlet(name="GetAvailableCardsServlet", urlPatterns={"/getAvailableCards"})
public class GetAvailableCardsServlet extends AjaxServlet {

    @Override
    protected Object getData(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        Player player = getPlayer(session);
        String[] result = new String[0];
        
        if (player != null) {
            Game game = getGame();
            List<String> availableCards = new LinkedList<>();
            CardsCollection hand = player.getHand();
            for (String property : hand.properties()) {
                Set<Card> cards = game.engine.findCards(property);
                if (cards.size() > hand.seriesSize(property)) {
                    for (Card card : cards) {
                        if (!hand.contains(card)) {
                            availableCards.add(card.getName());
                        }
                    }
                }
            }
            result = availableCards.toArray(new String[availableCards.size()]);
        }
        
        return result;
    }
   
}
