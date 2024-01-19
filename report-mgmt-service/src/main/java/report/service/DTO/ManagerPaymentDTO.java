package report.service.DTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;
/*
  ################################
  # Developed by Jakub (s232946) #
  ################################
*/
@Getter
@EqualsAndHashCode
public class ManagerPaymentDTO {
    public int amount;
    public String token;
    public String customerId;
    public String merchantId;

    public ManagerPaymentDTO(int amount, String token, String customerId, String merchantId) {
        this.amount = amount;
        this.token = token;
        this.customerId = customerId;
        this.merchantId = merchantId;
    }

    @Override
    public String toString() {
        return String.format("Payment amount %s, token used %s, with merchant %s and customer %s", amount, token, merchantId, customerId);
    }
}
