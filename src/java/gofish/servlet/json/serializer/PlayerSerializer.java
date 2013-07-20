package gofish.servlet.json.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import gofish.game.player.Player;
import java.lang.reflect.Type;

public class PlayerSerializer implements JsonSerializer<Player> {

    @Override
    public JsonElement serialize(Player player, Type type, JsonSerializationContext jsc) {
        JsonObject element = new JsonObject();
        element.add("id", jsc.serialize(player.getId()));
        element.add("name", jsc.serialize(player.getName()));
        return element;
    }

}
