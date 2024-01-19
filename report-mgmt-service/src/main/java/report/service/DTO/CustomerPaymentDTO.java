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
public class CustomerPaymentDTO {
    public int amount;
    public String token;
    public String merchantId;

    public CustomerPaymentDTO(int amount, String token, String merchantId) {
        this.amount = amount;
        this.token = token;
        this.merchantId = merchantId;
    }

    @Override
    public String toString() {
        return String.format("Payment amount %s, token used %s, with merchant %s", amount, token, merchantId);
    }
}
