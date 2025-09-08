package stepdefinitions;

import base.DriverFactory;
import io.cucumber.java.en.Given;

public class CommonSteps {

    @Given("I open the banking site")
    public void i_open_the_banking_site() {
        DriverFactory.getDriver()
            .get("https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login");
    }
}
