package token.service;

import messaging.Event;
import messaging.MessageQueue;

import java.util.Comparator;
import java.util.List;

import static token.service.EventTopics.*;

public class TokenService {

    MessageQueue queue;

    public TokenService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler(ISSUE_TOKEN_REQUESTED, this::handleIssueTokenRequested);
        this.queue.addHandler(TOKEN_CONSUMPTION_REQUESTED, this::handleTokenConsumptionRequested);
        this.queue.addHandler(PAYMENT_REQUESTED, this::handlePaymentRequested);
    }

    public void handleIssueTokenRequested(Event ev) {
        var userid = ev.getArgument(0, String.class);
        var tokenAmount = ev.getArgument(1, Integer.class);
        List<Token> tokens = TokenManagement.getInstance().getNotConsumedTokensForUser(userid);

        if ((tokens.size() + tokenAmount <= 6) && (tokens.size() <= 1)) {
            for (int i = 0; i < tokenAmount; i++) {
                Token token = TokenManagement.getInstance().generateToken(userid);
                tokens.add(token);
            }

            tokens.sort(Comparator.comparing(Token::getTokenId));

            // Needed for parsing list of tokens
            TokenListWrapper tokenListWrapper = new TokenListWrapper();
            tokenListWrapper.setTokens(tokens);

            Event event = new Event(TOKEN_ISSUED, new Object[]{tokenListWrapper});
            queue.publish(event);
        } else {
            tokens.sort(Comparator.comparing(Token::getTokenId));
            TokenListWrapper tokenListWrapper = new TokenListWrapper();
            tokenListWrapper.setTokens(tokens);
            Event event = new Event(TOKEN_NOT_ISSUED, new Object[]{tokenListWrapper});
            queue.publish(event);
        }
    }

    public void handleTokenConsumptionRequested(Event ev) {
        var payment = ev.getArgument(0, Payment.class);
        var tokenId = payment.getCustomerTokenId();
        Token token = TokenManagement.getInstance().getToken(tokenId);
        if (token != null) {
            token.consume();
            Event event = new Event(TOKEN_IS_CONSUMED, new Object[]{payment});
            queue.publish(event);
        } else {
            String returnString = "Couldn't find token";
            Event event = new Event(MONEY_NOT_TRANSFERRED, new Object[]{returnString});
            queue.publish(event);
        }

    }


    public void handlePaymentRequested(Event ev) {
        var payment = ev.getArgument(0, Payment.class);
        var id = TokenManagement.getInstance().getUserIdFromToken(payment.getCustomerTokenId());
        payment.setCustomerID(id);
        Event event = new Event(USER_ID_RETURNED, new Object[]{payment});
        queue.publish(event);
    }
}
