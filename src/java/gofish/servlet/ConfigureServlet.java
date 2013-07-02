package gofish.servlet;

import gofish.game.Engine;
import gofish.game.config.Config;
import gofish.game.engine.AddPlayerException;
import gofish.game.player.Computer;
import javax.servlet.ServletException;
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
        
        Engine engine = getEngine();
        if (engine.getStatus() == Engine.Status.IDLE) {
            Config config = getConfig(request);
            engine.configure(config);
            addBots(engine, config);
            getServletContext().setAttribute("game.config", config);
            result.success = true;
        }
        result.status = engine.getStatus();
        
        return result;
    }

    private Config getConfig(HttpServletRequest request) {
        Config config = new Config();
        
        config.setNumHumanPlayers(getInteger(request, "humanPlayers"));
        config.setNumComputerPlayers(getInteger(request, "computerPlayers"));
        config.setAllowMutipleRequests(getBoolean(request, "allowMultipleRequests"));
        config.setForceShowOfSeries(getBoolean(request, "forceShowOfSeries"));
        
        return config;
    }
    
    private void addBots(Engine engine, Config config) throws ServletException {
        try {
            int total = config.getNumComputerPlayers();
            for (int i = 0; i < total; i++) {
                String name = botNames[i];
                engine.addPlayer(new Computer(name));
            }
        } catch (AddPlayerException e) {
            throw new ServletException(e);
        }
    }
    
    private int getInteger(HttpServletRequest request, String name) {
        return Integer.parseInt(request.getParameter(name));
    }
    
    private boolean getBoolean(HttpServletRequest request, String name) {
        String parameter = request.getParameter(name);
        return (parameter != null) ? parameter.equals("true") : false;
    }
    
    public static class StartGameResult {
        
        public boolean success = false;
        
        public Engine.Status status;
        
    }
    
}
