package facade.service.adapter.rest;

import facade.classes.Customer;
import facade.exceptions.CustomerServiceException;
import facade.exceptions.ReportCreationException;
import facade.service.CustomerService;
import facade.service.TokenListWrapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/*
  ##############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programming #
  ##############################################################################
*/
@Path("/customer")
public class CustomerResource {
    CustomerService service = new CustomerFactory().getService();

    /*
    ####################################
    # Responsible: Jakub (s232946) #
    ####################################
    */
    @Path("report")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces("application/json")
    public Response getReport(@QueryParam("id") String id) {
        try {
            var result = service.getReport(id);
            return Response.ok().entity(result).build();
        } catch (ReportCreationException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    // Issue new tokens. Will return all customer's unconsumed tokens, old and new.
    @Path("tokens")
    @GET
    @Produces("application/json")
    public TokenListWrapper getTokens(@QueryParam("id") String id, @QueryParam("amount") int amount) {
        return service.getTokens(id, amount);
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    // Register customer
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response register(Customer customer) {
        try {
            return Response.ok().entity(service.register(customer)).build();
        } catch (CustomerServiceException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    // De-register customer
    @DELETE
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deregister(@QueryParam("id") String id) {
        try {
            return Response.ok().entity(service.deregister(id)).build();
        } catch (CustomerServiceException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}
