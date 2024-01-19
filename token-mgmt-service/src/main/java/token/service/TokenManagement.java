package token.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenManagement {
    private static TokenManagement instance = null;

    public static synchronized TokenManagement getInstance() {
        if (instance == null) {
            instance = new TokenManagement();
        }
        return instance;
    }

    private Map<String, Token> tokenMap = new HashMap<>();

    public Token generateToken(String userId) {
        Token token = new Token(userId);
        tokenMap.put(token.getTokenId(), token);
        return token;
    }

    public boolean validateToken(String tokenId) {
        if (tokenMap.containsKey(tokenId)) {
            Token token = tokenMap.get(tokenId);
            return !token.isConsumed();
        }
        return false;
    }

    public String getUserIdFromToken(String tokenId) {
        if (validateToken(tokenId)) {
            return tokenMap.get(tokenId).getUserId();
        }
        return null;
    }

    public List<Token> getNotConsumedTokensForUser(String userid) {
        List<Token> notConsumedTokens = new ArrayList<>();
        for (Token token : tokenMap.values()) {
            if (token.getUserId().equals(userid) && !token.isConsumed()) {
                notConsumedTokens.add(token);
            }
        }
        return notConsumedTokens;
    }

    public Token getToken(String tokenId) {

        return tokenMap.get(tokenId);
    }

}
