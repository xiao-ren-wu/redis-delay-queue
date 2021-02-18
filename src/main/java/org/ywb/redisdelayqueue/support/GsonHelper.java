package org.ywb.redisdelayqueue.support;

import com.google.gson.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author yuwenbo1
 * @date 2021/1/19 18:49
 * @since 1.0.0
 */
public class GsonHelper {

    private static final Gson GSON;

    private static final String DEFAULT_DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";

    private static final String DEFAULT_DATE_FORMATTER = "yyyy-MM-dd";

    private static final String DEFAULT_TIME_FORMATTER = "HH:mm:ss";

    static {
        GSON = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (localDateTime, typeOfSrc, context) -> {
                    if (Objects.isNull(localDateTime)) {
                        return new JsonPrimitive("");
                    }
                    return new JsonPrimitive(localDateTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMATTER)));
                })
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json1, typeOfT, context) -> {
                    String string = json1.getAsJsonPrimitive().getAsString();
                    if (Strings.isNullOrEmpty(string)) {
                        return null;
                    }
                    return LocalDateTime.parse(string, DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMATTER));
                })
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (localDate, type, jsonSerializationContext) -> {
                    if (Objects.isNull(localDate)) {
                        return new JsonPrimitive("");
                    }
                    return new JsonPrimitive(localDate.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMATTER)));
                })
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> {
                    String string = json.getAsJsonPrimitive().getAsString();
                    if (Strings.isNullOrEmpty(string)) {
                        return null;
                    }
                    return LocalDate.parse(string, DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMATTER));
                })
                .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (localTime, type, jsonSerializationContext) -> {
                    if (Objects.isNull(localTime)) {
                        return new JsonPrimitive("");
                    }
                    return new JsonPrimitive(localTime.format(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMATTER)));
                })
                .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, typeOfT, context) -> {
                    String string = json.getAsJsonPrimitive().getAsString();
                    if (Strings.isNullOrEmpty(string)) {
                        return null;
                    }
                    return LocalTime.parse(string, DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMATTER));
                })
                .create();
    }

    public static <T> T toObj(String json, Class<T> tClass) {
        return GSON.fromJson(json, tClass);
    }

    public static String toJson(Object o) {
        return GSON.toJson(o);
    }
}
