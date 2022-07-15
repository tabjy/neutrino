package ca.uwaterloo.cs854s22.neutrino.endpoints;

import ca.uwaterloo.cs854s22.neutrino.Registry;
import ca.uwaterloo.cs854s22.neutrino.Sandboxes;
import io.smallrye.common.annotation.Blocking;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/run")
public class RunResource {

    @Inject
    Registry registry;

    @Inject
    Sandboxes sandboxes;

    @Inject
    Logger logger;

    private static final String REGISTRY_PATH = "./registry";

    @POST
    @Path("{function}")
    @Consumes()
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    public String run(String function) {
        logger.info("running function " + function);

        Registry.FunctionPointers pointers = registry.getEntry(function);

//        return String.format("{\"create\": \"%s\", \"teardown\": \"%s\", \"entrypoint\": \"%s\"}",
//                pointers.createIsolate.rawValue(),
//                pointers.detachAndTearDownIsolate.rawValue(),
//                pointers.entrypoint.rawValue()
//                );
        return sandboxes.run(pointers, "");
    }
}
