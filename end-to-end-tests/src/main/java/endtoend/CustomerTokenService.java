package endtoend;

import classes.Token;
import classes.TokenListWrapper;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

import java.util.List;

/*
  #############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programing #
  #############################################################################
*/
public class CustomerTokenService {

    Client client = ClientBuilder.newClient();
    WebTarget r = client.target("http://localhost:8080/");

    public List<Token> getTokens(String id, int amount) {
        var response = r.path("customer/tokens").queryParam("id", id).queryParam("amount", amount).request().get(TokenListWrapper.class);
        return response.getTokens();
    }
}
