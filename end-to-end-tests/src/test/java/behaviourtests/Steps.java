package behaviourtests;

import classes.*;
import classes.DTO.CustomerPaymentDTO;
import classes.DTO.ManagerPaymentDTO;
import classes.DTO.MerchantPaymentDTO;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import endtoend.*;
import exceptions.CustomerServiceException;
import exceptions.MerchantServiceException;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/*
  ##############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programming #
  ##############################################################################
  ###############################################################################################
  # Reporting Developed by Jakub (s232946) assisted by Andreas (s176334) and Christian (s194578)#
  ###############################################################################################
*/
public class Steps {
    private CustomerRegistrationService customerRegistrationService = new CustomerRegistrationService();
    private CustomerTokenService customerTokenService = new CustomerTokenService();
    private MerchantRegistrationService merchantRegistrationService = new MerchantRegistrationService();
    private MerchantPaymentService merchantpaymentService = new MerchantPaymentService();
    private BankService bank = new BankServiceService().getBankServicePort();
    private CustomerReportService customerReportService = new CustomerReportService();
    private ReportService reportService = new ReportService();
    private MerchantReportService merchantReportService = new MerchantReportService();
    private Customer customer, customerResult;
    private Merchant merchant, merchantResult;
    private String customerDeregisterResult, customerBankAccountId, merchantDeregisterResult, merchantBankAccountId, expectedString, expectedErrorMessage, paymentResult;
    private List<Token> tokens;
    private List<Token> initialTokens;
    private Payment payment;
    private CustomerReport customerReport;
    private ManagerReport managerReport;
    private MerchantReport merchantReport;

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Given("the customer has {int} token")
    public void theCustomerHasToken(int amount) {
        initialTokens = customerTokenService.getTokens(customer.getId(), amount);
        assertEquals(initialTokens.size(), amount);

    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the customer has {int} tokens")
    public void theCustomerHasTokens(int amount) {
        assertEquals(tokens.size(), amount);
        for (Token token : tokens) {
            assertEquals(token.getUserId(), customer.getId());
        }
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @When("another merchant registers with the same CPR")
    public void anotherMerchantRegistersWithTheSameCPR() {
        merchant = new Merchant();
        merchant.setCpr("123456-1234");
        try {
            Merchant merchantResult = merchantRegistrationService.register(merchant);
        } catch (MerchantServiceException exception) {
            expectedErrorMessage = exception.getMessage();
        }
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @When("another customer registers with the same CPR")
    public void anotherCustomerRegistersWithTheSameCPR() {
        customer = new Customer();
        customer.setCpr("123456-1234");
        try {
            Customer customerResult = customerRegistrationService.register(customer);
        } catch (CustomerServiceException exception) {
            expectedErrorMessage = exception.getMessage();
        }
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("an error message is returned saying {string}")
    public void anErrorMessageIsReturnedSaying(String errorMessage) {
        assertEquals(errorMessage, expectedErrorMessage);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Given("the customer with a bank account with balance {int}")
    public void theCustomerWithABankAccountWithBalance(Integer amount) throws BankServiceException_Exception {
        dtu.ws.fastmoney.User bankCustomer = new dtu.ws.fastmoney.User();
        bankCustomer.setFirstName("James");
        bankCustomer.setLastName("Bond");
        bankCustomer.setCprNumber("123456-1234");
        customerBankAccountId = bank.createAccountWithBalance(bankCustomer, BigDecimal.valueOf(amount));
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Given("a customer is registered with DTU Pay")
    public void aCustomerIsRegisteredWithDTUPay() throws CustomerServiceException {
        customer = new Customer();
        customer.setFirstName("James");
        customer.setLastName("Bond");
        customer.setCpr("123456-1234");
        customer.setBankAccount(customerBankAccountId);
        Customer customerResult = customerRegistrationService.register(customer);
        customer.setId(customerResult.getId());
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Given("the merchant with a bank account with balance {int}")
    public void theMerchantWithABankAccountWithBalance(Integer amount) throws BankServiceException_Exception {
        dtu.ws.fastmoney.User bankMerchant = new dtu.ws.fastmoney.User();
        bankMerchant.setFirstName("Anders");
        bankMerchant.setLastName("And");
        bankMerchant.setCprNumber("123456-0000");
        merchantBankAccountId = bank.createAccountWithBalance(bankMerchant, BigDecimal.valueOf(amount));
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Given("a merchant is registered with DTU Pay")
    public void aMerchantIsRegisteredWithDTUPay() throws MerchantServiceException {
        merchant = new Merchant();
        merchant.setFirstName("Anders");
        merchant.setLastName("And");
        merchant.setCpr("123456-0000");
        merchant.setBankAccount(merchantBankAccountId);
        Merchant merchantResult = merchantRegistrationService.register(merchant);
        merchant.setId(merchantResult.getId());
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Given("the customer requests {int} tokens")
    public void theCustomerRequestsTokens(int amount) {
        tokens = customerTokenService.getTokens(customer.getId(), amount);
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @When("the customer has handed a token to the merchant")
    public void theCustomerHasHandedATokenToTheMerchant() {
        payment = new Payment();
        payment.setCustomerTokenId(tokens.get(0).getTokenId());
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @When("the merchant initiates a payment of {int}")
    public void theMerchantInitiatesAPaymentOf(Integer amount) {
        payment.setMerchantID(merchant.getId());
        payment.setAmount(amount);
        try {
            paymentResult = merchantpaymentService.pay(payment);
        } catch (MerchantServiceException exception) {
            expectedErrorMessage = exception.getMessage();
        }

    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the payment is unsuccessful")
    public void thePaymentIsUnsuccessful() {
        assertEquals("Unsuccessful payment", expectedErrorMessage);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertEquals("Successful payment", paymentResult);
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the balance of the customer is {int}")
    public void theBalanceOfTheCustomerIs(Integer amount) throws BankServiceException_Exception {
        assertEquals(BigDecimal.valueOf(amount), bank.getAccount(customerBankAccountId).getBalance());

    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the balance of the merchant is {int}")
    public void theBalanceOfTheMerchantIs(Integer amount) throws BankServiceException_Exception {
        assertEquals(BigDecimal.valueOf(amount), bank.getAccount(merchantBankAccountId).getBalance());
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Given("an unregistered customer with empty id")
    public void anUnregisteredCustomerWithEmptyId() {
        customer = new Customer();
        customer.setFirstName("James");
        customer.setLastName("Bond");
        customer.setCpr("123456-1234");
        assertNull(customer.getId());
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @When("the customer is being registered")
    public void theCustomerIsBeingRegistered() throws CustomerServiceException {
        customerResult = customerRegistrationService.register(customer);
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the customer is registered")
    public void theCustomerIsRegistered() {
        customer.setId(customerResult.getId());
        assertEquals(customer, customerResult);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the customer has a non empty id")
    public void theCustomerHasANonEmptyId() {
        assertNotNull(customerResult.getId());
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @When("the customer is being deregistered")
    public void theCustomerIsBeingDeregistered() {
        try {
            customerDeregisterResult = customerRegistrationService.deregister(customer.getId());
        } catch (CustomerServiceException exception) {
            expectedErrorMessage = exception.getMessage();
        }
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the customer is deregistered")
    public void theCustomerIsDeregistered() {
        expectedString = customer.toString() + " is deleted";
        assertEquals(customerDeregisterResult, expectedString);
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Given("an unregistered merchant with empty id")
    public void anUnregisteredMerchantWithEmptyId() {
        merchant = new Merchant();
        merchant.setFirstName("James");
        merchant.setLastName("Bond");
        merchant.setCpr("123456-1234");
        assertNull(merchant.getId());
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @When("the merchant is being registered")
    public void theMerchantIsBeingRegistered() throws MerchantServiceException {
        merchantResult = merchantRegistrationService.register(merchant);
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the merchant is registered")
    public void theMerchantIsRegistered() {
        merchant.setId(merchantResult.getId());
        assertEquals(merchant, merchantResult);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the merchant has a non empty id")
    public void theMerchantHasANonEmptyId() {
        assertNotNull(merchantResult.getId());
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @When("the merchant is being deregistered")
    public void theMerchantIsBeingDeregistered() {
        try {
            merchantDeregisterResult = merchantRegistrationService.deregister(merchant.getId());
        } catch (MerchantServiceException exception) {
            expectedErrorMessage = exception.getMessage();
        }
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the merchant is deregistered")
    public void theMerchantIsDeregistered() {
        expectedString = merchant.toString() + " is deleted";
        assertEquals(merchantDeregisterResult, expectedString);
    }

    /*
      ################################
      # Developed by Jakub (s232946) #
      ################################
    */
    @When("the customer ask for a report")
    public void theCustomerAskForAReport() {
        customerReport = customerReportService.getReport(customer.getId());
    }

    /*
      ################################
      # Developed by Jakub (s232946) #
      ################################
    */
    @When("the manager ask for a report")
    public void theManagerAskForAReport() {
        managerReport = reportService.getReport();
    }

    /*
      ################################
      # Developed by Jakub (s232946) #
      ################################
    */
    @When("the merchant ask for a report")
    public void theMerchantAskForAReport() {
        merchantReport = merchantReportService.getReport(merchant.getId());
    }

    /*
      ################################
      # Developed by Jakub (s232946) #
      ################################
    */
    @Then("a report is returned to the manager")
    public void aReportIsReturnedToTheManger() {
        assertNotNull(managerReport);
    }

    /*
      ################################
      # Developed by Jakub (s232946) #
      ################################
    */
    @Then("a report is returned to the customer")
    public void aReportIsReturnedToTheCustomer() {
        assertNotNull(customerReport);
    }

    /*
      ################################
      # Developed by Jakub (s232946) #
      ################################
    */
    @Then("a report is returned to the merchant")
    public void aReportIsReturnedToTheMerchant() {
        assertNotNull(merchantReport);
    }

    /*
      ################################
      # Developed by Jakub (s232946) #
      ################################
    */
    @Then("the customer report contains the payment")
    public void theCustomerReportContainsThePayment() {
        CustomerPaymentDTO paymentForCustomer = new CustomerPaymentDTO(
                payment.getAmount(),
                payment.getCustomerTokenId(),
                payment.getMerchantID()
        );
        assertFalse(customerReport.getCustomerPayments().isEmpty());
        assertTrue(customerReport.getCustomerPayments().contains(paymentForCustomer));
    }

    /*
      ################################
      # Developed by Jakub (s232946) #
      ################################
    */
    @Then("the manager report contains the payment")
    public void theManagerReportContainsThePayment() {
        ManagerPaymentDTO paymentForCustomer = new ManagerPaymentDTO(
                payment.getAmount(),
                payment.getCustomerTokenId(),
                customer.getId(),
                payment.getMerchantID()
        );
        assertFalse(managerReport.getManagerPayments().isEmpty());
        assertTrue(managerReport.getManagerPayments().contains(paymentForCustomer));
    }

    /*
      ################################
      # Developed by Jakub (s232946) #
      ################################
    */
    @And("the merchant report contains the payment")
    public void theMerchantReportContainsThePayment() {
        MerchantPaymentDTO paymentForMerchant = new MerchantPaymentDTO(
                payment.getAmount(),
                payment.getCustomerTokenId()
        );
        assertFalse(merchantReport.getMerchantPayments().isEmpty());
        assertTrue(merchantReport.getMerchantPayments().contains(paymentForMerchant));
    }

    @After
    public void deleteUser() throws BankServiceException_Exception {
        try {
            this.bank.retireAccount(this.customerBankAccountId);
        } catch (BankServiceException_Exception exception) {
            Assertions.assertEquals("Account does not exist", exception.getMessage());
        }

        try {
            this.bank.retireAccount(this.merchantBankAccountId);
        } catch (BankServiceException_Exception exception) {
            Assertions.assertEquals("Account does not exist", exception.getMessage());
        }

        try {
            customerRegistrationService.deregister(customer.getId());
        } catch (CustomerServiceException exception) {
            Assertions.assertEquals("Customer does not exist", exception.getMessage());
        } catch (NullPointerException exception) {
        }

        try {
            customerRegistrationService.deregister(merchant.getId());
        } catch (CustomerServiceException exception) {
            Assertions.assertEquals("Customer does not exist", exception.getMessage());
        } catch (NullPointerException exception) {
        }

        try {
            customerRegistrationService.deregister(customerResult.getId());
        } catch (CustomerServiceException exception) {
            Assertions.assertEquals("Customer does not exist", exception.getMessage());
        } catch (NullPointerException exception) {
        }

        try {
            customerRegistrationService.deregister(merchantResult.getId());
        } catch (CustomerServiceException exception) {
            Assertions.assertEquals("Customer does not exist", exception.getMessage());
        } catch (NullPointerException exception) {
        }
    }
}

