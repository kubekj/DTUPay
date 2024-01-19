package behaviourtests;

import account.service.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static utilities.EventTopics.*;
import static utilities.Responses.RESPONSE_CUSTOMER_DOES_NOT_EXIST;
import static utilities.Responses.RESPONSE_MERCHANT_DOES_NOT_EXIST;

/*
  #############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programming #
  #############################################################################

  Step file for AccountMgmtService feature file
*/
public class AccountMgmtServiceSteps {
    private MessageQueue queue = mock(MessageQueue.class);
    private AccountMgmtService accountMgmtService = new AccountMgmtService(queue);
    private User user, expectedUser;
    private Customer customer;
    private Merchant merchant;
    private String userId, customerId, merchantId;
    private Payment expectedBankPayment;

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @When("another {string} event is received with the same CPR")
    public void anotherEventIsReceivedWithTheSameCPR(String eventName) {
        if (eventName.equals(CUSTOMER_REGISTRATION_REQUESTED)) {
            user = new Customer();
        } else if (eventName.equals(MERCHANT_REGISTRATION_REQUESTED)) {
            user = new Merchant();
        }
        user.setFirstName("James");
        user.setLastName("Bond");
        user.setCpr("123456-1234");
        assertNull(user.getId());
        userId = accountMgmtService.handleUserRegistrationRequested(new Event(eventName, new Object[]{user}));
        user.setId(userId);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the {string} registration event is sent")
    public void theRegistrationEventIsSent(String eventName) {
        if (eventName.equals(CUSTOMER_NOT_REGISTERED)) {
            expectedUser = new Customer();
        } else if (eventName.equals(MERCHANT_NOT_REGISTERED)) {
            expectedUser = new Merchant();
        }
        expectedUser.setFirstName(user.getFirstName());
        expectedUser.setLastName(user.getLastName());
        expectedUser.setCpr(user.getCpr());
        expectedUser.setId(user.getId());
        var event = new Event(eventName, new Object[]{expectedUser});
        verify(queue).publish(event);
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the user is not assigned an id")
    public void theUserIsNotAssignedAnId() {
        assertNull(expectedUser.getId());
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the user is not registered")
    public void theUserIsNotRegistered() {
        assertNull(accountMgmtService.getUser(expectedUser.getId()));
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Given("a registered customer")
    public void aRegisteredCustomer() {
        customer = new Customer();
        customer.setFirstName("James");
        customer.setLastName("Bond");
        customer.setCpr("123456-1234");
        customer.setBankAccount("1");
        customerId = accountMgmtService.addUser(customer);
        assertNotNull(accountMgmtService.getUser(customerId));
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Given("a registered merchant")
    public void aRegisteredMerchant() {
        merchant = new Merchant();
        merchant.setFirstName("James");
        merchant.setLastName("Bond");
        merchant.setCpr("123456-1234");
        merchant.setBankAccount("2");
        merchantId = accountMgmtService.addUser(merchant);
        assertNotNull(accountMgmtService.getUser(merchantId));
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @When("a {string} bank account event is received")
    public void aBankAccountEventIsReceived(String eventName) {
        Payment payment = new Payment();
        payment.setCustomerID(customer.getId());
        payment.setMerchantID(merchant.getId());
        payment.setAmount(100);
        accountMgmtService.handleUserIdReturned(new Event(eventName, new Object[]{payment}));
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the {string} bank account event is sent")
    public void theBankAccountEventIsSent(String eventName) {
        expectedBankPayment = new Payment();
        expectedBankPayment.setMerchantID(merchant.getId());
        expectedBankPayment.setCustomerID(customer.getId());
        expectedBankPayment.setCustomerBankAccount(customer.getBankAccount());
        expectedBankPayment.setMerchantBankAccount(merchant.getBankAccount());
        expectedBankPayment.setAmount(100);
        var event = new Event(eventName, new Object[]{expectedBankPayment});
        verify(queue).publish(event);
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the merchant bank account is retrieved")
    public void theMerchantBankAccountIsRetrieved() {
        assertEquals(expectedBankPayment.getMerchantBankAccount(), accountMgmtService.getUser(merchant.getId()).getBankAccount());
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the customer bank account is retrieved")
    public void theCustomerBankAccountIsRetrieved() {
        assertEquals(expectedBankPayment.getCustomerBankAccount(), accountMgmtService.getUser(customer.getId()).getBankAccount());
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @When("a {string} deregistration event is received")
    public void aDeregistrationEventIsReceived(String eventName) {
        if (eventName.equals(CUSTOMER_DEREGISTRATION_REQUESTED)) {
            user = new Customer();
        } else if (eventName.equals(MERCHANT_DEREGISTRATION_REQUESTED)) {
            user = new Merchant();
        }
        user.setFirstName("James");
        user.setLastName("Bond");
        user.setCpr("123456-1234");
        user.setId(userId);
        accountMgmtService.handleUserDeregistrationRequested(new Event(eventName, new Object[]{user.getId()}));
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("the {string} deregistration event is sent")
    public void theDeregistrationEventIsSent(String eventName) {
        String expectedString = null;
        if (eventName.contains("Deregistered")) {
            expectedString = user.toString() + " is deleted";
        } else if (eventName.equals(CUSTOMER_DOES_NOT_EXIST)) {
            expectedString = RESPONSE_CUSTOMER_DOES_NOT_EXIST;
        } else if (eventName.equals(MERCHANT_DOES_NOT_EXIST)) {
            expectedString = RESPONSE_MERCHANT_DOES_NOT_EXIST;
        }

        var event = new Event(eventName, new Object[]{expectedString});
        verify(queue).publish(event);
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the user is deregistered")
    public void theUserIsDeregistered() {
        assertNull(accountMgmtService.getUser(user.getId()));
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @When("a {string} event is received")
    public void aEventIsReceived(String eventName) {
        if (eventName.equals(CUSTOMER_REGISTRATION_REQUESTED)) {
            user = new Customer();
        } else if (eventName.equals(MERCHANT_REGISTRATION_REQUESTED)) {
            user = new Merchant();
        }
        user.setFirstName("James");
        user.setLastName("Bond");
        user.setCpr("123456-1234");
        assertNull(user.getId());
        userId = accountMgmtService.handleUserRegistrationRequested(new Event(eventName, new Object[]{user}));
        user.setId(userId);
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("the {string} event is sent")
    public void theEventIsSent(String eventName) {
        if (eventName.equals(CUSTOMER_REGISTERED)) {
            expectedUser = new Customer();
        } else if (eventName.equals(MERCHANT_REGISTERED)) {
            expectedUser = new Merchant();
        }
        expectedUser.setFirstName(user.getFirstName());
        expectedUser.setLastName(user.getLastName());
        expectedUser.setCpr(user.getCpr());
        expectedUser.setId(user.getId());
        var event = new Event(eventName, new Object[]{expectedUser});
        verify(queue).publish(event);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("a customer is assigned an id")
    public void aCustomerIsAssignedAnId() {
        // Write code here that turns the phrase above into concrete actions
        assertTrue(accountMgmtService.getUser(expectedUser.getId()) instanceof Customer);
        assertNotNull(expectedUser.getId());
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("a customer is registered")
    public void aCustomerIsRegistered() {
        assertTrue(accountMgmtService.getUser(expectedUser.getId()) instanceof Customer);
        assertEquals(accountMgmtService.getUser(expectedUser.getId()), expectedUser);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    @Then("a merchant is assigned an id")
    public void aMerchantIsAssignedAnId() {
        assertTrue(accountMgmtService.getUser(expectedUser.getId()) instanceof Merchant);
        assertNotNull(expectedUser.getId());
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    @Then("a merchant is registered")
    public void aMerchantIsRegistered() {
        assertTrue(accountMgmtService.getUser(expectedUser.getId()) instanceof Merchant);
        assertEquals(accountMgmtService.getUser(expectedUser.getId()), expectedUser);
    }
}

