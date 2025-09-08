
package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AccountPage {

    private WebDriver driver;
    public AccountPage(WebDriver driver) { this.driver = driver; }

    public void clickDepositTab() { driver.findElement(By.cssSelector("button[ng-click='deposit()']")).click(); }

    public void enterDeposit(String amount) {
        driver.findElement(By.cssSelector("input[ng-model='amount']")).clear();
        driver.findElement(By.cssSelector("input[ng-model='amount']")).sendKeys(amount);
    }

    public void clickDepositButton() { driver.findElement(By.cssSelector("button[type='submit']")).click(); }

    public String getMessage() { return driver.findElement(By.cssSelector("span[ng-show='message']")).getText(); }
}
