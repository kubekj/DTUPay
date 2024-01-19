package behaviourtests;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import payment.service.*;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static utilities.EventTopics.MONEY_NOT_TRANSFERRED;
import static utilities.EventTopics.MONEY_TRANSFERRED;
import static utilities.Responses.SUCCESSFUL_PAYMENT;
import static utilities.Responses.UNSUCCESSFUL_PAYMENT;

/*
  ##############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programming #
  ##############################################################################
  ###############################################################################################
  # Reporting Developed by Jakub (s232946) assisted by Andreas (s176334) and Christian (s194578)#
  ###############################################################################################
*/
public class PaymentSteps {
    private MessageQueue queue = mock(MessageQueue.class);
    private PaymentService paymentService = new PaymentService(queue);
    private BankPayment bankPayment, expectedBankPayment;
    private String customerBankAccountID, merchantBankAccountID;
    private BankService bank = new BankServiceService().getBankServicePort();
    private boolean successful = false;
    Customer customer;
    Merchant merchant;
    Payment payment;
    PaymentListWrapper paymentListWrapper, expectedPaymentsWrapper;

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the payment to the bank is unsuccessful")
    public void thePaymentToTheBankIsUnsuccessful() {
        assertFalse(successful);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Given("a customer with a bank account with balance {int}")
    public void aCustomerWithABankAccountWithBalance(int amount) throws BankServiceException_Exception {
        String cpr = "123321-8888";
        String firstName = "Alice";
        String lastName = "Customer";
        dtu.ws.fastmoney.User user = new dtu.ws.fastmoney.User();
        user.setCprNumber(cpr);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        this.customerBankAccountID = this.bank.createAccountWithBalance(user, BigDecimal.valueOf(amount));
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Given("a merchant with a bank account with balance {int}")
    public void aMerchantWithABankAccountWithBalance(int amount) throws BankServiceException_Exception {
        String cpr = "123321-1337";
        String firstName = "Bob";
        String lastName = "Merchant";
        dtu.ws.fastmoney.User user = new dtu.ws.fastmoney.User();
        user.setCprNumber(cpr);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        this.merchantBankAccountID = this.bank.createAccountWithBalance(user, BigDecimal.valueOf(amount));
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @When("a {string} event is received with amount {int}")
    public void aEventIsReceivedWithAmount(String eventName, int amount) {
        bankPayment = new BankPayment();
        bankPayment.setCustomerBankAccount(customerBankAccountID);
        bankPayment.setMerchantBankAccount(merchantBankAccountID);
        bankPayment.setAmount(amount);
        successful = paymentService.handleTokenConsumed(new Event(eventName, new Object[]{bankPayment}));
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the payment to the bank is successful")
    public void thePaymentToTheBankIsSuccessful() {
        assertTrue(successful);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the balance of the customer at the bank is {int} kr")
    public void theBalanceOfTheCustomerAtTheBankIsKr(Integer customerBalance) throws BankServiceException_Exception {
        assertEquals(BigDecimal.valueOf(customerBalance), bank.getAccount(customerBankAccountID).getBalance());
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the balance of the merchant at the bank is {int} kr")
    public void theBalanceOfTheMerchantAtTheBankIsKr(Integer merchantBalance) throws BankServiceException_Exception {
        assertEquals(BigDecimal.valueOf(merchantBalance), bank.getAccount(merchantBankAccountID).getBalance());
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the {string} event is sent")
    public void theEventIsSent(String eventName) {
        Event event = null;
        if (eventName.equals(MONEY_TRANSFERRED)) {
            expectedBankPayment = new BankPayment();
            expectedBankPayment.setAmount(bankPayment.getAmount());
            expectedBankPayment.setCustomerBankAccount(bankPayment.getCustomerBankAccount());
            expectedBankPayment.setMerchantBankAccount(bankPayment.getMerchantBankAccount());
            event = new Event(eventName, new Object[]{SUCCESSFUL_PAYMENT});
        } else if (eventName.equals(MONEY_NOT_TRANSFERRED)) {
            event = new Event(eventName, new Object[]{UNSUCCESSFUL_PAYMENT});
        }

        verify(queue).publish(event);
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Given("a customer with id {string} has made payment with a merchant with id {string}")
    public void aCustomerWithIdHasMadePaymentContainingMerchantWithId(String customerID, String merchantID) {
        customer = new Customer();
        customer.setId(customerID);

        merchant = new Merchant();
        merchant.setId(merchantID);

        payment = new Payment();
        payment.setCustomerID(customer.getId());
        payment.setCustomerTokenId("1337");
        payment.setMerchantID(merchantID);
        paymentService.getPayments().add(payment);
    }

    /*
    ################################
    # Responsible: Jakub (s232946) #
    ################################
    */
    @When("a {string} event from customer is received")
    public void aEventFromCustomerIsReceived(String eventName) {
        Event event = new Event(eventName, new Object[]{customer.getId()});
        expectedPaymentsWrapper = paymentService.handleCustomerReportRequested(event);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @When("a {string} event from manager is received")
    public void aEventFromManagerIsReceived(String eventName) {
        Event event = new Event(eventName);
        expectedPaymentsWrapper = paymentService.handleManagerReportRequested(event);
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @When("a {string} event from merchant is received")
    public void aEventFromMerchantIsReceived(String eventName) {
        Event event = new Event(eventName, new Object[]{merchant.getId()});
        expectedPaymentsWrapper = paymentService.handleMerchantReportRequested(event);
    }

    /*
    ################################
    # Responsible: Jakub (s232946) #
    ################################
    */
    @Then("the {string} event is sent with a list of payments")
    public void theEventIsSentWithAListOfPayments(String eventName) {
        ArrayList<Payment> payments = new ArrayList<>();
        payments.add(payment);
        paymentListWrapper = new PaymentListWrapper(payments);
        var event = new Event(eventName, new Object[]{paymentListWrapper});
        verify(queue).publish(event);
    }

    /*
    ################################
    # Responsible: Jakub (s232946) #
    ################################
    */
    @And("the list contains correct payments")
    public void theListContainsCorrectPayments() {
        assertEquals(paymentListWrapper.getPayments(), expectedPaymentsWrapper.getPayments());
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @After
    public void deleteUser() {
        try {
            this.bank.retireAccount(this.merchantBankAccountID);
        } catch (BankServiceException_Exception exception) {
            assertEquals("Account does not exist", exception.getMessage());
        }

        try {
            this.bank.retireAccount(this.customerBankAccountID);
        } catch (BankServiceException_Exception exception) {
            assertEquals("Account does not exist", exception.getMessage());
        }
    }
}

