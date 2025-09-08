Feature: Add New Customer (Manager)

  Scenario: Add customer with valid details
    Given user is on Add Customer page
    When user enters first name "Pavitra" last name "P" and postcode "523157"
    And clicks on Add Customer button
    Then success message for customer creation should be displayed

  Scenario: Add customer with empty details
    Given user is on Add Customer page
    When user enters first name "" last name "" and postcode ""
    And clicks on Add Customer button
    Then an error/validation alert for customer creation should be displayed
