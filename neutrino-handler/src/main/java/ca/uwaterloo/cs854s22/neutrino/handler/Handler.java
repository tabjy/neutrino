package ca.uwaterloo.cs854s22.neutrino.handler;

import ca.uwaterloo.cs854s22.neutrino.handler.internal.BaseHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Handler extends BaseHandler {
    @Override
    // addition
    public JsonElement handle(JsonObject object) {
        int a = object.get("a").getAsInt();
        int b = object.get("b").getAsInt();

        JsonObject result = new JsonObject();
        result.add("result", new JsonPrimitive(a + b));
        return result;
    }

    @Override
    // echo
    public JsonElement handle(String input) {
        return new JsonPrimitive(input);
    }
}
