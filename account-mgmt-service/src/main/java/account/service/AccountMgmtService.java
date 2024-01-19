package account.service;

import messaging.Event;
import messaging.MessageQueue;
import utilities.EventHelper;

import java.util.HashMap;
import java.util.UUID;

import static utilities.EventTopics.*;
import static utilities.Responses.RESPONSE_CUSTOMER_DOES_NOT_EXIST;
import static utilities.Responses.RESPONSE_MERCHANT_DOES_NOT_EXIST;

/*
  #############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programming #
  #############################################################################

  This micro service is responsible for account managing.
*/
public class AccountMgmtService {
    private MessageQueue queue;
    private HashMap<String, User> users = new HashMap<>();

    // Init handlers
    public AccountMgmtService(MessageQueue mq) {
        this.queue = mq;
        this.queue.addHandler(MERCHANT_REGISTRATION_REQUESTED, this::handleUserRegistrationRequested);
        this.queue.addHandler(CUSTOMER_REGISTRATION_REQUESTED, this::handleUserRegistrationRequested);
        this.queue.addHandler(MERCHANT_DEREGISTRATION_REQUESTED, this::handleUserDeregistrationRequested);
        this.queue.addHandler(CUSTOMER_DEREGISTRATION_REQUESTED, this::handleUserDeregistrationRequested);
        this.queue.addHandler(USER_ID_RETURNED, this::handleUserIdReturned);
    }

    /*
      ##################################
      # Responsible: Andreas (s176334) #
      ##################################
    */
    // Handler for registering users
    // Listens for events from the MerchantService and CustomerService
    public String handleUserRegistrationRequested(Event ev) {
        EventHelper eventHelper = null;
        if (ev.getType().equals(CUSTOMER_REGISTRATION_REQUESTED)) {
            eventHelper = registrationHelper(ev, CUSTOMER_NOT_REGISTERED, CUSTOMER_REGISTERED, Customer.class);
        } else if (ev.getType().equals(MERCHANT_REGISTRATION_REQUESTED)) {
            eventHelper = registrationHelper(ev, MERCHANT_NOT_REGISTERED, MERCHANT_REGISTERED, Merchant.class);
        }
        queue.publish(eventHelper.getEvent());
        return eventHelper.getId();
    }

    /*
    ####################################
    # Responsible: Christian (s194578) #
    ####################################
    */
    // Handler for de-registering users.
    // Listens for events from the MerchantService and CustomerService
    public void handleUserDeregistrationRequested(Event ev) {
        String response;
        var id = ev.getArgument(0, String.class);
        if (users.containsKey(id)) { // Check if user exist
            response = users.get(id).toString() + " is deleted";
            removeUser(id); // Remove user
            String eventTopic = ev.getType().equals(CUSTOMER_DEREGISTRATION_REQUESTED) ? CUSTOMER_DEREGISTERED : MERCHANT_DEREGISTERED;
            createAndPublishEvent(eventTopic, response);
        } else { // Return User don't exist
            if (ev.getType().equals(CUSTOMER_DEREGISTRATION_REQUESTED)) {
                createAndPublishEvent(CUSTOMER_DOES_NOT_EXIST, RESPONSE_CUSTOMER_DOES_NOT_EXIST);
            } else if (ev.getType().equals(MERCHANT_DEREGISTRATION_REQUESTED)) {
                createAndPublishEvent(MERCHANT_DOES_NOT_EXIST, RESPONSE_MERCHANT_DOES_NOT_EXIST);
            }
        }
    }

    /*
    ##################################
    # Responsible: Andreas (s176334) #
    ##################################
    */
    // Handler returning merchant and customer bank account from their IDs
    // Listens for events from TokenMgmtService
    public void handleUserIdReturned(Event ev) {
        var payment = ev.getArgument(0, Payment.class);
        User c = getUser(payment.getCustomerID());
        User m = getUser(payment.getMerchantID());
        payment.setCustomerBankAccount(c.getBankAccount());
        payment.setMerchantBankAccount(m.getBankAccount());
        Event event = new Event(TOKEN_CONSUMPTION_REQUESTED, new Object[]{payment});
        queue.publish(event);
    }

    /*
      ####################################
      # Responsible: Christian (s194578) #
      ####################################
    */
    // Helper function registration handler
    private EventHelper registrationHelper(Event ev, String eventTopicNotRegistered, String eventTopicRegistered, Class<? extends User> userClass) {
        User user = ev.getArgument(0, userClass);
        Event event;
        String id = null;
        if (userAlreadyExist(user.getCpr())) {
            event = new Event(eventTopicNotRegistered, new Object[]{user});
        } else {
            id = addUser(user);
            event = new Event(eventTopicRegistered, new Object[]{user});
        }
        return new EventHelper(event, id);
    }

    /*
      ##################################
      # Responsible: Andreas (s176334) #
      ##################################
    */
    // Helper function for retrieving user bank accounts
    public User getUser(String id) {
        return users.get(id);
    }

    /*
      ####################################
      # Responsible: Christian (s194578) #
      ####################################
    */
    private void createAndPublishEvent(String eventType, String response) {
        Event event = new Event(eventType, new Object[]{response});
        queue.publish(event);
    }

    /*
      ##################################
      # Responsible: Andreas (s176334) #
      ##################################
    */
    // Helper function for registration
    public String addUser(User user) {
        user.setId(UUID.randomUUID().toString());
        this.users.put(user.getId(), user);
        return user.getId();
    }

    /*
      ####################################
      # Responsible: Christian (s194578) #
      ####################################
    */
    // Helper function for de-registration
    public void removeUser(String id) {
        this.users.remove(id);
    }

    /*
      ##################################
      # Responsible: Andreas (s176334) #
      ##################################
    */
    // Helper function for registration
    public boolean userAlreadyExist(String cpr) {
        return this.users.values().stream().anyMatch(u -> u.getCpr().equals(cpr));
    }

}
