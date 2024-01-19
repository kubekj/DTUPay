package behaviourtests;

import facade.classes.*;
import facade.classes.DTO.CustomerPaymentDTO;
import facade.classes.DTO.ManagerPaymentDTO;
import facade.classes.DTO.MerchantPaymentDTO;
import facade.exceptions.CustomerServiceException;
import facade.exceptions.MerchantServiceException;
import facade.exceptions.ReportCreationException;
import facade.service.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.*;
import static utilities.ErrorMessages.*;
import static utilities.EventTopics.*;

/*
  ##############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programming #
  ##############################################################################
  ###############################################################################################
  # Reporting Developed by Jakub (s232946) assisted by Andreas (s176334) and Christian (s194578)#
  ###############################################################################################
*/
public class FacadeSteps {

    private CompletableFuture<Event> publishedEvent = new CompletableFuture<>();

    private MessageQueue q = new MessageQueue() {

        @Override
        public void publish(Event event) {
            publishedEvent.complete(event);
        }

        @Override
        public void addHandler(String eventType, Consumer<Event> handler) {
        }

    };
    private MerchantService merchantService = new MerchantService(q);
    private CustomerService customerService = new CustomerService(q);
    private ReportService reportService = new ReportService(q);
    private CompletableFuture<Merchant> registeredMerchant = new CompletableFuture<>();
    private CompletableFuture<String> deregisteredMerchant = new CompletableFuture<>();
    private CompletableFuture<Customer> registeredCustomer = new CompletableFuture<>();
    private CompletableFuture<String> deregisteredCustomer = new CompletableFuture<>();
    private CompletableFuture<String> successfulPayment = new CompletableFuture<>();
    private CompletableFuture<TokenListWrapper> issuedTokens = new CompletableFuture<>();
    private CompletableFuture<CustomerReport> customerReport = new CompletableFuture<>();
    private CompletableFuture<MerchantReport> merchantReport = new CompletableFuture<>();
    private CompletableFuture<ManagerReport> managerReport = new CompletableFuture<>();
    private User expectedUser, expectedUser2;
    private String expectedString, expectedErrorMessage;
    private int amountOfTokens;
    private ArrayList<Token> expectedListOfTokens;
    private ArrayList<Payment> expectedListOfPayments;
    private Payment expectedPayment;
    private CustomerReport expectedCustomerReport;
    private MerchantReport expectedMerchantReport;
    private ManagerReport expectedManagerReport;


