package endtoend;

import classes.Payment;
import exceptions.MerchantServiceException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;

/*
  #############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programing #
  #############################################################################
*/
public class MerchantPaymentService {

    Client client = ClientBuilder.newClient();
    WebTarget r = client.target("http://localhost:8080/");

    public String pay(Payment payment) throws MerchantServiceException {
        try {
            var response = r.path("merchant/payment").request().post(Entity.json(payment), String.class);
            return response;
        } catch (BadRequestException e) {
            throw new MerchantServiceException(e.getResponse().readEntity(String.class));
        }
    }
}
