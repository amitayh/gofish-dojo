package gofish.servlet;

import gofish.game.Engine;
import gofish.game.config.Config;
import gofish.game.player.Computer;
import gofish.game.player.Player;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

@WebServlet(name = "CongirueServlet", urlPatterns = {"/configure"})
public class ConfigureServlet extends AjaxServlet {
    
    final private static String[] botNames = {
        "Leonardo", "Raphael", "Donatello", "Michelangelo", "Splinter"
    };

    @Override
    protected Object getData(HttpServletRequest request) throws Exception {
        
        StartGameResult result = new StartGameResult();
        
        Game game = getGame();
        Engine engine = game.engine;
        if (engine.getStatus() == Engine.Status.IDLE) {
            Config config = getConfig(request);
            List<Player> bots = getBots(config);
            game.configure(config, bots);
            result.success = true;
        }
        result.status = engine.getStatus();
        
        return result;
    }

    private Config getConfig(HttpServletRequest request) {
        Config config = new Config();
        
        config.setNumHumanPlayers(ServletUtils.getInteger(request, "humanPlayers"));
        config.setNumComputerPlayers(ServletUtils.getInteger(request, "computerPlayers"));
        config.setAllowMutipleRequests(ServletUtils.getBoolean(request, "allowMultipleRequests"));
        config.setForceShowOfSeries(ServletUtils.getBoolean(request, "forceShowOfSeries"));
        
        return config;
    }

    private List<Player> getBots(Config config) {
        List<Player> bots = new LinkedList<>();
        
        int numBots = config.getNumComputerPlayers();
        for (int i = 0; i < numBots; i++) {
            String name = botNames[i];
            bots.add(new Computer(name));
        }
        
        return bots;
    }
    
    public static class StartGameResult {
        
        public boolean success = false;
        
        public Engine.Status status;
        
    }
    
}
