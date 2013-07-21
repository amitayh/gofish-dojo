package gofish.servlet;

import com.google.gson.Gson;
import gofish.game.Engine;
import gofish.game.config.Config;
import gofish.game.config.ValidationException;
import gofish.game.config.XMLConfigFactory;
import gofish.game.engine.AddPlayerException;
import gofish.game.engine.GameStatusException;
import gofish.game.player.Computer;
import gofish.game.player.Human;
import gofish.game.player.Player;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/uploadXml")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadXmlServlet extends BaseServlet {
    
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        UploadXmlResult result = new UploadXmlResult();
        
        Game game = getGame();
        if (game.engine.getStatus() == Engine.Status.IDLE) {
            File file = getUploadedFile(request);
            try {
                XMLConfigFactory factory = new Factory();
                factory.validate(file);
                
                Config config = factory.getConfig();
                List<Player> players = factory.getPlayers();
                game.configure(config, players);
                result.success = true;
                
            } catch (ValidationException | GameStatusException | AddPlayerException e) {
                result.message = e.getMessage();
            } finally {
                // Delete temp file
                if (!file.delete()) {
                    file.deleteOnExit();
                }
            }
            
        } else {
            result.message = "Game was configured another player";
        }
        
        printOutput(response, result);
    }
    
    private File getUploadedFile(HttpServletRequest request)
            throws ServletException, IOException {
        
        File file = null;
        
        for (Part part : request.getParts()) {
            file = File.createTempFile("gofish-xml-", ".tmp");
            part.write(file.getAbsolutePath());
            part.delete();
            break;
        }
        
        if (file == null) {
            throw new ServletException("File was not uploaded");
        }
        
        return file;
    }

    private void printOutput(HttpServletResponse response, UploadXmlResult result)
            throws IOException {
        
        Gson gson = AjaxServlet.getGson();
        String message = gson.toJson(result);
        
        // Use HTML5's postMessage API to send the result
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.format("<script>parent.postMessage(%s, location.origin);</script>", message);
        out.flush();
    }
    
    private static class Factory extends XMLConfigFactory {

        @Override
        protected Player createPlayer(Player.Type type, String name) {
            Player player;
            
            switch (type) {
                case COMPUTER:
                    player = new Computer(name);
                    break;
                case HUMAN:
                    player = new Human(name);
                    break;
                default:
                    throw new RuntimeException("Unexpected player type");
            }
            
            return player;
        }
        
    }
    
    public static class UploadXmlResult {
        
        public String type = "UploadXmlResult";
        
        public boolean success = false;
        
        public String message;
        
    }
    
}
