package payment.service;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import lombok.Getter;
import lombok.Setter;
import messaging.Event;
import messaging.MessageQueue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static utilities.EventTopics.*;
import static utilities.Responses.SUCCESSFUL_PAYMENT;
import static utilities.Responses.UNSUCCESSFUL_PAYMENT;

/*
  ##############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programming #
  ##############################################################################
*/
public class PaymentService {

    MessageQueue queue;

    BankService bank = new BankServiceService().getBankServicePort();

    @Setter
    @Getter
    private ArrayList<Payment> payments = new ArrayList<>();


    public PaymentService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler(TOKEN_IS_CONSUMED, this::handleTokenConsumed);
        this.queue.addHandler(CUSTOMER_REPORT_REQUESTED, this::handleCustomerReportRequested);
        this.queue.addHandler(MANAGER_REPORT_REQUESTED, this::handleManagerReportRequested);
        this.queue.addHandler(MERCHANT_REPORT_REQUESTED, this::handleMerchantReportRequested);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    // Handler for token consumption - Get event from TokenMgmtService and publish to MerchantService
    public boolean handleTokenConsumed(Event ev) {
        var payment = ev.getArgument(0, Payment.class);
        BankPayment bankPayment = new BankPayment();
        bankPayment.setAmount(payment.getAmount());
        bankPayment.setMerchantBankAccount(payment.getMerchantBankAccount());
        bankPayment.setCustomerBankAccount(payment.getCustomerBankAccount());
        boolean successful = transferMoney(bankPayment);
        if (successful) {
            payments.add(payment);
            Event event = new Event(MONEY_TRANSFERRED, new Object[]{SUCCESSFUL_PAYMENT});
            queue.publish(event);
        } else {
            Event event = new Event(MONEY_NOT_TRANSFERRED, new Object[]{UNSUCCESSFUL_PAYMENT});
            queue.publish(event);
        }

        return successful;
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    // Transfer money to bank using SOAP
    public boolean transferMoney(BankPayment bankPayment) {
        try {
            bank.transferMoneyFromTo(bankPayment.getCustomerBankAccount(), bankPayment.getMerchantBankAccount(), BigDecimal.valueOf(bankPayment.getAmount()), "description");
        } catch (BankServiceException_Exception exception) {
            return false; // If unsuccessful
        }
        return true; // Successful
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    // Handler for customer report requested - Get event from customerService and publish event to reportMgmtService
    public PaymentListWrapper handleCustomerReportRequested(Event ev) {
        var id = ev.getArgument(0, String.class);
        var customerPayments = payments.stream()
                .filter(payment -> id.equals(payment.getCustomerID()))
                .collect(Collectors.toList());
        PaymentListWrapper paymentWrapper = new PaymentListWrapper(customerPayments);
        Event event = new Event(CUSTOMER_PAYMENTS_RETURNED, new Object[]{paymentWrapper});
        queue.publish(event);
        return paymentWrapper;
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    // Handler for merchant report requested - Get event from merchantService and publish event to reportMgmtService
    public PaymentListWrapper handleMerchantReportRequested(Event ev) {
        var id = ev.getArgument(0, String.class);
        var merchantPayments = payments.stream()
                .filter(payment -> id.equals(payment.getMerchantID()))
                .collect(Collectors.toList());
        PaymentListWrapper paymentWrapper = new PaymentListWrapper(merchantPayments);
        Event event = new Event(MERCHANT_PAYMENTS_RETURNED, new Object[]{paymentWrapper});
        queue.publish(event);
        return paymentWrapper;
    }

    /*
    ################################
    # Responsible: Jakub (s232946) #
    ################################
    */
    // Handler for manager report requested - Get event from reportService and publish event to reportMgmtService
    public PaymentListWrapper handleManagerReportRequested(Event ev) {
        PaymentListWrapper paymentWrapper = new PaymentListWrapper(payments);
        Event event = new Event(ALL_PAYMENTS_RETURNED, new Object[]{paymentWrapper});
        queue.publish(event);
        return paymentWrapper;
    }
}
