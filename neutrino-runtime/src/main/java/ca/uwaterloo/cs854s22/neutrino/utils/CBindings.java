package ca.uwaterloo.cs854s22.neutrino.utils;

import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.constant.CConstant;
import org.graalvm.nativeimage.c.function.CFunction;
import org.graalvm.nativeimage.c.function.CFunctionPointer;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.VoidPointer;

import java.util.List;

public final class CBindings {
    private static class Directives implements CContext.Directives {
        @Override
        public List<String> getHeaderFiles() {
            return List.of("<dlfcn.h>");
        }
    }

    @CContext(CBindings.Directives.class)
    public final static class DynamicLinking {

        /**
         * If this value is specified, or the environment variable LD_BIND_NOW is set to a nonempty string, all
         * undefined symbols in the shared object are resolved before dlopen() returns. If this cannot be done, an error
         * is returned.
         *
         * @return the RTLD_NOW flag constant
         */
        @CConstant("RTLD_NOW")
        public static native int RTLD_NOW();

        /**
         * Load the dynamic shared object (shared library) file named by the null-terminated string filename and
         * returns an opaque "handle" for the loaded object.
         * <p>
         * If filename is NULL, then the returned handle is for the main program.  If filename contains a slash ("/"),
         * then it is interpreted as a (relative or absolute) pathname.  Otherwise, the dynamic linker searches for the
         * object (see ld.so(8) for  further details).
         *
         * <pre>void *dlopen(const char *filename, int flags)</pre>
         *
         * @param filename name of the dynamic library
         * @param flags    always use RTLD_NOW
         * @return object handle or null (if failed)
         */
        @CFunction("dlopen")
        public static native VoidPointer dlopen(CCharPointer filename, int flags);

        /**
         * Decrement the reference count on the dynamically loaded shared object referred to by handle.
         *
         * <pre>int dlclose(void *handle)</pre>
         *
         * @param handle name of the dynamic library
         * @return zero or non-zero error code
         */
        @CFunction("dlclose")
        public static native int dlclose(VoidPointer handle);

        /**
         * Obtain address of a symbol in a shared object or executable
         *
         * <pre>void *dlsym(void *restrict handle, const char *restrict symbol)</pre>
         *
         * @param handle handle of a dynamic loaded shared object returned by <code>dlopen</code>
         * @param symbol null-terminated symbol name
         * @return address associated with symbol or null (if failed)
         */
        @CFunction("dlsym")
        public static native <T extends CFunctionPointer> T dlsym(VoidPointer handle, CCharPointer symbol);

        /**
         * Obtain error diagnostic for functions in the <code>dlopen</code> API. Returns <code>NULL</code> if no errors
         * have occurred since initialization or since it was last called.
         *
         * <pre>char *dlerror(void)</pre>
         *
         * @return error message or null
         */
        @CFunction("dlerror")
        public static native CCharPointer dlerror();
    }
}
