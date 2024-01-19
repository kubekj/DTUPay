package facade.service.adapter.rest;

import facade.exceptions.ReportCreationException;
import facade.service.ReportService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/*
  ################################
  # Developed by Jakub (s232946) #
  ################################
*/
@Path("/report")
public class ReportResource {
    ReportService service = new ReportFactory().getService();

    @GET
    @Produces("application/json")
    public Response getReport() {
        try {
            var result = service.getReport();
            return Response.ok().entity(result).build();
        } catch (ReportCreationException e) {
            return Response.status(Response.Status.NO_CONTENT).entity(e.getMessage()).build();
        }
    }
}
