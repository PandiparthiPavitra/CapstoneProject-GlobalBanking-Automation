
package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage {

    private WebDriver driver;
    public HomePage(WebDriver driver) { this.driver = driver; }

    public void open() {
        driver.get("https://globalsqa.com/angularJs-protractor/BankingProject/#/login");
    }

    public void goToManager() {
        driver.findElement(By.cssSelector("button[ng-click='manager()']")).click();
    }

    public void goToCustomer() {
        driver.findElement(By.cssSelector("button[ng-click='customer()']")).click();
    }
}
