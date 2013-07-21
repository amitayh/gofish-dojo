package gofish.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gofish.game.card.Card;
import gofish.game.event.PlayerJoinEvent;
import gofish.game.player.Player;
import gofish.servlet.json.serializer.CardSerializer;
import gofish.servlet.json.serializer.JoinEventSerializer;
import gofish.servlet.json.serializer.PlayerSerializer;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

abstract public class AjaxServlet extends BaseServlet {
    
    final private static String CONTENT_TYPE = "application/json";
    
    final private static String ENCODING = "utf-8";
    
    private static Gson gson;
    
    static {
        // Register custom type adapters
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Card.class, new CardSerializer());
        builder.registerTypeAdapter(Player.class, new PlayerSerializer());
        builder.registerTypeAdapter(PlayerJoinEvent.class, new JoinEventSerializer());
        gson = builder.create();
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Object data;
        try {
            data = getData(request);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            data = e;
        }
        
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(ENCODING);
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(data));
        out.flush();
        out.close();
        
    }
    
    abstract protected Object getData(HttpServletRequest request) throws Exception;
    
    public static Gson getGson() {
        return gson;
    }

}
