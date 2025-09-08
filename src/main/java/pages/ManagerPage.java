
package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ManagerPage {

    private WebDriver driver;
    public ManagerPage(WebDriver driver) { this.driver = driver; }

    public void openAddCustomer() {
        driver.findElement(By.cssSelector("button[ng-click='addCust()']")).click();
    }

    public void openOpenAccount() {
        driver.findElement(By.cssSelector("button[ng-click='openAccount()']")).click();
    }
}
