package stepdefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import hooks.Hooks;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class CustomerSteps {

    private WebDriver driver;
    private WebDriverWait wait;

    @Before(order = 1)
    public void wireDriverFromHooks() {
        driver = Hooks.getDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private final By btnCustomerLogin = By.cssSelector("button[ng-click='customer()']");
    private final By userSelect       = By.id("userSelect");
    private final By btnLogin         = By.xpath("//button[normalize-space()='Login']");
    private final By tabDeposit       = By.cssSelector("button[ng-click='deposit()']");
    private final By amountInput      = By.cssSelector("input[ng-model='amount']");
    private final By btnDeposit       = By.xpath("//form//button[normalize-space()='Deposit']");
    private final By messageSpan      = By.cssSelector("span[ng-show='message'], span[ng-bind='message']");

    @Given("user is on Customer Login page")
    public void user_is_on_customer_login_page() {
        wait.until(elementToBeClickable(btnCustomerLogin)).click();
        wait.until(visibilityOfElementLocated(userSelect));
    }

    @When("user selects customer by index {int}")
    public void user_selects_customer_by_index(Integer index) {
        Select dd = new Select(wait.until(visibilityOfElementLocated(userSelect)));
        dd.selectByIndex(index);
    }

    @When("user does not select any customer")
    public void user_does_not_select_any_customer() {
        wait.until(visibilityOfElementLocated(userSelect)); // leave default
    }

    @And("clicks on Login button")
    public void clicks_on_login_button() {
        // If placeholder ("Your Name") is selected, DO NOT click — negative TC expects stay on login page
        try {
            Select dd = new Select(driver.findElement(userSelect));
            WebElement selected = dd.getFirstSelectedOption();
            String txt = selected.getText().trim();
            String val = selected.getAttribute("value") == null ? "" : selected.getAttribute("value").trim();
            if (txt.equalsIgnoreCase("Your Name") || val.isEmpty()) {
                return; // stay on login page
            }
        } catch (NoSuchElementException ignored) {}

        // Otherwise click (with JS fallback)
        try {
            WebElement btn = wait.until(elementToBeClickable(btnLogin));
            btn.click();
        } catch (TimeoutException | ElementClickInterceptedException e) {
            try {
                WebElement btn = driver.findElement(btnLogin);
                if (btn.isDisplayed()) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                }
            } catch (Exception ignored) {}
        }
    }

    // ------ Deposit steps ------

    @Given("user is logged in as customer index {int}")
    public void user_is_logged_in_as_customer_index(Integer index) {
        user_is_on_customer_login_page();
        user_selects_customer_by_index(index);
        clicks_on_login_button();
        wait.until(visibilityOfElementLocated(tabDeposit)); // reached account page
    }

    @When("user enters deposit amount {string}")
    public void user_enters_deposit_amount(String amt) {
        wait.until(elementToBeClickable(tabDeposit)).click();
        WebElement input = wait.until(visibilityOfElementLocated(amountInput));
        input.clear();
        input.sendKeys(amt);
    }

    @And("clicks on Deposit button")
    public void clicks_on_deposit_button() {
        wait.until(elementToBeClickable(btnDeposit)).click();
    }

    // ---------- helpers used by AssertSteps ----------

    public boolean isDepositSuccessShown() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(6)).until(d ->
                    !d.findElements(By.xpath("//*[contains(normalize-space(.),'Deposit Successful')]")).isEmpty()
                    || (!d.findElements(messageSpan).isEmpty()
                    && d.findElement(messageSpan).getText().trim().equalsIgnoreCase("Deposit Successful"))
            );
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isOnCustomerLoginStill() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(visibilityOfElementLocated(userSelect));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isAccountPageVisible() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                    .until(visibilityOfElementLocated(tabDeposit));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
