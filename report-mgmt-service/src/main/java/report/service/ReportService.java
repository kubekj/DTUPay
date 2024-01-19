package report.service;

import messaging.Event;
import messaging.MessageQueue;
import report.service.classes.CustomerReport;
import report.service.classes.ManagerReport;
import report.service.classes.MerchantReport;
import report.service.classes.PaymentListWrapper;
import report.service.enums.ReportEvents;
import report.service.exceptions.ReportCreationException;

import java.util.HashMap;
import java.util.UUID;
/*
  #############################################################
  # Developed by Jakub (s232946) assisted by Andreas (s176334)#
  #############################################################
*/
public class ReportService {

    MessageQueue queue;
    public HashMap<UUID, ManagerReport> managerReports = new HashMap<>();
    public HashMap<UUID, CustomerReport> customerReports = new HashMap<>();
    public HashMap<UUID, MerchantReport> merchantReports = new HashMap<>();


    public ReportService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler(ReportEvents.AllPaymentsReturned.name(), this::handleAllPaymentsReturned);
        this.queue.addHandler(ReportEvents.CustomerPaymentsReturned.name(), this::handleCustomerPaymentsReturned);
        this.queue.addHandler(ReportEvents.MerchantPaymentsReturned.name(), this::handleMerchantPaymentsReturned);
    }

    public void handleManagerReportRequested() {
        Event event = new Event("AllPaymentsRequested", new Object[]{});
        queue.publish(event);
    }

    public void handleCustomerReportRequested(Event ev) {
        UUID customerId = ev.getArgument(0, UUID.class);
        Event event = new Event("CustomerPaymentsRequested", new Object[]{customerId});
        queue.publish(event);
    }

    public void handleMerchantReportRequested(Event ev) {
        UUID merchantId = ev.getArgument(0, UUID.class);
        Event event = new Event("MerchantPaymentsRequested", new Object[]{merchantId});
        queue.publish(event);
    }

    public ManagerReport handleAllPaymentsReturned(Event ev) {
        PaymentListWrapper payments = ev.getArgument(0, PaymentListWrapper.class);
        String eventName = ReportEvents.ManagerReportGenerated.name();
        var managerReport = new ManagerReport();

        try {
            managerReport.generateReport(payments);
        } catch (ReportCreationException e) {
            System.out.println("Report failed");
        }

        managerReports.put(managerReport.getId(), managerReport);
        Event event = new Event(eventName, new Object[]{managerReport});
        queue.publish(event);
        return managerReport;
    }

    public CustomerReport handleCustomerPaymentsReturned(Event ev) {
        var payments = ev.getArgument(0, PaymentListWrapper.class);
        String eventName = ReportEvents.CustomerReportGenerated.name();
        var customerReport = new CustomerReport();

        try {
            customerReport.generateReport(payments);
        } catch (ReportCreationException e) {
            System.out.println("Report failed");
        }

        customerReports.put(customerReport.getId(), customerReport);
        Event event = new Event(eventName, new Object[]{customerReport});
        queue.publish(event);
        return customerReport;
    }

    public MerchantReport handleMerchantPaymentsReturned(Event ev) {
        var payments = ev.getArgument(0, PaymentListWrapper.class);
        String eventName = ReportEvents.MerchantReportGenerated.name();
        var merchantReport = new MerchantReport();

        try {
            merchantReport.generateReport(payments);
        } catch (ReportCreationException e) {
            System.out.println("Report failed");
        }

        merchantReports.put(merchantReport.getId(), merchantReport);
        Event event = new Event(eventName, new Object[]{merchantReport});
        queue.publish(event);
        return merchantReport;
    }

    public ManagerReport getManagerReport() {
        return managerReports.entrySet().iterator().next().getValue();
    }

    public CustomerReport getCustomerReport() {
        return customerReports.entrySet().iterator().next().getValue();
    }

    public MerchantReport getMerchantReport() {
        return merchantReports.entrySet().iterator().next().getValue();
    }
}
