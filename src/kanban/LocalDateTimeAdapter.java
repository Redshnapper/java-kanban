package kanban;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter implements JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString(), formatter);
    }

    @Override
    public JsonElement serialize(LocalDateTime dateTime, Type type, JsonSerializationContext context) {
        return context.serialize(dateTime.format(formatter));
    }
}
