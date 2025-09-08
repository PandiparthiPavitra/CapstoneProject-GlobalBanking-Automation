package stepdefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import hooks.Hooks;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class ManagerSteps {

    private WebDriver driver;
    private WebDriverWait wait;

    // Shared across assertions
    public static String lastAlertText = null;

    // Keep the last typed data so Assert can verify via Customers tab if no alert pops
    public static String typedFirst = null;
    public static String typedLast  = null;
    public static String typedPost  = null;

    @Before(order = 1)
    public void wireDriverFromHooks() {
        driver = Hooks.getDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        lastAlertText = null;
    }

    private static final By btnManagerLogin = By.cssSelector("button[ng-click='manager()']");
    private static final By tabOpenAccount  = By.cssSelector("button[ng-click='openAccount()']");
    private static final By tabAddCustomer  = By.cssSelector("button[ng-click='addCust()']");
    private static final By tabCustomers    = By.cssSelector("button[ng-click='showCust()']");

    private static final By openCustomerSel = By.id("userSelect");
    private static final By currencySel     = By.id("currency");
    private static final By btnProcess      = By.xpath("//button[normalize-space()='Process']");

    private static final By fName           = By.cssSelector("input[ng-model='fName']");
    private static final By lName           = By.cssSelector("input[ng-model='lName']");
    private static final By postCd          = By.cssSelector("input[ng-model='postCd']");
    private static final By btnAddCustomer  = By.xpath("//button[normalize-space()='Add Customer']");

    private void goManagerHome() {
        wait.until(elementToBeClickable(btnManagerLogin)).click();
    }

    // -------- Open Account --------

    @Given("user is on Open Account page")
    public void user_is_on_open_account_page() {
        goManagerHome();
        wait.until(elementToBeClickable(tabOpenAccount)).click();
        wait.until(visibilityOfElementLocated(openCustomerSel));
    }

    @When("user selects customer by index {int} and currency by index {int}")
    public void user_selects_customer_by_index_and_currency_by_index(Integer custIdx, Integer currIdx) {
        new Select(wait.until(visibilityOfElementLocated(openCustomerSel))).selectByIndex(custIdx);
        new Select(wait.until(visibilityOfElementLocated(currencySel))).selectByIndex(currIdx);
    }

    @When("user does not select customer and currency")
    public void user_does_not_select_customer_and_currency() {
        wait.until(visibilityOfElementLocated(openCustomerSel));
        wait.until(visibilityOfElementLocated(currencySel));
    }

    @And("clicks on Process button")
    public void clicks_on_process_button() {
        lastAlertText = null;
        wait.until(elementToBeClickable(btnProcess)).click();
        try {
            Alert a = new WebDriverWait(driver, Duration.ofSeconds(6)).until(d -> {
                try { return d.switchTo().alert(); } catch (NoAlertPresentException ex) { return null; }
            });
            if (a != null) {
                lastAlertText = a.getText();
                a.accept();
            }
        } catch (TimeoutException ignored) { }
    }

    // -------- Add Customer --------

    @Given("user is on Add Customer page")
    public void user_is_on_add_customer_page() {
        goManagerHome();
        wait.until(elementToBeClickable(tabAddCustomer)).click();
        wait.until(visibilityOfElementLocated(fName));
    }

    @When("user enters first name {string} last name {string} and postcode {string}")
    public void user_enters_first_name_last_name_and_postcode(String fn, String ln, String pc) {
        typedFirst = fn;
        typedLast  = ln;
        typedPost  = pc;

        driver.findElement(fName).clear();  driver.findElement(fName).sendKeys(fn);
        driver.findElement(lName).clear();  driver.findElement(lName).sendKeys(ln);
        driver.findElement(postCd).clear(); driver.findElement(postCd).sendKeys(pc);
    }

    @And("clicks on Add Customer button")
    public void clicks_on_add_customer_button() {
        lastAlertText = null;

        WebElement btn = wait.until(visibilityOfElementLocated(btnAddCustomer));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); // more reliable with Angular

        // Try to capture the JavaScript alert (some environments delay it)
        long end = System.currentTimeMillis() + 8000; // give up to 8s
        while (System.currentTimeMillis() < end) {
            try {
                Alert a = driver.switchTo().alert();
                lastAlertText = a.getText();
                a.accept();
                return;
            } catch (NoAlertPresentException ex) {
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            }
        }
        // No alert seen — Assert step will verify via Customers tab using typedFirst/typedLast
    }

    // helpers for assertions
    public static boolean wasAnyAlertShown() { return lastAlertText != null; }

    public static boolean isOnOpenAccountPage() {
        WebDriver d = Hooks.getDriver();
        try {
            new WebDriverWait(d, Duration.ofSeconds(3))
                .until(visibilityOfElementLocated(openCustomerSel));
            return true;
        } catch (TimeoutException e) { return false; }
    }
}
