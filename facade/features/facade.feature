Feature: Facade feature
  ##############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programming #
  ##############################################################################
  ###############################################################################################
  # Reporting Developed by Jakub (s232946) assisted by Andreas (s176334) and Christian (s194578)#
  ###############################################################################################


  # Customer endpoint
  ##################################
  # Responsible: Andreas (s176334) #
  ##################################
  Scenario: Issue tokens to customer
    Given there is a customer registered with DTU Pay
    When the customer requests 2 tokens
    Then the "IssueTokenRequested" token event is sent
    When the "TokenIssued" token event is received
    Then the customer received 2 tokens

  ####################################
  # Responsible: Christian (s194578) #
  ####################################
  Scenario: Issue tokens to customer fail
    Given there is a customer registered with DTU Pay
    When the customer requests 7 tokens
    Then the "IssueTokenRequested" token event is sent
    When the "TokenNotIssued" token event is received
    Then the customer received 0 tokens

  ################################
  # Responsible: Jakub (s232946) #
  ################################
  Scenario: Customer generate report
    Given there is a customer registered with DTU Pay
    And there is a merchant registered with DTU Pay
    And the customer has made payment
    When the customer requests a report
    Then the "CustomerReportRequested" report event is sent
    When the "CustomerReportGenerated" report event is received with non-empty report
    Then the payment is in the customer report

  ##################################
  # Responsible: Andreas (s176334) #
  ##################################
  Scenario: Customer registration
    Given there is a customer with empty id
    When the customer is being registered
    Then the "CustomerRegistrationRequested" event is sent
    When the "CustomerRegistered" registration event is sent with non-empty id
    Then the customer is registered and his id is set

  ####################################
  # Responsible: Christian (s194578) #
  ####################################
  Scenario: Customer registration fail
    Given there is a customer with empty id
    When the customer is being registered with the same CPR number as another
    Then the "CustomerRegistrationRequested" event is sent
    When the "CustomerNotRegistered" registration event is received
    Then an error message is returned saying "Customer already exist"

  ##################################
  # Responsible: Andreas (s176334) #
  ##################################
  Scenario: Customer de-registration
    Given there is a customer with id "123"
    When the customer is being de-registered
    Then the "CustomerDeregistrationRequested" event is sent
    When the "CustomerDeregistered" deregistration event is sent
    Then the customer is de-registered

  ####################################
  # Responsible: Christian (s194578) #
  ####################################
  Scenario: Customer de-registration fail
    Given there is no customer with id "123"
    When the customer is being de-registered
    Then the "CustomerDeregistrationRequested" event is sent
    Then the "CustomerDoesNotExist" deregistration event is received
    And a deregistration error message is returned saying "Customer does not exist"

  # Merchant endpoint
  ##################################
  # Responsible: Andreas (s176334) #
  ##################################
  Scenario: Merchant registration
    Given there is a merchant with empty id
    When the merchant is being registered
    Then the "MerchantRegistrationRequested" event is sent
    When the "MerchantRegistered" registration event is sent with non-empty id
    Then the merchant is registered and his id is set

  ####################################
  # Responsible: Christian (s194578) #
  ####################################
  Scenario: Merchant registration fail
    Given there is a merchant with empty id
    When the merchant is being registered with the same CPR number as another
    Then the "MerchantRegistrationRequested" event is sent
    When the "MerchantNotRegistered" registration event is received
    Then an error message is returned saying "Merchant already exist"

  ##################################
  # Responsible: Andreas (s176334) #
  ##################################
  Scenario: Merchant de-registration
    Given there is a merchant with id "123"
    When the merchant is being de-registered
    Then the "MerchantDeregistrationRequested" event is sent
    When the "MerchantDeregistered" deregistration event is sent
    Then the merchant is de-registered

  ####################################
  # Responsible: Christian (s194578) #
  ####################################
  Scenario: Merchant de-registration fail
    Given there is no merchant with id "123"
    When the merchant is being de-registered
    Then the "MerchantDeregistrationRequested" event is sent
    Then the "MerchantDoesNotExist" deregistration event is received
    And a deregistration error message is returned saying "Merchant does not exist"

  ##################################
  # Responsible: Andreas (s176334) #
  ##################################
  Scenario: Merchant issues a payment request
    Given there is a merchant with id "123"
    When the merchant initiates a payment with amount 10 and token id "1"
    Then the "PaymentRequested" payment event is sent
    When the "MoneyTransferred" event is received
    Then the payment is successful

  ####################################
  # Responsible: Christian (s194578) #
  ####################################
  Scenario: Merchant issues a payment request fail
    Given there is a merchant with id "123"
    When the merchant initiates a payment with amount 10 and token id "1"
    Then the "PaymentRequested" payment event is sent
    When the "MoneyNotTransferred" event is received
    Then the payment is unsuccessful

  ################################
  # Responsible: Jakub (s232946) #
  ################################
  Scenario: Merchant generate report
    Given there is a customer registered with DTU Pay
    And there is a merchant registered with DTU Pay
    And the customer has made payment
    When the merchant requests a report
    Then the "MerchantReportRequested" report event is sent
    When the "MerchantReportGenerated" report event is received with non-empty report
    Then the payment is in the merchant report

  # Reports endpoint
  ################################
  # Responsible: Jakub (s232946) #
  ################################
  Scenario: Manager generate report
    Given there is a customer registered with DTU Pay
    And there is a merchant registered with DTU Pay
    And the customer has made payment
    When the manager requests a report
    Then the "ManagerReportRequested" report event is sent
    When the "ManagerReportGenerated" report event is received with non-empty report
    Then the payment is in the manager report