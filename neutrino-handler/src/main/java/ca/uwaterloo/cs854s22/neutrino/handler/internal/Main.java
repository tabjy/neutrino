package ca.uwaterloo.cs854s22.neutrino.handler.internal;

import ca.uwaterloo.cs854s22.neutrino.handler.Handler;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class Main {
    private static final Handler handler = new Handler();

    public static void main(String[] args) throws IOException {
        String result = exec(args[0]);

        try (FileOutputStream fos = new FileOutputStream("/tmp/output")) {
            fos.write(result.getBytes(StandardCharsets.UTF_8));
        }

        System.out.println(result);
    }

    static String exec(String input) {
        try {
            JsonElement request = JsonParser.parseString(input);
            JsonElement output = handler.handle(request);
            if (output == null) {
                return "null";
            }

            return output.toString();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stack = new Gson().toJson(sw.toString());

            return String.format(
                    "{\"error\": \"%s\", \"message\": \"%s\", \"stack\": %s}",
                    e.getClass().getSimpleName(), e.getMessage(), stack);
        }
    }
}
