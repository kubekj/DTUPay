package classes;

import classes.DTO.ManagerPaymentDTO;
import exceptions.ReportCreationException;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/*
  ################################
  # Developed by Jakub (s232946) #
  ################################
*/
@Getter
@Setter
public class ManagerReport extends Report {
    private List<ManagerPaymentDTO> managerPayments = new LinkedList<>();

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

            this.managerPayments.add(paymentDTO);
        }
    }

    @Override
    public String toString() {
        return getManagerPayments().toString();
    }
}
