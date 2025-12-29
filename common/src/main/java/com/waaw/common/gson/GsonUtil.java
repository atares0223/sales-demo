package com.waaw.common.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.bind.DateTypeAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

public class GsonUtil {
    public static final Gson gson;

    public static String getStringValue(JsonObject jsonObject, String key) {
        JsonElement orderDateJE = jsonObject.get(key);

        return orderDateJE != null && orderDateJE.isJsonPrimitive() ? orderDateJE.getAsString() : null;
    }

    private GsonUtil() {
    }

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new DateTypeAdapter());
        builder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter());
        builder.registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter());
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        gson = builder.create();
    }
}
