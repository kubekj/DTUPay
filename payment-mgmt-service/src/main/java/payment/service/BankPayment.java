package payment.service;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/*
  ##############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programming #
  ##############################################################################
*/
@Getter
@Setter
@EqualsAndHashCode
public class BankPayment {
    private String customerBankAccount;
    private String merchantBankAccount;
    private int amount;

    @Override
    public String toString() {
        return String.format("Payment from %s to %s of %s", getCustomerBankAccount(), getMerchantBankAccount(), getAmount());
    }
}
