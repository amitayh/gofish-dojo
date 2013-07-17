package gofish.servlet.json.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import gofish.game.event.PlayerJoinEvent;
import java.lang.reflect.Type;

public class JoinEventSerializer implements JsonSerializer<PlayerJoinEvent> {

    @Override
    public JsonElement serialize(PlayerJoinEvent event, Type type, JsonSerializationContext jsc) {
        JsonObject player = new JsonObject();
        player.add("id", jsc.serialize(event.player.getId()));
        player.add("name", jsc.serialize(event.player.getName()));
        player.add("hand", jsc.serialize(event.player.getHand()));
        
        JsonObject element = new JsonObject();
        element.add("type", jsc.serialize(event.type));
        element.add("player", player);
        
        return element;
    }

}
