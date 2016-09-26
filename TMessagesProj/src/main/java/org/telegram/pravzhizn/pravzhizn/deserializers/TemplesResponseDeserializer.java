package org.telegram.pravzhizn.pravzhizn.deserializers;

import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.telegram.pravzhizn.pravzhizn.RemoteChurch;
import org.telegram.pravzhizn.pravzhizn.RemoteChurch.ImageObject;
import org.telegram.pravzhizn.pravzhizn.RemoteChurch.TechranchChatObject;
import org.telegram.pravzhizn.pravzhizn.responses.TemplesResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlad on 6/3/16.
 */
public class TemplesResponseDeserializer implements JsonDeserializer<TemplesResponse> {

    @Override
    public TemplesResponse deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        TemplesResponse response = new TemplesResponse();

        response.success = json.getAsJsonObject().get("success").getAsBoolean();

        JsonObject data = json.getAsJsonObject().get("data").getAsJsonObject();

        response.count = data.get("count").getAsInt();
        response.items = new ArrayList<>();

        JsonArray items = data.getAsJsonArray("items");
        if (items == null) {
            return response;
        }

        for (JsonElement element: items) {
            response.items.add(jsonToRemoteChurch(element));
        }

        return response;
    }

    public static RemoteChurch jsonToRemoteChurch(final JsonElement element) {
        JsonObject churchObject = element.getAsJsonObject();

        RemoteChurch item = new RemoteChurch();
        item.id = longOrDefault(churchObject, "id");
        item.title = stringOrDefault(churchObject, "title");
        item.content = stringOrDefault(churchObject, "content");
        item.contact_phones = stringOrDefault(churchObject, "contact_phones");
        item.contact_emails = stringOrDefault(churchObject, "contact_emails");
        item.site = stringOrDefault(churchObject, "site");
        item.address = stringOrDefault(churchObject, "address");
        item.map_lat = doubleOrDefault(churchObject, "map_lat");
        item.map_lng = doubleOrDefault(churchObject, "map_lng");
        item.is_my_temple = booleanOrDefault(churchObject, "is_my_temple");

        JsonArray images = churchObject.getAsJsonArray("images");
        if (images == null) {
            item.images = new ArrayList<>();
        } else {
            final List<ImageObject> imagesList = new ArrayList<>();

            for (JsonElement churchImage: images) {
                ImageObject image = new ImageObject();
                final JsonObject asJsonObject = churchImage.getAsJsonObject();
                image.url = stringOrDefault(asJsonObject, "url");
                image.thumb_url = stringOrDefault(asJsonObject, "thumb_url");
                image.big_url = stringOrDefault(asJsonObject, "big_url");
                imagesList.add(image);
            }
            item.images = imagesList;
        }

        JsonArray chats = churchObject.getAsJsonArray("chats");
        if (chats == null) {
            item.chats = new ArrayList<>();
        } else {
            final List<TechranchChatObject> imagesList = new ArrayList<>();

            for (JsonElement churchImage: chats) {
                TechranchChatObject chatObject = new TechranchChatObject();
                final JsonObject asJsonObject = churchImage.getAsJsonObject();
                chatObject.id = longOrDefault(asJsonObject, "id");
                chatObject.user_id = longOrDefault(asJsonObject, "user_id");
                chatObject.temple_id = longOrDefault(asJsonObject, "temple_id");
                chatObject.invitation_link = Uri.parse(stringOrDefault(asJsonObject, "invitation_link"));
                chatObject.type = longOrDefault(asJsonObject, "type");
                imagesList.add(chatObject);
            }
            item.chats = imagesList;
        }

        return item;
    }

    private static String stringOrDefault(final JsonObject churchObject, final String key) {
        return stringOrDefault(churchObject, key, "");
    }

    private static String stringOrDefault(final JsonObject churchObject, final String key, final String defaultValue) {
        if (churchObject.has(key)) {
            final JsonElement element = churchObject.get(key);
            if (element.isJsonPrimitive()) {
                return element.getAsString();
            }
        }

        return defaultValue;
    }

    private static Integer longOrDefault(final JsonObject churchObject, final String key) {
        return longOrDefault(churchObject, key, null);
    }

    private static Integer longOrDefault(final JsonObject churchObject, final String key, final Integer defaultValue) {
        if (churchObject.has(key)) {
            final JsonElement element = churchObject.get(key);
            if (element.isJsonPrimitive()) {

                String asString = element.getAsString();

                if (!TextUtils.isEmpty(asString)) {
                    return element.getAsInt();
                }
            }
        }

        return defaultValue;
    }

    private static Double doubleOrDefault(final JsonObject churchObject, final String key) {
        return doubleOrDefault(churchObject, key, null);
    }

    private static Double doubleOrDefault(final JsonObject churchObject, final String key, final Double defaultValue) {
        if (churchObject.has(key)) {
            final JsonElement element = churchObject.get(key);
            if (element.isJsonPrimitive()) {
                String asString = element.getAsString();

                if (!TextUtils.isEmpty(asString)) {
                    return element.getAsDouble();
                }
            }
        }

        return defaultValue;
    }

    private static boolean booleanOrDefault(final JsonObject churchObject, final String key) {
        return booleanOrDefault(churchObject, key, false);
    }

    private static boolean booleanOrDefault(final JsonObject churchObject, final String key, final boolean defaultValue) {
        if (churchObject.has(key)) {
            final JsonElement element = churchObject.get(key);
            if (element.isJsonPrimitive()) {
                return element.getAsBoolean();
            }
        }

        return defaultValue;
    }


}
