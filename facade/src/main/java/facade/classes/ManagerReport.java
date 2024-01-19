package facade.classes;

import facade.classes.DTO.ManagerPaymentDTO;
import facade.exceptions.ReportCreationException;
import facade.service.PaymentListWrapper;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/*
  ################################
  # Developed by Jakub (s232946) #
  ################################
*/
@Getter
public class ManagerReport extends Report {
    public final List<ManagerPaymentDTO> managerPayments = new LinkedList<>();

    public ManagerReport() {
        super();
    }

    @Override
    public void generateReport(PaymentListWrapper paymentListWrapper) throws ReportCreationException {
        var payments = paymentListWrapper.getPayments();

        if (payments.isEmpty()) throw new ReportCreationException();

        for (Payment payment : payments) {
            ManagerPaymentDTO paymentDTO = new ManagerPaymentDTO(
                    payment.getAmount(),
                    payment.getCustomerTokenId(),
                    payment.getCustomerID(),
                    payment.getMerchantID());

            managerPayments.add(paymentDTO);
        }
    }

    @Override
    public String toString() {
        return getManagerPayments().toString();
    }
}
