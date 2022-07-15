package ca.uwaterloo.cs854s22.neutrino.handler.internal;

import com.google.gson.*;

public class BaseHandler {

    protected BaseHandler() {
    }

    public JsonElement handle(JsonElement element) {
        if (element.isJsonNull()) {
            return this.handle();
        }

        if (element.isJsonPrimitive()) {
            return this.handle((JsonPrimitive) element);
        }

        if (element.isJsonObject()) {
            return this.handle((JsonObject) element);
        }

        if (element.isJsonArray()) {
            return this.handle((JsonArray) element);
        }

        throw new UnsupportedOperationException("unexpected JsonElement type!");
    }

    public JsonElement handle() {
        throw new UnsupportedOperationException("handler not implemented!");
    }

    public JsonElement handle(JsonPrimitive primitive) {
        if (primitive.isBoolean()) {
            return this.handle(primitive.getAsBoolean());
        }

        if (primitive.isNumber()) {
            return this.handle(primitive.getAsNumber());
        }

        if (primitive.isString()) {
            return this.handle(primitive.getAsString());
        }

        throw new UnsupportedOperationException("unexpected JsonPrimitive type!");
    }

    public JsonElement handle(boolean bool) {
        throw new UnsupportedOperationException("handler not implemented!");
    }

    public JsonElement handle(Number number) {
        if (number instanceof Long || number instanceof Integer) {
            return this.handle(number.longValue());
        }

        if (number instanceof Double || number instanceof Float) {
            return this.handle(number.doubleValue());
        }

        throw new UnsupportedOperationException("unexpected Number type!");
    }

    public JsonElement handle(long number) {
        return this.handle((int) number);
    }

    public JsonElement handle(int number) {
        throw new UnsupportedOperationException("handler not implemented!");
    }

    public JsonElement handle(double number) {
        return this.handle((float) number);
    }

    public JsonElement handle(float number) {
        throw new UnsupportedOperationException("handler not implemented!");
    }

    public JsonElement handle(String string) {
        throw new UnsupportedOperationException("handler not implemented!");
    }

    public JsonElement handle(JsonObject object) {
        throw new UnsupportedOperationException("handler not implemented!");
    }

    public JsonElement handle(JsonArray array) {
        throw new UnsupportedOperationException("handler not implemented!");
    }
}
