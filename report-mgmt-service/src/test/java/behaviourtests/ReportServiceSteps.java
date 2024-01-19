package behaviourtests;

import io.cucumber.java.en.And;
import io.cucumber.java.en.But;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import report.service.DTO.CustomerPaymentDTO;
import report.service.DTO.ManagerPaymentDTO;
import report.service.DTO.MerchantPaymentDTO;
import report.service.ReportService;
import report.service.classes.*;
import report.service.enums.ReportEvents;
import report.service.exceptions.ReportCreationException;
import report.service.factories.ReportFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
/*
  ################################
  # Developed by Jakub (s232946) #
  ################################
*/
public class ReportServiceSteps {
    MessageQueue queue = mock(MessageQueue.class);
    ReportService reportService = new ReportService(queue);
    Report report;
    CustomerReport customerReport, expectedCustomerReport;
    ManagerReport managerReport, expectedManagerReport;
    MerchantReport merchantReport, expectedMerchantReport;
    PaymentListWrapper paymentListWrapper;
    List<Payment> payments = new ArrayList<>();
    List<Exception> exceptions = new ArrayList<>();

    private void mockPayments(int transactions) {
        UUID customerId = UUID.randomUUID();
        UUID merchantId = UUID.randomUUID();
        Payment payment;
        for (int i = 0; i < transactions; i++) {
            String token = "Token_" + UUID.randomUUID();

            if (i % 2 == 0) {
                payment = new Payment(i * 1000 + 100, token, UUID.randomUUID().toString(), UUID.randomUUID().toString());
            } else {
                payment = new Payment(i * 1000 + 100, token, customerId.toString(), merchantId.toString());
            }

            payments.add(payment);
        }
    }

    @And("a {string} event for reports is received")
    public void aEventForReportsRequestedIsReceived(String eventName) {
        Event event = new Event(eventName);
        handleReportRequestedEvent(eventName, event);
    }

    @When("a {string} event for reports is sent with userId {string}")
    public void aEventForReportsRequestedWithUserIdIsReceived(String eventName, String userId) {
        UUID uuid = UUID.fromString(userId);
        Event event = new Event(eventName, new Object[]{uuid});
        handleReportRequestedEvent(eventName, event);
    }

    private void handleReportRequestedEvent(String eventName, Event event) {
        if (eventName.equals(ReportEvents.CustomerReportRequested.name()))
            reportService.handleCustomerReportRequested(event);
        else if (eventName.equals(ReportEvents.MerchantReportRequested.name()))
            reportService.handleMerchantReportRequested(event);
        else reportService.handleManagerReportRequested();

    }

    @Then("there are {int} transactions registered")
    public void addTransactions(int transactions) {
        mockPayments(transactions);
    }

    @And("a AllPaymentsReturned event for payments is received")
    public void aEventForAllPaymentsReturnedIsReceived() {
        String eventName = ReportEvents.AllPaymentsReturned.name();
        PaymentListWrapper paymentWrapper = new PaymentListWrapper(payments);
        Event event = new Event(eventName, new Object[]{paymentWrapper});
        handlePaymentReturnedEvent(eventName, event);
    }

    @And("a CustomerPaymentsReturned event for payments is received")
    public void aEventForCustomerPaymentsReturnedIsReceived() {
        String eventName = ReportEvents.CustomerPaymentsReturned.name();
        var paymentWrapper = new PaymentListWrapper(payments);
        Event event = new Event(eventName, new Object[]{paymentWrapper});
        handlePaymentReturnedEvent(eventName, event);
    }

    @And("a MerchantPaymentsReturned event for payments is received")
    public void aEventForMerchantPaymentsReturnedIsReceived() {
        String eventName = ReportEvents.MerchantPaymentsReturned.name();
        var paymentWrapper = new PaymentListWrapper(payments);
        Event event = new Event(eventName, new Object[]{paymentWrapper});
        handlePaymentReturnedEvent(eventName, event);
    }

    private Report handlePaymentReturnedEvent(String eventName, Event event) {
        if (eventName.equals(ReportEvents.CustomerPaymentsReturned.name()))
            return reportService.handleCustomerPaymentsReturned(event);
        else if (eventName.equals(ReportEvents.MerchantPaymentsReturned.name()))
            return reportService.handleMerchantPaymentsReturned(event);
        else {
            return reportService.handleAllPaymentsReturned(event);
        }
    }

