package ca.uwaterloo.cs854s22.neutrino.endpoints;

import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class TestResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }

    @GET
    @Path("/blocking")
    public String blocking() {
        return Thread.currentThread().getName();
    }

    @GET
    @Path("/nonblocking")
    public Uni<String> nonblocking() {
        return Uni.createFrom().item(Thread.currentThread().getName());
    }
}