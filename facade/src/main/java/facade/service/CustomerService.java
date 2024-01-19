package facade.service;

import facade.classes.Customer;
import facade.classes.CustomerReport;
import facade.classes.User;
import facade.exceptions.CustomerServiceException;
import facade.exceptions.ReportCreationException;
import messaging.Event;
import messaging.MessageQueue;

import java.util.concurrent.CompletableFuture;

import static utilities.ErrorMessages.ERROR_CUSTOMER_ALREADY_EXIST;
import static utilities.ErrorMessages.ERROR_CUSTOMER_DOES_NOT_EXIST;
import static utilities.EventTopics.*;

/*
  ##############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programming #
  ##############################################################################
*/
public class CustomerService {
    private MessageQueue queue;
    private CompletableFuture<Customer> registeredCustomers;
    private CompletableFuture<String> deregisteredCustomers;
    private CompletableFuture<TokenListWrapper> issuedTokens;
    private CompletableFuture<CustomerReport> customerReport;

    // Init handlers
    public CustomerService(MessageQueue mq) {
        queue = mq;
        queue.addHandler(CUSTOMER_REGISTERED, this::handleCustomerRegistered);
        queue.addHandler(CUSTOMER_DEREGISTERED, this::handleCustomerDeregistered);
        queue.addHandler(TOKEN_ISSUED, this::handleTokenIssued);
        queue.addHandler(TOKEN_NOT_ISSUED, this::handleTokenIssued);
        queue.addHandler(CUSTOMER_NOT_REGISTERED, this::handleCustomerRegistered);
        queue.addHandler(CUSTOMER_DOES_NOT_EXIST, this::handleCustomerDeregistered);
        queue.addHandler(CUSTOMER_REPORT_GENERATED, this::handleCostumerReportGenerated);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    // Function to request new tokens, will publish event for the TokenMgmtService
    public TokenListWrapper getTokens(String id, int amount) {
        issuedTokens = new CompletableFuture<>();
        Event event = new Event(ISSUE_TOKEN_REQUESTED, new Object[]{id, amount});
        queue.publish(event);
        return issuedTokens.join();
    }

    /*
    ####################################
    # Responsible: Jakub (s232946) #
    ####################################
    */
    public CustomerReport getReport(String id) throws ReportCreationException {
        customerReport = new CompletableFuture<>();
        Event event = new Event(CUSTOMER_REPORT_REQUESTED, new Object[]{id});
        queue.publish(event);
        var result = customerReport.join();
        if (result.getCustomerPayments().isEmpty())
            throw new ReportCreationException();
        return result;
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    // Register customer, publish event to AccountMgmt
    public Customer register(User customer) throws CustomerServiceException {
        registeredCustomers = new CompletableFuture<>();
        Event event = new Event(CUSTOMER_REGISTRATION_REQUESTED, new Object[]{customer});
        queue.publish(event);
        var result = registeredCustomers.join();
        if (result.getId() == null) {
            throw new CustomerServiceException(ERROR_CUSTOMER_ALREADY_EXIST);
        }
        return result;
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    // De-register customer, publish event to AccountMgmt
    public String deregister(String id) throws CustomerServiceException {
        deregisteredCustomers = new CompletableFuture<>();
        Event event = new Event(CUSTOMER_DEREGISTRATION_REQUESTED, new Object[]{id});
        queue.publish(event);
        var result = deregisteredCustomers.join();
        if (result.equals(ERROR_CUSTOMER_DOES_NOT_EXIST)) {
            throw new CustomerServiceException(ERROR_CUSTOMER_DOES_NOT_EXIST);
        }
        return result;
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    // Handler for customer registration, event comes from AccountMgmtService
    public void handleCustomerRegistered(Event event) {
        var customer = event.getArgument(0, Customer.class);
        registeredCustomers.complete(customer);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    // Handler for customer de-registration, event comes from AccountMgmtService
    public void handleCustomerDeregistered(Event event) {
        var string = event.getArgument(0, String.class);
        deregisteredCustomers.complete(string);
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    // Handler for report, event comes from ReportMgmtService
    public void handleCostumerReportGenerated(Event event) {
        CustomerReport cr = event.getArgument(0, CustomerReport.class);
        customerReport.complete(cr);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    // Handler for tokens issuing, event comes from TokenMgmtService
    public void handleTokenIssued(Event event) {
        TokenListWrapper tokenWrapper = event.getArgument(0, TokenListWrapper.class);
        issuedTokens.complete(tokenWrapper);
    }
}
