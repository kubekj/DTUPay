package facade.classes;

import facade.classes.DTO.MerchantPaymentDTO;
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
public class MerchantReport extends Report {
    public final ArrayList<MerchantPaymentDTO> merchantPayments = new ArrayList<>();

    public MerchantReport() {
        super();
    }

    public ArrayList<MerchantPaymentDTO> getMerchantPayments() {
        return merchantPayments;
    }

    @Override
    public void generateReport(PaymentListWrapper paymentListWrapper) throws ReportCreationException {
        var payments = paymentListWrapper.getPayments();

        if (payments.isEmpty()) throw new ReportCreationException();

        for (Payment payment : payments) {
            MerchantPaymentDTO paymentDTO = new MerchantPaymentDTO(
                    payment.getAmount(),
                    payment.getCustomerTokenId());

            merchantPayments.add(paymentDTO);
        }
    }

    @Override
    public String toString() {
        return getMerchantPayments().toString();
    }
}
