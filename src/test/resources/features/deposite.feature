Feature: Deposit Amount (Customer)

  Scenario: Deposit Amount - Positive
    Given user is logged in as customer index 1
    When user enters deposit amount "500"
    And clicks on Deposit button
    Then deposit success message should be displayed

  Scenario: Deposit Amount - Negative
    Given user is logged in as customer index 1
    When user enters deposit amount "-100"
    And clicks on Deposit button
    Then deposit error message should be displayed
