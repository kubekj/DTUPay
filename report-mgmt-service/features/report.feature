####################################################################################
# Developed by Jakub (s232946) assisted by Andreas (s176334) and Enrico (s232438) ##
####################################################################################

Feature: Report generation feature

  #####   MANAGER   ######
  Scenario: Merchant request a report with registered transactions
    Then there are 5 transactions registered
    When a "AllPaymentsReturned" event is received with a list of payments
    Then the "ManagerReportGenerated" manager event is sent
    And the manager report contains the correct payments

  Scenario: DTU Pay Manager requests a report with no transactions
    When a "ManagerReportRequested" event for reports is received
    And an empty transaction list from "AllPaymentsReturned" event for payments is received
    Then "ManagerReportGenerated" event is trying to generate a report
    But the ReportCreationException with a error message "There are no transactions to report" is thrown

  #####   MERCHANT   ######
  Scenario: Merchant request a report with registered transactions
    Then there are 5 transactions registered
    When a "MerchantPaymentsReturned" event is received with a list of payments
    Then the "MerchantReportGenerated" merchant event is sent
    And the merchant report contains the correct payments

  Scenario: Merchant requests a report with no transactions
    When a "MerchantReportRequested" event for reports is sent with userId "4449cbc7-bc5e-4d59-9abe-6368b6e96953"
    And an empty transaction list from "MerchantPaymentsReturned" event for payments is received
    Then "MerchantReportGenerated" event is trying to generate a report
    But the ReportCreationException with a error message "There are no transactions to report" is thrown

  #####   CUSTOMER   ######
  Scenario: Customer request a report with registered transactions
    Then there are 5 transactions registered
    When a "CustomerPaymentsReturned" event is received with a list of payments
    Then the "CustomerReportGenerated" customer event is sent
    And the customer report contains the correct payments

  Scenario: Customer requests a report with no registered transactions
    When a "CustomerReportRequested" event for reports is sent with userId "4449cbc7-bc5e-4d59-9abe-6368b6e96953"
    And an empty transaction list from "CustomerPaymentsReturned" event for payments is received
    Then "CustomerReportGenerated" event is trying to generate a report
    But the ReportCreationException with a error message "There are no transactions to report" is thrown

