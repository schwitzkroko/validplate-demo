package net.schwitzkroko.demo.validplate;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/validplate")
public class ValidationResource {

	@Path("/hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST";
    }
    
	@Path("/validate/{plate}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String validate(@PathParam("plate") String plate) {
        return "Validating plate: " + plate;
    }
}
