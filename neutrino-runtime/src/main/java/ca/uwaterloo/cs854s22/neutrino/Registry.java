package ca.uwaterloo.cs854s22.neutrino;

import ca.uwaterloo.cs854s22.neutrino.utils.CBindings;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.graalvm.collections.Pair;
import org.graalvm.nativeimage.c.function.CFunctionPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.nativeimage.c.type.VoidPointer;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public final class Registry {
    @Inject
    Logger logger;

    @ConfigProperty(name = "neutrino.registry_path", defaultValue = "registry")
    String path;

    Map<String, FunctionPointers> repository = new HashMap<>();

    public FunctionPointers getEntry(String name) {
        logger.debug("Fetching function " + name);

        if (repository.containsKey(name)) {
            logger.debug("Function " + name + " is already loaded");
            return repository.get(name);
        }

        loadEntry(name);
        return repository.get(name);
    }

    private void loadEntry(String name) {
        String location = Path.of(path, name + ".neutrino.so").toString();
        logger.debug("Loading function from " + location);

        try (CTypeConversion.CCharPointerHolder pathHolder = CTypeConversion.toCString(location)) {
            VoidPointer handle = CBindings.DynamicLinking.dlopen(pathHolder.get(), CBindings.DynamicLinking.RTLD_NOW());
            if (handle.isNull()) {
                throw new IllegalArgumentException("Cannot load shared library: "
                        + CTypeConversion.toJavaString(CBindings.DynamicLinking.dlerror()));
            }

            try (CTypeConversion.CCharPointerHolder createHolder = CTypeConversion.toCString("graal_create_isolate")) {
                CFunctionPointer createIsolate = CBindings.DynamicLinking.dlsym(handle, createHolder.get());
                if (createIsolate.isNull()) {
                    throw new IllegalStateException("Cannot resolve symbol graal_create_isolate: "
                            + CTypeConversion.toJavaString(CBindings.DynamicLinking.dlerror()));
                }

                try (CTypeConversion.CCharPointerHolder detachAndTearDownHolder = CTypeConversion.toCString("graal_detach_all_threads_and_tear_down_isolate")) {
                    CFunctionPointer detachAndTearDown = CBindings.DynamicLinking.dlsym(handle, detachAndTearDownHolder.get());
                    if (detachAndTearDown.isNull()) {
                        throw new IllegalStateException("Cannot resolve symbol graal_detach_all_threads_and_tear_down_isolate: "
                                + CTypeConversion.toJavaString(CBindings.DynamicLinking.dlerror()));
                    }

                    try (CTypeConversion.CCharPointerHolder entrypointHolder = CTypeConversion.toCString("neutrino_handler_entrypoint")) {
                        CFunctionPointer entrypoint = CBindings.DynamicLinking.dlsym(handle, entrypointHolder.get());
                        if (entrypoint.isNull()) {
                            throw new IllegalStateException("Cannot resolve symbol neutrino_handler_entrypoint: "
                                    + CTypeConversion.toJavaString(CBindings.DynamicLinking.dlerror()));
                        }

                        FunctionPointers pointers = new FunctionPointers(handle, createIsolate, detachAndTearDown, entrypoint);
                        repository.put(name, pointers);
                    }
                }
            }
        }
    }

    private <T extends Throwable> void warnAndThrow(T t) throws T {
        logger.warn(t);
        throw t;
    }

    public static class FunctionPointers {
        public VoidPointer handle;

        public CFunctionPointer createIsolate;
        public CFunctionPointer detachAndTearDownIsolate;
        public CFunctionPointer entrypoint;

        private FunctionPointers(VoidPointer handle,
                                 CFunctionPointer createIsolate,
                                 CFunctionPointer detachAndTearDownIsolate,
                                 CFunctionPointer entrypoint) {
            this.handle = handle;
            this.createIsolate = createIsolate;
            this.detachAndTearDownIsolate = detachAndTearDownIsolate;
            this.entrypoint = entrypoint;
        }
    }
}
