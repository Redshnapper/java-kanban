package kanban;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

public class GsonUtils {
    private static Gson gson;

    private GsonUtils() {

    }

    public static Gson getGson() {
        gson = new GsonBuilder().serializeNulls().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
        return gson;
    }
}
