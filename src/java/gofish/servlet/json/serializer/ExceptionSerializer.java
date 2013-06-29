package gofish.servlet.json.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class ExceptionSerializer implements JsonSerializer<Exception> {

    @Override
    public JsonElement serialize(Exception exception, Type type, JsonSerializationContext jsc) {
        return jsc.serialize(exception.getMessage());
    }

}