    @And("the Manager receives the report")
    public void theManagerReceivesTheReport() {
        ManagerReport managerReport = reportService.getManagerReport();
        assertNotNull(managerReport);
        assertFalse(managerReport.managerPayments.isEmpty());
    }

    @And("the Merchant receives the report")
    public void theMerchantReceivesTheReport() {
        MerchantReport merchantReport = reportService.getMerchantReport();
        assertNotNull(merchantReport);
        assertFalse(merchantReport.merchantPayments.isEmpty());
    }

    @And("the Customer receives the report")
    public void theCustomerReceivesTheReport() {
        CustomerReport customerReport = reportService.getCustomerReport();
        assertNotNull(customerReport);
        assertFalse(customerReport.getCustomerPayments().isEmpty());
    }

    @But("the ReportCreationException with a error message {string} is thrown")
    public void theExceptionWithAErrorMessageIsThrown(String errorMessage) {
        String actualMessage = exceptions.get(0).getMessage();
        assertTrue(actualMessage.contains(errorMessage));
    }

    @Then("the {string} event is sent")
    public void theEventForManagerReportIsSent(String eventName) {
        var paymentWrapper = new PaymentListWrapper(payments);
        Event event = new Event(eventName, new Object[]{paymentWrapper});
        reportService.handleAllPaymentsReturned(event);
    }

    @Then("an empty transaction list from {string} event for payments is received")
    public void anEmptyAllPaymentsReturnedEventForPaymentsIsReceived(String eventName) {
        paymentListWrapper = new PaymentListWrapper(payments);
    }

    @Then("{string} event is trying to generate a report")
    public void eventIsTryingToGenerateAReport(String eventName) {
        try {
            report = new ReportFactory().createReport(eventName, paymentListWrapper);
        } catch (ReportCreationException e) {
            exceptions.add(e);
        }
    }

    @When("a {string} event is received with a list of payments")
    public void aEventIsReceivedWithAListOfPayments(String eventName) {
        paymentListWrapper = new PaymentListWrapper(payments);
        Event event = new Event(eventName, new Object[]{paymentListWrapper});
        report = handlePaymentReturnedEvent(eventName, event);
    }

    @Then("the {string} manager event is sent")
    public void theManagerEventIsSent(String eventName) {
        expectedManagerReport = reportService.getManagerReport();
        var event = new Event(eventName, new Object[]{expectedManagerReport});
        verify(queue).publish(event);
    }

    @Then("the {string} merchant event is sent")
    public void theMerchantEventIsSent(String eventName) {
        expectedMerchantReport = reportService.getMerchantReport();
        var event = new Event(eventName, new Object[]{expectedMerchantReport});
        verify(queue).publish(event);
    }

    @Then("the {string} customer event is sent")
    public void theCustomerEventIsSent(String eventName) {
        expectedCustomerReport = reportService.getCustomerReport();
        var event = new Event(eventName, new Object[]{expectedCustomerReport});
        verify(queue).publish(event);
    }

    @And("the customer report contains the correct payments")
    public void theCustomerReportContainsTheSamePayments() {
        List<CustomerPaymentDTO> expectedPayments = expectedCustomerReport.getCustomerPayments();
        customerReport = (CustomerReport) report;
        List<CustomerPaymentDTO> payments = customerReport.getCustomerPayments();
        assertEquals(expectedPayments, payments);
    }

    @And("the merchant report contains the correct payments")
    public void theMerchantReportContainsTheSamePayments() {
        List<MerchantPaymentDTO> expectedPayments = expectedMerchantReport.getMerchantPayments();
        merchantReport = (MerchantReport) report;
        List<MerchantPaymentDTO> payments = merchantReport.getMerchantPayments();
        assertEquals(expectedPayments, payments);
    }

    @And("the manager report contains the correct payments")
    public void theManagerReportContainsTheSamePayments() {
        List<ManagerPaymentDTO> expectedPayments = expectedManagerReport.getManagerPayments();
        managerReport = (ManagerReport) report;
        List<ManagerPaymentDTO> payments = managerReport.getManagerPayments();
        assertEquals(expectedPayments, payments);
    }
}

