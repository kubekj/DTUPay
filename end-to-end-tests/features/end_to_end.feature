Feature: Student Registration feature
  ##############################################################################
  # Developed by Andreas (s176334) and Christian (s194578) as pair programming #
  ##############################################################################
  ###############################################################################################
  # Reporting Developed by Jakub (s232946) assisted by Andreas (s176334) and Christian (s194578)#
  ###############################################################################################


  ####################################
  # Responsible: Christian (s194578) #
  ####################################
  Scenario: Customer Registration and deregistration twice
    Given an unregistered customer with empty id
    When the customer is being registered
    Then the customer is registered
    And the customer has a non empty id
    When the customer is being deregistered
    Then the customer is deregistered
    When the customer is being deregistered
    Then an error message is returned saying "Customer does not exist"

  ##################################
  # Responsible: Andreas (s176334) #
  ##################################
  Scenario: Merchant Registration and deregistration twice
    Given an unregistered merchant with empty id
    When the merchant is being registered
    Then the merchant is registered
    And the merchant has a non empty id
    When the merchant is being deregistered
    Then the merchant is deregistered
    When the merchant is being deregistered
    Then an error message is returned saying "Merchant does not exist"

  ####################################
  # Responsible: Christian (s194578) #
  ####################################
  Scenario: Successful payment
    Given the customer with a bank account with balance 1000
    And a customer is registered with DTU Pay
    And the merchant with a bank account with balance 1000
    And a merchant is registered with DTU Pay
    And the customer requests 5 tokens
    When the customer has handed a token to the merchant
    And the merchant initiates a payment of 100
    Then the payment is successful
    And the balance of the customer is 900
    And the balance of the merchant is 1100

  ##################################
  # Responsible: Andreas (s176334) #
  ##################################
  Scenario: Unsuccessful payment due to insufficient amount of money
    Given the customer with a bank account with balance 0
    And a customer is registered with DTU Pay
    And the merchant with a bank account with balance 1000
    And a merchant is registered with DTU Pay
    And the customer requests 5 tokens
    When the customer has handed a token to the merchant
    And the merchant initiates a payment of 100
    Then the payment is unsuccessful
    And the balance of the customer is 0
    And the balance of the merchant is 1000

  ####################################
  # Responsible: Christian (s194578) #
  ####################################
  Scenario: Customer already exist
    Given an unregistered customer with empty id
    When the customer is being registered
    Then the customer is registered
    And the customer has a non empty id
    When another customer registers with the same CPR
    Then an error message is returned saying "Customer already exist"

  ##################################
  # Responsible: Andreas (s176334) #
  ##################################
  Scenario: Merchant already exist
    Given an unregistered merchant with empty id
    When the merchant is being registered
    Then the merchant is registered
    And the merchant has a non empty id
    When another merchant registers with the same CPR
    Then an error message is returned saying "Merchant already exist"

  ####################################
  # Responsible: Christian (s194578) #
  ####################################
  Scenario: Customer requests tokens
    Given a customer is registered with DTU Pay
    And the customer has 1 token
    When the customer requests 5 tokens
    Then the customer has 6 tokens

  ##################################
  # Responsible: Andreas (s176334) #
  ##################################
  Scenario: Customer requests tokens fail
    Given a customer is registered with DTU Pay
    And the customer has 2 token
    When the customer requests 5 tokens
    Then the customer has 2 token

  ################################
  # Responsible: Jakub (s232946) #
  ################################
  Scenario: Manager ask for report successful
    Given the customer with a bank account with balance 1000
    And a customer is registered with DTU Pay
    And the merchant with a bank account with balance 1000
    And a merchant is registered with DTU Pay
    And the customer requests 5 tokens
    When the customer has handed a token to the merchant
    And the merchant initiates a payment of 100
    Then the payment is successful
    And the balance of the customer is 900
    And the balance of the merchant is 1100
    When the manager ask for a report
    Then a report is returned to the manager
    And the manager report contains the payment

  ################################
  # Responsible: Jakub (s232946) #
  ################################
  Scenario: Customer ask for report successful
    Given the customer with a bank account with balance 1000
    And a customer is registered with DTU Pay
    And the merchant with a bank account with balance 1000
    And a merchant is registered with DTU Pay
    And the customer requests 5 tokens
    When the customer has handed a token to the merchant
    And the merchant initiates a payment of 100
    Then the payment is successful
    And the balance of the customer is 900
    And the balance of the merchant is 1100
    When the customer ask for a report
    Then a report is returned to the customer
    And the customer report contains the payment

  ################################
  # Responsible: Jakub (s232946) #
  ################################
  Scenario: Merchant ask for report successful
    Given the customer with a bank account with balance 1000
    And a customer is registered with DTU Pay
    And the merchant with a bank account with balance 1000
    And a merchant is registered with DTU Pay
    And the customer requests 5 tokens
    When the customer has handed a token to the merchant
    And the merchant initiates a payment of 100
    Then the payment is successful
    And the balance of the customer is 900
    And the balance of the merchant is 1100
    When the merchant ask for a report
    Then a report is returned to the merchant
    And the merchant report contains the payment


