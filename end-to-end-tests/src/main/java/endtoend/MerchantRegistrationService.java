package endtoend;

import classes.Merchant;
import exceptions.MerchantServiceException;
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
public class MerchantRegistrationService {
    Client client = ClientBuilder.newClient();
    WebTarget r = client.target("http://localhost:8080/");

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    // Registers a merchant
    public Merchant register(Merchant m) throws MerchantServiceException {
        try {
            var response = r.path("merchant").request().post(Entity.json(m), Merchant.class);
            return response;
        } catch (BadRequestException e) { // Return error messages
            throw new MerchantServiceException(e.getResponse().readEntity(String.class));
        }
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    // De-registers a merchant
    public String deregister(String id) throws MerchantServiceException {
        try {
            var response = r.path("merchant").queryParam("id", id).request().delete(String.class);
            return response;
        } catch (NotFoundException e) { // Return error messages
            throw new MerchantServiceException(e.getResponse().readEntity(String.class));
        }
    }
}
