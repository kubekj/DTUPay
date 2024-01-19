package classes;

import classes.DTO.MerchantPaymentDTO;
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
public class MerchantReport extends Report {
    private ArrayList<MerchantPaymentDTO> merchantPayments = new ArrayList<>();

    public MerchantReport() {
        super();
    }

    @Override
    public void generateReport(PaymentListWrapper paymentListWrapper) throws ReportCreationException {
        var payments = paymentListWrapper.getPayments();

        if (payments.isEmpty()) throw new ReportCreationException();

        for (Payment payment : payments) {
            MerchantPaymentDTO paymentDTO = new MerchantPaymentDTO(
                    payment.getAmount(),
                    payment.getCustomerTokenId());

            this.merchantPayments.add(paymentDTO);
        }
    }

    @Override
    public String toString() {
        return getMerchantPayments().toString();
    }
}
