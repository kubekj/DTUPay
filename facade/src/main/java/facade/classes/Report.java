package facade.classes;

import facade.exceptions.ReportCreationException;
import facade.service.PaymentListWrapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/*
  ################################
  # Developed by Jakub (s232946) #
  ################################
*/
@Setter
@Getter
@EqualsAndHashCode
public abstract class Report {

    private UUID id;

    public Report() {
        this.id = UUID.randomUUID();
    }

    public abstract void generateReport(PaymentListWrapper paymentListWrapper) throws ReportCreationException;

    @Override
    public String toString() {
        return getId().toString();
    }
}
