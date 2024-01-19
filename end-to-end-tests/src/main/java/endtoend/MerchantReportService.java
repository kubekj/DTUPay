package endtoend;

import classes.MerchantReport;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

/*
  ################################
  # Developed by Jakub (s232946) #
  ################################
*/
public class MerchantReportService {
    Client client = ClientBuilder.newClient();
    WebTarget r = client.target("http://localhost:8080/");

    public MerchantReport getReport(String id) {
        return r.path("merchant/report").queryParam("id", id).request().get(MerchantReport.class);
    }
}
