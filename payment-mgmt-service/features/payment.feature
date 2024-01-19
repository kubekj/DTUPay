Feature: Payment feature
  ##############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programming #
  ##############################################################################
  ###############################################################################################
  # Reporting Developed by Jakub (s232946) assisted by Andreas (s176334) and Christian (s194578)#
  ###############################################################################################

  ####################################
  # Responsible: Christian (s194578) #
  ####################################
  Scenario: Successful payment
    Given a customer with a bank account with balance 1000
    And a merchant with a bank account with balance 1000
    When a "TokenIsConsumed" event is received with amount 100
    And the payment to the bank is successful
    And the balance of the customer at the bank is 900 kr
    And the balance of the merchant at the bank is 1100 kr
    And the "MoneyTransferred" event is sent

  ##################################
  # Responsible: Andreas (s176334) #
  ##################################
  Scenario: Unsuccessful payment due to insufficient amount of money
    Given a customer with a bank account with balance 0
    And a merchant with a bank account with balance 1000
    When a "TokenIsConsumed" event is received with amount 100
    Then the payment to the bank is unsuccessful
    And the "MoneyNotTransferred" event is sent

  ####################################
  # Responsible: Christian (s194578) #
  ####################################
  Scenario: Manager report requested
    Given a customer with id "1" has made payment with a merchant with id "1234"
    When a "ManagerReportRequested" event from manager is received
    Then the "AllPaymentsReturned" event is sent with a list of payments
    And the list contains correct payments

  ##################################
  # Responsible: Andreas (s176334) #
  ##################################
  Scenario: Merchant report requested
    Given a customer with id "1" has made payment with a merchant with id "1234"
    When a "MerchantReportRequested" event from merchant is received
    Then the "MerchantPaymentsReturned" event is sent with a list of payments
    And the list contains correct payments

  ################################
  # Responsible: Jakub (s232946) #
  ################################
  Scenario: Customer report requested
    Given a customer with id "1" has made payment with a merchant with id "1234"
    When a "CustomerReportRequested" event from customer is received
    Then the "CustomerPaymentsReturned" event is sent with a list of payments
    And the list contains correct payments
