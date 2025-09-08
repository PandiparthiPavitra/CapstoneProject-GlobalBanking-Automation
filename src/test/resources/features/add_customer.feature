Feature: Customer Login

  Scenario: Positive Customer Login
    Given user is on Customer Login page
    When user selects customer by index 2
    And clicks on Login button
    Then customer account page should be displayed

  Scenario: Negative Customer Login
    Given user is on Customer Login page
    When user does not select any customer
    And clicks on Login button
    Then error message "Please select a customer." should be displayed
