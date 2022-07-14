package ca.uwaterloo.cs854s22.neutrino.endpoints;

import io.smallrye.common.annotation.Blocking;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;

@Path("/run")
public class RunResource {

    private static String REGISTRY_PATH = "./registry";

    @POST
    @Path("{function}")
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    public String run(String function) {
        return new File("registry").getAbsolutePath();
    }
}
