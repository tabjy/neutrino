package ca.uwaterloo.cs854s22.neutrino.handler;

import ca.uwaterloo.cs854s22.neutrino.handler.internal.BaseHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Handler extends BaseHandler {
    @Override
    public JsonElement handle(JsonElement element) {
        return element;
    }
}
