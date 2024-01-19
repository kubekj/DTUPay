package behaviourtests;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import token.service.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TokenManagementServiceSteps {

    User customer;
    List<Token> unconsumedTokens = new ArrayList<>();
    MessageQueue queue = mock(MessageQueue.class);
    TokenService service = new TokenService(queue);
    TokenManagement tokenManagement = TokenManagement.getInstance();
    Payment payment;
    Payment expectedPayment;
    Token userToken;

    @Given("the customer has a token")

    public void theCustomerHasAToken() {
        userToken = tokenManagement.generateToken(customer.getId());
    }

    @When("a {string} event is received")
    public void aEventIsReceived(String eventName) {
        payment = new Payment();
        payment.setMerchantID("1");
        payment.setCustomerTokenId(userToken.getTokenId());
        payment.setAmount(100);
        assertNull(payment.getCustomerID());
        service.handlePaymentRequested(new Event(eventName, new Object[]{payment}));
    }

    @Then("the {string} event is sent")
    public void theEventIsSent(String eventName) {
        // UserIDReturned
        expectedPayment = new Payment();
        expectedPayment.setMerchantID("1");
        expectedPayment.setCustomerTokenId(userToken.getTokenId());
        expectedPayment.setAmount(100);
        var id = tokenManagement.getUserIdFromToken(expectedPayment.getCustomerTokenId());
        expectedPayment.setCustomerID(id.toString());
        var event = new Event(eventName, new Object[]{expectedPayment});
        verify(queue).publish(event);
    }

    @Then("the customer ID is retrieved")
    public void theCustomerIDIsRetrieved() {
        assertEquals(customer.getId().toString(), expectedPayment.getCustomerID());
    }

    @Given("a customer with id {string}")
    public void aCustomerWithId(String id) {
        customer = new User(id);
        assertNotNull(customer);
    }

    @Given("{int} token is generated for the customer")
    public void aTokenIsGeneratedForTheCustomer(int tokenAmount) {
        for (int i = 0; i < tokenAmount; i++) {
            Token token = tokenManagement.generateToken(customer.getId());
            unconsumedTokens.add(token);
        }
        assertEquals(tokenAmount, unconsumedTokens.size());
        assertEquals(tokenAmount, tokenManagement.getNotConsumedTokensForUser(customer.getId()).size());
    }

    @When("a IssueTokenRequested event for the customer is received")
    public void aIssueTokenRequestedEventForTheCustomerIsReceived() {
        service.handleIssueTokenRequested(new Event("IssueTokenRequested", new Object[]{customer.getId(), 1}));
    }

    @When("a TokenConsumptionRequested event for the customer is received")
    public void aTokenConsumptionRequestedEventForTheCustomerIsReceived() {
        String tokenId = unconsumedTokens.get(0).getTokenId();
        payment = new Payment();
        payment.setCustomerTokenId(tokenId);
        service.handleTokenConsumptionRequested(new Event("TokenConsumptionRequested", new Object[]{payment}));
    }

    @Then("the token is valid")
    public void theTokenIsValid() {
        assertTrue(tokenManagement.validateToken(unconsumedTokens.get(0).getTokenId()));
    }

    @Then("the token is invalid")
    public void theTokenIsInvalid() {
        assertFalse(tokenManagement.validateToken(unconsumedTokens.get(0).getTokenId()));
    }

    @Then("the TokenIssued event is sent")
    public void theTokenIssuedEventIsSent() {
        unconsumedTokens = tokenManagement.getNotConsumedTokensForUser(customer.getId());
        unconsumedTokens.sort(Comparator.comparing(Token::getTokenId));
        TokenListWrapper tokenListWrapper = new TokenListWrapper();
        tokenListWrapper.setTokens(unconsumedTokens);
        var event = new Event("TokenIssued", new Object[]{tokenListWrapper});
        verify(queue).publish(event);
    }

    @Then("the TokenNotIssued event is sent")
    public void theTokenNotIssuedEventIsSent() {
        TokenListWrapper tokenListWrapper = new TokenListWrapper();
        unconsumedTokens.sort(Comparator.comparing(Token::getTokenId));
        tokenListWrapper.setTokens(unconsumedTokens);
        var event = new Event("TokenNotIssued", new Object[]{tokenListWrapper});
        verify(queue).publish(event);
    }

    @Then("the TokenIsConsumed event is sent")
    public void theTokenIsConsumedEventIsSent() {
        Token updatedToken = tokenManagement.getToken(unconsumedTokens.get(0).getTokenId());
        payment = new Payment();
        payment.setCustomerTokenId(updatedToken.getTokenId());
        var event = new Event("TokenIsConsumed", new Object[]{payment});
        verify(queue).publish(event);
    }

    @Then("the token created belongs to the customer")
    public void theTokenBelongsToCustomer() {
        assertEquals(1, unconsumedTokens.size());
        assertEquals(1, tokenManagement.getNotConsumedTokensForUser(customer.getId()).size());
        assertEquals(customer.getId(), tokenManagement.getUserIdFromToken(unconsumedTokens.get(0).getTokenId()));
    }

    @When("a IssueTokenRequested event for the customer is received for {int} tokens")
    public void aIssueTokenRequestedEventForTheCustomerIsReceivedForTokens(int tokenAmount) {
        service.handleIssueTokenRequested(new Event("IssueTokenRequested", new Object[]{customer.getId(), tokenAmount}));
    }

    @Then("the customer has {int} unconsumed tokens")
    public void theCustomerHasTokens(int tokenAmount) {
        assertEquals(tokenAmount, tokenManagement.getNotConsumedTokensForUser(customer.getId()).size());
    }


}
