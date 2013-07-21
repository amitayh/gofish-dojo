package gofish.servlet;

import gofish.game.Engine;
import gofish.game.card.Card;
import gofish.game.card.Deck;
import gofish.game.engine.AddPlayerException;
import gofish.game.player.Human;
import gofish.game.player.Player;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends AjaxServlet {

    @Override
    protected Object getData(HttpServletRequest request) throws Exception {
        Game game = getGame();
        String name = request.getParameter("name");
        HttpSession session = request.getSession(true);
        
        LoginResult result = new LoginResult();
        if (isLoggedIn(session)) {
            result.message = "Already logged in";
        } else {
            try {
                boolean dealCards = game.humanPlayers.isEmpty();
                Player player = getPlayer(name);
                Engine engine = game.engine;
                engine.addPlayer(player);
                if (engine.gameIsFull()) {
                    if (dealCards) {
                        // Game was configured manually, use a standard deck
                        dealCards(engine.getPlayers());
                    }
                    engine.startGame();
                }
                session.setAttribute("player", player);
                result.success = true;
            } catch (AddPlayerException e) {
                result.message = e.getMessage();
            }
        }
        
        return result;
    }
    
    private Player getPlayer(String name) {
        Player player;
        
        Game game = getGame();
        if (!game.humanPlayers.isEmpty()) {
            player = game.humanPlayers.remove(name);
        } else {
            player = new Human(name);
        }
        
        return player;
    }

    private void dealCards(List<Player> players) {
        Deck deck = new Deck();
        deck.shuffle();
        
        int index = 0;
        while (deck.size() > 0) {
            Player player = players.get(index);
            Card card = deck.deal();
            player.addCard(card);
            index = (index + 1) % players.size();
        }
    }
    
    public static class LoginResult {
        
        public boolean success = false;
        
        public String message;
        
    }
    
}
