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
public class MerchantPaymentDTO {
    public int amount;
    public String token;

    public MerchantPaymentDTO(int amount, String token) {
        this.amount = amount;
        this.token = token;
    }

    @Override
    public String toString() {
        return String.format("Payment amount %s, token used %s", amount, token);
    }
}
