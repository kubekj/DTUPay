package account.service;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/*
  #############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programming #
  #############################################################################
*/
@Getter
@Setter
@EqualsAndHashCode
public class Payment {
    private String customerTokenId;
    private String customerID;
    private String customerBankAccount;
    private String merchantID;
    private String merchantBankAccount;
    private int amount;

    @Override
    public String toString() {
        return String.format("Payment with token %s to merchant %s of %s", getCustomerTokenId(), getMerchantID(), getAmount());
    }
}
