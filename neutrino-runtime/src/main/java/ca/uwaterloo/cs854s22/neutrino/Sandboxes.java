package ca.uwaterloo.cs854s22.neutrino;

import io.quarkus.runtime.Startup;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.graalvm.nativeimage.UnmanagedMemory;
import org.graalvm.nativeimage.c.function.CFunctionPointer;
import org.graalvm.nativeimage.c.function.InvokeCFunctionPointer;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.nativeimage.c.type.VoidPointer;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static ca.uwaterloo.cs854s22.neutrino.utils.CBindings.DynamicLinking;

@Startup
@ApplicationScoped
public final class Sandboxes {
    @ConfigProperty(name = "neutrino.output_buffer", defaultValue = "10240")
    long bufferLength;

    @ConfigProperty(name = "neutrino.native_lib_path", defaultValue = "./neutrino-native/target/neutrino-native.so")
    String path;

    @Inject
    Logger logger;

    private NeutrinoNativeFunctionPointer srun;

    @PostConstruct
    void initialize() {
        logger.info("Sandboxes initializing");
        try (CTypeConversion.CCharPointerHolder pathHolder = CTypeConversion.toCString(path)) {
            VoidPointer handle = DynamicLinking.dlopen(pathHolder.get(), DynamicLinking.RTLD_NOW());
            if (handle.isNull()) {
                throw new IllegalStateException("Cannot load neutrino-native shared library: " + CTypeConversion.toJavaString(DynamicLinking.dlerror()));
            }

            try (CTypeConversion.CCharPointerHolder symbolHolder = CTypeConversion.toCString("srun")) {
                srun = DynamicLinking.dlsym(handle, symbolHolder.get());
                if (srun.isNull()) {
                    throw new IllegalStateException("Cannot resolve neutrino-native symbol srun: " + CTypeConversion.toJavaString(DynamicLinking.dlerror()));
                }
            }
        }
    }

    public String run(Registry.FunctionPointers pointers, String input) {
        try (CTypeConversion.CCharPointerHolder inputHolder = CTypeConversion.toCString(input)) {
            CCharPointer outputPointer = UnmanagedMemory.malloc(1024 * 10);

            if (srun.srun(
                    pointers.createIsolate,
                    pointers.detachAndTearDownIsolate,
                    pointers.entrypoint,
                    inputHolder.get(),
                    outputPointer,
                    bufferLength
            ) != 0) {
                throw new IllegalStateException("failed to execute function");
            }

            String output = CTypeConversion.toJavaString(outputPointer);
            UnmanagedMemory.free(outputPointer);

            return output;
        }
    }

    interface NeutrinoNativeFunctionPointer extends CFunctionPointer {

        /**
         * Execute the entrypoint secure(-ish)-ly with a newly created sandbox.
         *
         * <pre>
         * int call(
         *     graal_create_isolate_fn_t graal_create_isolate,
         *     graal_detach_all_threads_and_tear_down_isolate_fn_t graal_detach_all_threads_and_tear_down_isolate,
         *     char* (*entryPoint)(graal_isolatethread_t*, char*),
         *     char* input,
         *     char* output,
         *     size_t len
         * )
         * </pre>
         *
         * @param createIsolate            the address of graal_create_isolate_fn_t symbol from the target shared library
         * @param detachAndTearDownIsolate the address of graal_detach_all_threads_and_tear_down_isolate_fn_t symbol
         *                                 from the target shared library
         * @param entrypoint               function entrypoint
         * @param input                    input buffer
         * @param output                   output buffer
         * @param len                      size of the output buffer
         * @return zero or non-zero error code
         */
        @InvokeCFunctionPointer
        int srun(CFunctionPointer createIsolate,
                 CFunctionPointer detachAndTearDownIsolate,
                 CFunctionPointer entrypoint,
                 CCharPointer input,
                 CCharPointer output,
                 long len);
    }
}
