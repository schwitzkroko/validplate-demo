package net.schwitzkroko.demo.validplate;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.schwitzkroko.demo.validplate.plate.PlateModel;
import net.schwitzkroko.demo.validplate.plate.PlateService;

@Path("/validplate")
public class ValidationResource {

    @Inject
    PlateService plateService;

    @Path("/validate/{plate}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response validate(@PathParam("plate") String plate) {
    	
		return switch (plateService.digest(plate)) {
            case PlateModel.Valid v   -> Response.ok(v.canonical()).build();
            case PlateModel.Invalid i -> Response.status(422).entity(i.canonical()).build();
        };
    }
}