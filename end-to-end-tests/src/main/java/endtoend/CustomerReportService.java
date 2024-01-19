package endtoend;

import classes.CustomerReport;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

/*
  ################################
  # Developed by Jakub (s232946) #
  ################################
*/
public class CustomerReportService {
    Client client = ClientBuilder.newClient();
    WebTarget r = client.target("http://localhost:8080/");

    public CustomerReport getReport(String id) {
        return r.path("customer/report").queryParam("id", id).request().get(CustomerReport.class);
    }
}
