package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import utils.ConfigReader;

public final class DriverFactory {
    private static final ThreadLocal<WebDriver> TL_DRIVER = new ThreadLocal<>();

    private DriverFactory(){}

    public static WebDriver getDriver() { return TL_DRIVER.get(); }

    public static void initDriver() {
        if (TL_DRIVER.get() != null) return;

        String browser = ConfigReader.get("browser").toLowerCase();
        boolean headless = ConfigReader.getBoolean("headless");

        switch (browser) {
            case "chrome" -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions opts = new ChromeOptions();
                if (headless) opts.addArguments("--headless=new");
                opts.addArguments("--remote-allow-origins=*");
                TL_DRIVER.set(new ChromeDriver(opts));
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions opts = new EdgeOptions();
                if (headless) opts.addArguments("--headless=new");
                TL_DRIVER.set(new EdgeDriver(opts));
            }
            case "firefox", "ff" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions opts = new FirefoxOptions();
                if (headless) opts.addArguments("--headless");
                TL_DRIVER.set(new FirefoxDriver(opts));
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        WebDriver driver = TL_DRIVER.get();
        if (ConfigReader.getBoolean("maximize")) driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getInt("implicitWait")));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getInt("pageLoadTimeout")));
    }

    public static void quitDriver() {
        WebDriver d = TL_DRIVER.get();
        if (d != null) {
            d.quit();
            TL_DRIVER.remove();
        }
    }
}
