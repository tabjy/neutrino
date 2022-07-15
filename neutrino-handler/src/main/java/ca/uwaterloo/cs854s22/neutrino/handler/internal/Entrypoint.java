package ca.uwaterloo.cs854s22.neutrino.handler.internal;

import ca.uwaterloo.cs854s22.neutrino.handler.Handler;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Entrypoint {
    private static final Handler handler = new Handler();

    public static void main(String[] args) {
        System.out.println(exec(args[0]));
    }

    @CEntryPoint(name = "neutrino_handler_entrypoint")
    public static CCharPointer entrypoint(@CEntryPoint.IsolateThreadContext IsolateThread thread, CCharPointer inputPointer) {
        String input = CTypeConversion.toJavaString(inputPointer);
        String output = exec(input);

        try (CTypeConversion.CCharPointerHolder holder = CTypeConversion.toCString(output)) {
            return holder.get();
        }
    }

    private static String exec(String input) {
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
