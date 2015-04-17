package com.bingcrowsby.byoadventure.Controller;

import com.bingcrowsby.byoadventure.Model.AdventureObject;
import com.bingcrowsby.byoadventure.Model.DirectionsObject;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by kevinfinn on 2/5/15.
 */
public class CustomGson {
    static Gson gson = null;

    public static Gson getGson(){
        if(gson == null) {
            gson = new Gson();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Marker.class, new MarkerSerializer());
            gsonBuilder.registerTypeAdapter(Marker.class, new MarkerDeserializer());
            gsonBuilder.registerTypeAdapter(LatLng.class, new LatLngSerializer());
            gsonBuilder.registerTypeAdapter(LatLng.class, new LatLngDeserializer());
            gsonBuilder.registerTypeAdapter(LatLngBounds.class, new LatLngBoundsSerializer());
            gsonBuilder.registerTypeAdapter(LatLngBounds.class, new LatLngBoundsDeserializer());
            gsonBuilder.registerTypeAdapter(MarkerOptions.class, new MarkerOptionsSerializer());
            gsonBuilder.registerTypeAdapter(MarkerOptions.class, new MarkerOptionsDeserializer());
            gsonBuilder.setExclusionStrategies(new ExclStrategy());
            gson = gsonBuilder.create();
        }

        return gson;
    }

    public static class ExclStrategy implements ExclusionStrategy {

        public boolean shouldSkipClass(Class<?> arg0) {
            return false;
        }

        public boolean shouldSkipField(FieldAttributes f) {
            if(f.getDeclaringClass() == AdventureObject.class)
                if(f.getName().equals("id") || f.getName().equals("mAdventureTitle") || f.getName().equals("listener"))
                    return true;

            if(f.getDeclaringClass() == DirectionsObject.class)
                if(f.getName().equals("directionPoints"))
                    return true;

            return false;
        }
    }

    private static class LatLngBoundsSerializer implements JsonSerializer<LatLngBounds> {

        @Override
        public JsonElement serialize(LatLngBounds src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("ne-lat",src.northeast.latitude);
            object.addProperty("ne-lng",src.northeast.longitude);
            object.addProperty("sw-lat",src.southwest.latitude);
            object.addProperty("sw-lng",src.southwest.longitude);

            return object;
        }
    }
    private static class LatLngBoundsDeserializer implements JsonDeserializer<LatLngBounds> {

        @Override
        public LatLngBounds deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            LatLng ne = new LatLng(json.getAsJsonObject().get("ne-lat").getAsDouble(),json.getAsJsonObject().get("ne-lng").getAsDouble());
            LatLng sw = new LatLng(json.getAsJsonObject().get("sw-lat").getAsDouble(),json.getAsJsonObject().get("sw-lng").getAsDouble());

            return new LatLngBounds.Builder().include(ne).include(sw).build();
        }
    }
    private static class MarkerOptionsSerializer implements JsonSerializer<MarkerOptions>{

        @Override
        public JsonElement serialize(MarkerOptions src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("lat",src.getPosition().latitude);
            object.addProperty("lng",src.getPosition().longitude);
            return object;
        }
    }

    private static class MarkerOptionsDeserializer implements JsonDeserializer<MarkerOptions>{

        @Override
        public MarkerOptions deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new MarkerOptions().position(new LatLng(json.getAsJsonObject().get("lat").getAsLong(),json.getAsJsonObject().get("lng").getAsLong()));
        }
    }

    private static class LatLngSerializer implements JsonSerializer<LatLng>{

        @Override
        public JsonElement serialize(LatLng src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("lat",src.latitude);
            object.addProperty("lng",src.longitude);
            return object;
        }
    }
    private static class LatLngDeserializer implements JsonDeserializer<LatLng>{

        @Override
        public LatLng deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new LatLng(json.getAsJsonObject().get("lat").getAsDouble(),json.getAsJsonObject().get("lng").getAsDouble());
        }
    }

    private static class MarkerSerializer implements JsonSerializer<Marker>{

        @Override
        public JsonElement serialize(Marker src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("lat",src.getPosition().latitude);
            object.addProperty("lng",src.getPosition().longitude);
            return object;
        }
    }
    private static class MarkerDeserializer implements JsonDeserializer<Marker>{

        @Override
        public Marker deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return null;
        }
    }
}
