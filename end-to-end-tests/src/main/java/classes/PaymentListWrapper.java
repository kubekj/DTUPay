package classes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
  ################################
  # Developed by Jakub (s232946) #
  ################################
*/
@Setter
@Getter
@EqualsAndHashCode
public class PaymentListWrapper {
    public PaymentListWrapper(List<Payment> payments) {
        this.payments = payments;
    }

    public PaymentListWrapper() {
    }

    private List<Payment> payments;

    @Override
    public String toString() {
        return getPayments().toString();
    }

}
