package facade.classes;

import facade.classes.DTO.CustomerPaymentDTO;
import facade.exceptions.ReportCreationException;
import facade.service.PaymentListWrapper;
import lombok.Getter;

import java.util.ArrayList;

/*
  ################################
  # Developed by Jakub (s232946) #
  ################################
*/
@Getter
public class CustomerReport extends Report {
    private final ArrayList<CustomerPaymentDTO> customerPayments = new ArrayList<>();

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

            customerPayments.add(paymentDTO);
        }
    }

    public String toString() {
        return getCustomerPayments().toString();
    }
}
