package report.service.classes;

import lombok.Getter;
import report.service.DTO.CustomerPaymentDTO;
import report.service.exceptions.ReportCreationException;

import java.util.ArrayList;
/*
  ################################
  # Developed by Jakub (s232946) #
  ################################
*/
@Getter
public class CustomerReport extends Report {
    public ArrayList<CustomerPaymentDTO> customerPayments = new ArrayList<>();

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

    public String toString() {
        return getCustomerPayments().toString();
    }
}
