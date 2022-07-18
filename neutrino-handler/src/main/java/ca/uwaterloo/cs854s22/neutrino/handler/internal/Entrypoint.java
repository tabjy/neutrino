package ca.uwaterloo.cs854s22.neutrino.handler.internal;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;

public class Entrypoint {


    @CEntryPoint(name = "neutrino_handler_entrypoint")
    public static CCharPointer entrypoint(@CEntryPoint.IsolateThreadContext IsolateThread thread, CCharPointer inputPointer) {
        String input = CTypeConversion.toJavaString(inputPointer);
        String output = Main.exec(input);

        try (CTypeConversion.CCharPointerHolder holder = CTypeConversion.toCString(output)) {
            return holder.get();
        }
    }
}
