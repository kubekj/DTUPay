package classes;

import classes.DTO.CustomerPaymentDTO;
import exceptions.ReportCreationException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/*
  ################################
  # Developed by Jakub (s232946) #
  ################################
*/
@Getter
@Setter
public class CustomerReport extends Report {
    private ArrayList<CustomerPaymentDTO> customerPayments = new ArrayList<>();

    public CustomerReport() {
        super();
    }

    @Override
    public void generateReport(PaymentListWrapper paymentListWrapper) throws ReportCreationException {
        var payments = paymentListWrapper.getPayments();

        if (payments.isEmpty()) throw new ReportCreationException();

        for (Payment payment : payments) {
            CustomerPaymentDTO paymentDTO = new CustomerPaymentDTO(
                    payment.getAmount(),
                    payment.getCustomerTokenId(),
                    payment.getMerchantID());

            this.customerPayments.add(paymentDTO);
        }
    }

    @Override
    public String toString() {
        return getCustomerPayments().toString();
    }
}
