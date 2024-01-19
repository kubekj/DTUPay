Feature: Token management feature

  Scenario: Successfully retrieve customer ID
    Given a customer with id "1337"
    And the customer has a token
    When a "PaymentRequested" event is received
    Then the "UserIDReturned" event is sent
    And the customer ID is retrieved


  Scenario: Token issue and user ID retrieval
    Given a customer with id "1"
    When a IssueTokenRequested event for the customer is received
    Then the TokenIssued event is sent
    And the token created belongs to the customer

  Scenario: Token consumption
    Given a customer with id "2"
    And 1 token is generated for the customer
    Then the token is valid
    When a TokenConsumptionRequested event for the customer is received
    Then the TokenIsConsumed event is sent
    And the token is invalid

  Scenario: Consume one of multiple tokens
    Given a customer with id "3"
    And 3 token is generated for the customer
    Then the customer has 3 unconsumed tokens
    When a TokenConsumptionRequested event for the customer is received
    Then the TokenIsConsumed event is sent
    And the customer has 2 unconsumed tokens

  Scenario: Issue of multiple tokens
    Given a customer with id "4"
    When a IssueTokenRequested event for the customer is received for 3 tokens
    Then the TokenIssued event is sent
    And the customer has 3 unconsumed tokens

  Scenario: Unable to issue tokens if the user will exceed the limit of 6 tokens
    Given a customer with id "5"
    And 4 token is generated for the customer
    When a IssueTokenRequested event for the customer is received for 3 tokens
    Then the TokenNotIssued event is sent
    And the customer has 4 unconsumed tokens

  Scenario: Unable to issue tokens if the user has more than 1 token
    Given a customer with id "6"
    And 2 token is generated for the customer
    When a IssueTokenRequested event for the customer is received for 2 tokens
    Then the TokenNotIssued event is sent
    And the customer has 2 unconsumed tokens

  Scenario: Unable to issue tokens even if the user has 1 token but it will exceed the limit of 6 tokens
    Given a customer with id "7"
    And 1 token is generated for the customer
    When a IssueTokenRequested event for the customer is received for 7 tokens
    Then the TokenNotIssued event is sent
    And the customer has 1 unconsumed tokens

  Scenario: Issue tokens when the user has 0 tokens
    Given a customer with id "8"
    When a IssueTokenRequested event for the customer is received for 4 tokens
    Then the TokenIssued event is sent
    And the customer has 4 unconsumed tokens

  Scenario: Issue tokens when the user has 1 tokens
    Given a customer with id "9"
    And 1 token is generated for the customer
    When a IssueTokenRequested event for the customer is received for 4 tokens
    Then the TokenIssued event is sent
    And the customer has 5 unconsumed tokens

  Scenario: Retrieve user ID from token
    Given a customer with id "10"
    And 1 token is generated for the customer
    Then the token created belongs to the customer
