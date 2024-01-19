package classes;

import exceptions.ReportCreationException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

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
