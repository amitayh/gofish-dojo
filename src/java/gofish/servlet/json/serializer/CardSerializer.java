package gofish.servlet.json.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import gofish.game.card.Card;
import java.lang.reflect.Type;

public class CardSerializer implements JsonSerializer<Card> {

    @Override
    public JsonElement serialize(Card card, Type type, JsonSerializationContext jsc) {
        JsonObject element = new JsonObject();
        element.add("id", jsc.serialize(card.getId()));
        element.add("name", jsc.serialize(card.getName()));
        return element;
    }

}
