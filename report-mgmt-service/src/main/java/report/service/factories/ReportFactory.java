package report.service.factories;

import report.service.classes.*;
import report.service.enums.ReportEvents;
import report.service.exceptions.ReportCreationException;
/*
  ################################
  # Developed by Jakub (s232946) #
  ################################
*/
public class ReportFactory {

    public Report createReport(String eventName, PaymentListWrapper payments) throws ReportCreationException {
        ReportEvents event = ReportEvents.valueOf(eventName);

        switch (event) {
            case ManagerReportGenerated:
                ManagerReport managerReport = new ManagerReport();
                managerReport.generateReport(payments);
                return managerReport;
            case CustomerReportGenerated:
                CustomerReport customerReport = new CustomerReport();
                customerReport.generateReport(payments);
                return customerReport;
            case MerchantReportGenerated:
                MerchantReport merchantReport = new MerchantReport();
                merchantReport.generateReport(payments);
                return merchantReport;
            default:
                throw new IllegalStateException("Unexpected value, there is no event handling for this specific event: " + event);
        }
    }
}
