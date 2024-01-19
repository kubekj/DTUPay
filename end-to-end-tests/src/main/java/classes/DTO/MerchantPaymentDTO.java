package classes.DTO;

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
public class MerchantPaymentDTO {
    public int amount;
    public String token;

    public MerchantPaymentDTO(int amount, String token) {
        this.amount = amount;
        this.token = token;
    }

    public MerchantPaymentDTO() {
    }

    @Override
    public String toString() {
        return String.format("Payment amount %s, token used %s", amount, token);
    }
}
