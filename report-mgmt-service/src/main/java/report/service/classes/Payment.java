package report.service.classes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
/*
  ################################
  # Developed by Jakub (s232946) #
  ################################
*/
@Getter
@Setter
@EqualsAndHashCode
public class Payment {
    public Payment() {
    }

    public Payment(int amount, String customerTokenId, String customerID, String merchantID) {
        this.customerTokenId = customerTokenId;
        this.customerID = customerID;
        this.merchantID = merchantID;
        this.amount = amount;
    }

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
