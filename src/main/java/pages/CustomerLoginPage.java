package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class CustomerLoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By btnCustomerLogin = By.xpath("//button[contains(.,'Customer Login')]");
    private final By userSelect = By.id("userSelect");
    private final By loginBtn   = By.cssSelector("button[type='submit']");
    private final By logoutBtn  = By.cssSelector("button[ng-click*='byebye']");

    public CustomerLoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(8));
    }

    public void openCustomerLoginPage(String baseUrl) {
        driver.get(baseUrl); // home or login
        try {
            driver.findElement(btnCustomerLogin).click(); // if already on login page, this will just fail silently
        } catch (NoSuchElementException ignored) {}
        wait.until(ExpectedConditions.visibilityOfElementLocated(userSelect));
    }

    public void selectCustomerByIndex(int idx) {
        Select sel = new Select(wait.until(ExpectedConditions.elementToBeClickable(userSelect)));
        sel.selectByIndex(idx);
    }

    public void clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn)).click();
    }

    public boolean isOnCustomerLoginPage() {
        try { return wait.until(ExpectedConditions.visibilityOfElementLocated(userSelect)).isDisplayed(); }
        catch (TimeoutException e) { return false; }
    }

    public boolean isOnAccountPage() {
        try { return wait.until(ExpectedConditions.visibilityOfElementLocated(logoutBtn)).isDisplayed(); }
        catch (TimeoutException e) { return false; }
    }

    public String consumeAlertIfPresent() {
        try {
            Alert a = new WebDriverWait(driver, Duration.ofSeconds(2)).until(ExpectedConditions.alertIsPresent());
            String text = a.getText();
            a.accept();
            return text;
        } catch (TimeoutException e) {
            return null;
        }
    }
}