    public FacadeSteps() {
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @When("the merchant requests a report")
    public void theMerchantRequestsAReport() {
        new Thread(() -> {
            MerchantReport result = null;
            try {
                result = merchantService.getReport(expectedUser2.getId());
            } catch (ReportCreationException e) {
                System.out.println(e.getMessage());
            }
            merchantReport.complete(result);
        }).start();
    }

    /*
    ################################
    # Responsible: Jakub (s232946) #
    ################################
    */
    @When("the manager requests a report")
    public void theManagerRequestsAReport() {
        new Thread(() -> {
            ManagerReport result = null;
            try {
                result = reportService.getReport();
            } catch (ReportCreationException e) {
                System.out.println(e.getMessage());
            }
            managerReport.complete(result);
        }).start();
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the payment is in the manager report")
    public void thePaymentIsInTheManagerReport() {
        assertNotNull(managerReport.join());
        assertEquals(expectedManagerReport, managerReport.join());
        var paymentDTO = new ManagerPaymentDTO(expectedPayment.getAmount(), expectedPayment.getCustomerTokenId(), expectedPayment.getCustomerID(), expectedPayment.getMerchantID());
        assertTrue(expectedManagerReport.getManagerPayments().contains(paymentDTO));
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @And("the customer has made payment")
    public void theCustomerHasMadePayment() {
        expectedPayment = new Payment();
        expectedPayment.setCustomerID(expectedUser.getId());
        expectedPayment.setCustomerTokenId("1337");
        expectedPayment.setMerchantID(expectedUser2.getId());
        expectedPayment.setAmount(100);
    }

    /*
    ################################
    # Responsible: Jakub (s232946) #
    ################################
    */
    @When("the customer requests a report")
    public void theCustomerRequestsAReport() {
        new Thread(() -> {
            CustomerReport result = null;
            try {
                result = customerService.getReport(expectedUser.getId());
            } catch (ReportCreationException e) {
                System.out.println(e.getMessage());
            }
            customerReport.complete(result);
        }).start();
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the {string} report event is sent")
    public void theReportEventIsSent(String eventName) {
        Event event = null;
        switch (eventName) {
            case CUSTOMER_REPORT_REQUESTED:
                event = new Event(eventName, new Object[]{expectedUser.getId()});
                break;
            case MERCHANT_REPORT_REQUESTED:
                event = new Event(eventName, new Object[]{expectedUser2.getId()});
                break;
            case MANAGER_REPORT_REQUESTED:
                event = new Event(eventName, new Object[]{});
                break;
        }
        assertEquals(event, publishedEvent.join());
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the payment is in the merchant report")
    public void thePaymentIsInTheMerchantReport() {
        assertNotNull(merchantReport.join());
        assertEquals(expectedMerchantReport, merchantReport.join());
        var paymentDTO = new MerchantPaymentDTO(expectedPayment.getAmount(), expectedPayment.getCustomerTokenId());
        assertTrue(expectedMerchantReport.getMerchantPayments().contains(paymentDTO));
    }

    /*
    ################################
    # Responsible: Jakub (s232946) #
    ################################
    */
    @When("the {string} report event is received with non-empty report")
    public void theReportEventIsReceivedWithNonEmptyReport(String eventName) throws ReportCreationException {
        expectedListOfPayments = new ArrayList<>();
        expectedListOfPayments.add(expectedPayment);

        PaymentListWrapper paymentListWrapper = new PaymentListWrapper(expectedListOfPayments);
        switch (eventName) {
            case CUSTOMER_REPORT_GENERATED:
                expectedCustomerReport = new CustomerReport();
                expectedCustomerReport.generateReport(paymentListWrapper);
                customerService.handleCostumerReportGenerated(new Event(eventName, new Object[]{expectedCustomerReport}));
                break;
            case MERCHANT_REPORT_GENERATED:
                expectedMerchantReport = new MerchantReport();
                expectedMerchantReport.generateReport(paymentListWrapper);
                merchantService.handleMerchantReportGenerated(new Event(eventName, new Object[]{expectedMerchantReport}));
                break;
            case MANAGER_REPORT_GENERATED:
                expectedManagerReport = new ManagerReport();
                expectedManagerReport.generateReport(paymentListWrapper);
                reportService.handleManagerReportGenerated(new Event(eventName, new Object[]{expectedManagerReport}));
                break;
        }
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the payment is in the customer report")
    public void thePaymentIsInTheCustomerReport() {

        assertNotNull(customerReport.join());
        assertEquals(expectedCustomerReport, customerReport.join());
        var paymentDTO = new CustomerPaymentDTO(expectedPayment.getAmount(), expectedPayment.getCustomerTokenId(), expectedPayment.getMerchantID());
        assertTrue(expectedCustomerReport.getCustomerPayments().contains(paymentDTO));

    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the {string} deregistration event is received")
    public void theDeregistrationEventIsReceived(String eventName) {
        if (eventName.equals(CUSTOMER_DOES_NOT_EXIST)) {
            customerService.handleCustomerDeregistered(new Event(eventName, new Object[]{ERROR_CUSTOMER_DOES_NOT_EXIST}));
        } else if (eventName.equals(MERCHANT_DOES_NOT_EXIST)) {
            merchantService.handleMerchantDeregistered(new Event(eventName, new Object[]{ERROR_MERCHANT_DOES_NOT_EXIST}));
        }
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("a deregistration error message is returned saying {string}")
    public void aDeregistrationErrorMessageIsReturnedSaying(String errorMessage) {
        if (errorMessage.contains("Customer")) {
            assertNull(deregisteredCustomer.join());
        } else if (errorMessage.contains("Merchant")) {
            assertNull(deregisteredMerchant.join());
        }
        assertEquals(errorMessage, expectedErrorMessage);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @When("the {string} registration event is received")
    public void theRegistrationEventIsReceived(String eventName) {
        User tmpUser = null;
        if (eventName.equals(CUSTOMER_NOT_REGISTERED)) {
            tmpUser = new Customer();
        } else if (eventName.equals(MERCHANT_NOT_REGISTERED)) {
            tmpUser = new Merchant();
        }

        tmpUser.setCpr(expectedUser.getCpr());

        if (eventName.equals(CUSTOMER_NOT_REGISTERED)) {
            customerService.handleCustomerRegistered(new Event(eventName, new Object[]{tmpUser}));
        } else if (eventName.equals(MERCHANT_NOT_REGISTERED)) {
            merchantService.handleMerchantRegistered(new Event(eventName, new Object[]{tmpUser}));
        }
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("an error message is returned saying {string}")
    public void anErrorMessageIsReturnedSaying(String errorMessage) {

        if (errorMessage.contains("Customer")) {
            assertNull(registeredCustomer.join());
        } else if (errorMessage.contains("Merchant")) {
            assertNull(registeredMerchant.join());
        }
        assertEquals(errorMessage, expectedErrorMessage);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Given("there is a merchant registered with DTU Pay")
    public void thereIsAMerchantRegisteredWithDTUPay() {
        expectedUser2 = new Merchant();
        expectedUser2.setId("2");
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Given("there is a customer registered with DTU Pay")
    public void thereIsACustomerRegisteredWithDTUPay() {
        expectedUser = new Customer();
        expectedUser.setId("1");
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @When("the customer requests {int} tokens")
    public void theCustomerRequestsTokens(Integer amount) {
        amountOfTokens = amount;
        new Thread(() -> {
            var result = customerService.getTokens(expectedUser.getId(), amount);
            issuedTokens.complete(result);
        }).start();
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the {string} token event is sent")
    public void theTokenEventIsSent(String eventName) {
        Event event = new Event(eventName, new Object[]{expectedUser.getId(), amountOfTokens});
        assertEquals(event, publishedEvent.join());
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @When("the {string} token event is received")
    public void theTokenEventIsReceived(String eventName) {
        expectedListOfTokens = new ArrayList<>();
        if (eventName.equals(TOKEN_ISSUED)) {
            Token token1 = new Token(expectedUser.getId());
            Token token2 = new Token(expectedUser.getId());
            expectedListOfTokens.add(token1);
            expectedListOfTokens.add(token2);
        }
        TokenListWrapper tokenListWrapper = new TokenListWrapper();
        tokenListWrapper.setTokens(expectedListOfTokens);
        customerService.handleTokenIssued(new Event(eventName, new Object[]{tokenListWrapper}));
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the customer received {int} tokens")
    public void theCustomerReceivedTokens(Integer amount) {
        assertEquals(amount, issuedTokens.join().getTokens().size());
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @When("the merchant initiates a payment with amount {int} and token id {string}")
    public void theMerchantInitiatesAPaymentWithAmountAndTokenId(Integer amount, String tokenId) {
        expectedPayment = new Payment();
        new Thread(() -> {
            expectedPayment.setMerchantID(expectedUser.getId());
            expectedPayment.setAmount(amount);
            expectedPayment.setCustomerTokenId(tokenId);
            String result = null;
            try {
                result = merchantService.pay(expectedPayment);
            } catch (MerchantServiceException e) {
                result = e.getMessage();
            }
            successfulPayment.complete(result);
        }).start();
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the {string} payment event is sent")
    public void thePaymentEventIsSent(String eventName) {
        Event event = new Event(eventName, new Object[]{expectedPayment});
        assertEquals(event, publishedEvent.join());
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @When("the {string} event is received")
    public void theEventIsReceived(String eventName) {
        if (eventName.equals(MONEY_TRANSFERRED)) {
            expectedString = expectedPayment.toString() + " is successful";
        } else if (eventName.equals(MONEY_NOT_TRANSFERRED)) {
            expectedString = ERROR_UNSUCCESSFUL_PAYMENT;
        }
        merchantService.handleMoneyTransferred(new Event(eventName, new Object[]{expectedString}));
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the payment is unsuccessful")
    public void thePaymentIsUnsuccessful() {
        assertEquals(expectedString, successfulPayment.join());
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertEquals(expectedString, successfulPayment.join());
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Given("there is no customer with id {string}")
    public void thereIsNoCustomerWithId(String id) {
        expectedUser = new Customer();
        expectedUser.setFirstName("James");
        expectedUser.setLastName("Bond");
        expectedUser.setCpr("123456-1234");
        expectedUser.setId(id);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Given("there is no merchant with id {string}")
    public void thereIsNoMerchantWithId(String id) {
        expectedUser = new Merchant();
        expectedUser.setFirstName("James");
        expectedUser.setLastName("Bond");
        expectedUser.setCpr("123456-1234");
        expectedUser.setId(id);
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Given("there is a merchant with id {string}")
    public void thereIsAMerchantWithId(String id) {
        expectedUser = new Merchant();
        expectedUser.setFirstName("James");
        expectedUser.setLastName("Bond");
        expectedUser.setCpr("123456-1234");
        expectedUser.setId(id);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @When("the merchant is being de-registered")
    public void theMerchantIsBeingDeRegistered() {
        new Thread(() -> {
            String result = null;
            try {
                result = merchantService.deregister(expectedUser.getId());
            } catch (MerchantServiceException e) {
                expectedErrorMessage = e.getMessage();
            }
            deregisteredMerchant.complete(result);
        }).start();
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the merchant is de-registered")
    public void theMerchantIsDeRegistered() {
        assertEquals(expectedString, deregisteredMerchant.join());
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Given("there is a customer with id {string}")
    public void thereIsACustomerWithId(String id) {
        expectedUser = new Customer();
        expectedUser.setFirstName("James");
        expectedUser.setLastName("Bond");
        expectedUser.setCpr("123456-1234");
        expectedUser.setId(id);
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @When("the customer is being de-registered")
    public void theCustomerIsBeingDeRegistered() {
        new Thread(() -> {
            String result = null;
            try {
                result = customerService.deregister(expectedUser.getId());
            } catch (CustomerServiceException e) {
                expectedErrorMessage = e.getMessage();
            }
            deregisteredCustomer.complete(result);
        }).start();
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @When("the {string} deregistration event is sent")
    public void theDeregistrationEventIsSent(String eventName) {
        expectedString = expectedUser.toString() + " is deleted";

        if (eventName.equals(CUSTOMER_DEREGISTERED)) {
            customerService.handleCustomerDeregistered(new Event(eventName, new Object[]{expectedString}));
        } else if (eventName.equals(MERCHANT_DEREGISTERED)) {
            merchantService.handleMerchantDeregistered(new Event(eventName, new Object[]{expectedString}));
        }
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the customer is de-registered")
    public void theCustomerIsDeRegistered() {
        assertEquals(expectedString, deregisteredCustomer.join());
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Given("there is a customer with empty id")
    public void thereIsACustomerWithEmptyId() {
        expectedUser = new Customer();
        expectedUser.setFirstName("James");
        expectedUser.setLastName("Bond");
        expectedUser.setCpr("123456-1234");
        assertNull(expectedUser.getId());
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @When("the customer is being registered")
    public void theCustomerIsBeingRegistered() {
        new Thread(() -> {
            Customer result = null;
            try {
                result = customerService.register(expectedUser);
            } catch (CustomerServiceException e) {
                expectedErrorMessage = e.getMessage();
            }
            registeredCustomer.complete(result);
        }).start();
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the customer is registered and his id is set")
    public void theCustomerIsRegisteredAndHisIdIsSet() {
        assertNotNull(registeredCustomer.join().getId());
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Given("there is a merchant with empty id")
    public void thereIsAMerchantWithEmptyId() {
        expectedUser = new Merchant();
        expectedUser.setFirstName("James");
        expectedUser.setLastName("Bond");
        expectedUser.setCpr("123456-1234");
        assertNull(expectedUser.getId());
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @When("the customer is being registered with the same CPR number as another")
    public void theCustomerIsBeingRegisteredWithTheSameCPRNumberAsAnother() {
        new Thread(() -> {
            Customer result = null;
            try {
                result = customerService.register(expectedUser);
            } catch (CustomerServiceException e) {
                expectedErrorMessage = e.getMessage();
            }
            registeredCustomer.complete(result);
        }).start();
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @When("the merchant is being registered with the same CPR number as another")
    public void theMerchantIsBeingRegisteredWithTheSameCPRNumberAsAnother() {
        new Thread(() -> {
            Merchant result = null;
            try {
                result = merchantService.register(expectedUser);
            } catch (MerchantServiceException e) {
                expectedErrorMessage = e.getMessage();
            }
            registeredMerchant.complete(result);
        }).start();
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @When("the merchant is being registered")
    public void theMerchantIsBeingRegistered() {
        new Thread(() -> {
            Merchant result = null;
            try {
                result = merchantService.register(expectedUser);
            } catch (MerchantServiceException e) {
                expectedErrorMessage = e.getMessage();
            }
            registeredMerchant.complete(result);
        }).start();
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the {string} event is sent")
    public void theEventIsSent(String eventName) {
        if (eventName.contains("DeregistrationRequested")) {
            Event event = new Event(eventName, new Object[]{expectedUser.getId()});
            assertEquals(event, publishedEvent.join());
        } else if (eventName.contains("RegistrationRequested")) {
            Event event = new Event(eventName, new Object[]{expectedUser});
            assertEquals(event, publishedEvent.join());
        }
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @When("the {string} registration event is sent with non-empty id")
    public void theRegistrationEventIsSentWithNonEmptyId(String eventName) {
        // This step simulate the event created by a downstream service.
        User tmpUser = null;
        if (eventName.equals(CUSTOMER_REGISTERED)) {
            tmpUser = new Customer();
        } else if (eventName.equals(MERCHANT_REGISTERED)) {
            tmpUser = new Merchant();
        }

        tmpUser.setFirstName(expectedUser.getFirstName());
        tmpUser.setLastName(expectedUser.getLastName());
        tmpUser.setCpr(expectedUser.getCpr());
        tmpUser.setId("123");

        if (eventName.equals(CUSTOMER_REGISTERED)) {
            customerService.handleCustomerRegistered(new Event(eventName, new Object[]{tmpUser}));
        } else if (eventName.equals(MERCHANT_REGISTERED)) {
            merchantService.handleMerchantRegistered(new Event(eventName, new Object[]{tmpUser}));
        }
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the merchant is registered and his id is set")
    public void theMerchantIsRegisteredAndHisIdIsSet() {
        assertNotNull(registeredMerchant.join().getId());
    }
}
