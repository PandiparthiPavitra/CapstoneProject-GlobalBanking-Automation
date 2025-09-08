Feature: Open New Account (Manager)

  Scenario: Positive Open Account
    Given user is on Open Account page
    When user selects customer by index 2 and currency by index 1
    And clicks on Process button
    Then account creation success message should be displayed

  Scenario: Negative Open Account
    Given user is on Open Account page
    When user does not select customer and currency
    And clicks on Process button
    Then error message "Please select customer and currency." should be displayed
