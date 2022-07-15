#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dlfcn.h>

#include "includes/graal_isolate_dynamic.h"

int srun(
        graal_create_isolate_fn_t graal_create_isolate,
        graal_detach_all_threads_and_tear_down_isolate_fn_t graal_detach_all_threads_and_tear_down_isolate,
        char* (*entryPoint)(graal_isolatethread_t*, char*),
        char* input,
        char* output,
        size_t len
    ) {
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        fprintf(stderr, "graal_create_isolate error\n");
        return 1;
    }

    char* result = entryPoint(thread, input);
    if (strlen(result) + 1 > len) {
        fprintf(stderr, "insufficient buffer size\n");
        return 1;
    }

    strcpy(output, result);

    if (graal_detach_all_threads_and_tear_down_isolate(thread)) {
        fprintf(stderr, "graal_detach_all_threads_and_tear_down_isolate error\n");
        return 1;
    }

    return 0;
}