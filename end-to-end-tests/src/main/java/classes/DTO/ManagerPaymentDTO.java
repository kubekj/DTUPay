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
public class ManagerPaymentDTO {
    private int amount;
    private String token;
    private String customerId;
    private String merchantId;

    public ManagerPaymentDTO(int amount, String token, String customerId, String merchantId) {
        this.amount = amount;
        this.token = token;
        this.customerId = customerId;
        this.merchantId = merchantId;
    }

    public ManagerPaymentDTO() {
    }

    @Override
    public String toString() {
        return String.format("Payment amount %s, token used %s, with merchant %s and customer %s", amount, token, merchantId, customerId);
    }
}
