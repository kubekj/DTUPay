package facade.classes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@EqualsAndHashCode
@Getter
@Setter
public class Token {
    private String tokenId;
    private String userId;
    private boolean isConsumed = false;

    public Token() {
    }

    public Token(String userId) {
        this.tokenId = generateTokenId();
        this.userId = userId;
    }

    private String generateTokenId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public void consume() {
        isConsumed = true;
    }

    @Override
    public String toString() {
        return "Token{" +
                "isConsumed=" + isConsumed +
                ", tokenId='" + tokenId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
