package facade.service;

import facade.classes.Merchant;
import facade.classes.MerchantReport;
import facade.classes.Payment;
import facade.classes.User;
import facade.exceptions.MerchantServiceException;
import facade.exceptions.ReportCreationException;
import messaging.Event;
import messaging.MessageQueue;

import java.util.concurrent.CompletableFuture;

import static utilities.ErrorMessages.*;
import static utilities.EventTopics.*;

/*
  ##############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programming #
  ##############################################################################
*/
public class MerchantService {
    private MessageQueue queue;
    private CompletableFuture<String> payments;
    private CompletableFuture<Merchant> registeredMerchant;
    private CompletableFuture<String> deregisteredMerchant;
    private CompletableFuture<MerchantReport> merchantReport;

    // Init handlers
    public MerchantService(MessageQueue mq) {
        queue = mq;
        queue.addHandler(MONEY_TRANSFERRED, this::handleMoneyTransferred);
        queue.addHandler(MERCHANT_REGISTERED, this::handleMerchantRegistered);
        queue.addHandler(MERCHANT_DEREGISTERED, this::handleMerchantDeregistered);
        queue.addHandler(MONEY_NOT_TRANSFERRED, this::handleMoneyTransferred);
        queue.addHandler(MERCHANT_NOT_REGISTERED, this::handleMerchantRegistered);
        queue.addHandler(MERCHANT_DOES_NOT_EXIST, this::handleMerchantDeregistered);
        queue.addHandler(MERCHANT_REPORT_GENERATED, this::handleMerchantReportGenerated);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    public Merchant register(User merchant) throws MerchantServiceException {
        registeredMerchant = new CompletableFuture<>();
        Event event = new Event(MERCHANT_REGISTRATION_REQUESTED, new Object[]{merchant});
        queue.publish(event);
        var result = registeredMerchant.join();
        if (result.getId() == null) {
            throw new MerchantServiceException(ERROR_MERCHANT_ALREADY_EXIST);
        }
        return result;
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    public String deregister(String id) throws MerchantServiceException {
        deregisteredMerchant = new CompletableFuture<>();
        Event event = new Event(MERCHANT_DEREGISTRATION_REQUESTED, new Object[]{id});
        queue.publish(event);
        var result = deregisteredMerchant.join();
        if (result.equals(ERROR_MERCHANT_DOES_NOT_EXIST)) {
            throw new MerchantServiceException(ERROR_MERCHANT_DOES_NOT_EXIST);
        }
        return result;
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    // Payment function, will publish event to TokenMgmtService
    public String pay(Payment p) throws MerchantServiceException {
        payments = new CompletableFuture<>();
        Event event = new Event(PAYMENT_REQUESTED, new Object[]{p});
        queue.publish(event);
        var result = payments.join();
        if (result.equals(ERROR_UNSUCCESSFUL_PAYMENT)) {
            throw new MerchantServiceException(ERROR_UNSUCCESSFUL_PAYMENT);
        }
        return result;
    }

    /*
    ####################################
    # Responsible: Jakub (s232946) #
    ####################################
    */
    // Report function, will publish event to PaymentService
    public MerchantReport getReport(String id) throws ReportCreationException {
        merchantReport = new CompletableFuture<>();
        Event event = new Event(MERCHANT_REPORT_REQUESTED, new Object[]{id});
        queue.publish(event);
        var result = merchantReport.join();
        if (result.getMerchantPayments().isEmpty())
            throw new ReportCreationException();
        return result;
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    // Handler for payment, event comes from PaymentService
    public void handleMoneyTransferred(Event event) {
        var payment = event.getArgument(0, String.class);
        payments.complete(payment);
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    // Handler for merchant registration, event comes from AccountMgmtService
    public void handleMerchantRegistered(Event event) {
        var merchant = event.getArgument(0, Merchant.class);
        registeredMerchant.complete(merchant);
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    // Handler for merchant de-registration, event comes from AccountMgmtService
    public void handleMerchantDeregistered(Event event) {
        var string = event.getArgument(0, String.class);
        deregisteredMerchant.complete(string);
    }

    /*
    ####################################
    # Responsible: Jakub (s232946) #
    ####################################
    */
    // Handler for report, event comes from ReportMgmtService
    public void handleMerchantReportGenerated(Event event) {
        MerchantReport mr = event.getArgument(0, MerchantReport.class);
        merchantReport.complete(mr);
    }
}
