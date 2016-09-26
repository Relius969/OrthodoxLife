package org.telegram.pravzhizn.pravzhizn.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.telegram.pravzhizn.pravzhizn.responses.TempleDetailsResponse;

import java.lang.reflect.Type;


import static org.telegram.pravzhizn.pravzhizn.deserializers.TemplesResponseDeserializer.jsonToRemoteChurch;

/**
 * Created by vlad on 6/22/16.
 */
public class TempleDetailsDeserializer implements JsonDeserializer<TempleDetailsResponse> {
    @Override
    public TempleDetailsResponse deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        TempleDetailsResponse response = new TempleDetailsResponse();

        response.success = json.getAsJsonObject().get("success").getAsBoolean();

        JsonObject data = json.getAsJsonObject().get("data").getAsJsonObject();

        response.data = jsonToRemoteChurch(data);

        return response;
    }

}
