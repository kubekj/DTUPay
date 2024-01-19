package endtoend;

import classes.Customer;
import exceptions.CustomerServiceException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;

/*
  #############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programing #
  #############################################################################
*/
public class CustomerRegistrationService {
    Client client = ClientBuilder.newClient();
    WebTarget r = client.target("http://localhost:8080/");

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    // Registers a customer
    public Customer register(Customer c) throws CustomerServiceException {
        try {
            var response = r.path("customer").request().post(Entity.json(c), Customer.class);
            return response;
        } catch (BadRequestException e) { // Return error messages
            throw new CustomerServiceException(e.getResponse().readEntity(String.class));
        }
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    // De-registers a customer
    public String deregister(String id) throws CustomerServiceException {
        try {
            var response = r.path("customer").queryParam("id", id).request().delete(String.class);
            return response;
        } catch (NotFoundException e) { // Return error messages
            throw new CustomerServiceException(e.getResponse().readEntity(String.class));
        }

    }
}
