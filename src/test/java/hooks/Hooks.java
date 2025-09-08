package hooks;

import io.cucumber.java.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Properties;

public class Hooks {

    private static final Properties CONFIG = new Properties();
    private static WebDriver driver;

    // ---------- load config once (no extra classes) ----------
    static {
        try (InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("config/config.properties")) {
            if (is == null) throw new IllegalStateException("Missing config/config.properties on classpath");
            CONFIG.load(is);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Failed to load config.properties: " + e);
        }
    }

    private static String cfg(String key) {
        String v = CONFIG.getProperty(key);
        if (v == null) throw new IllegalArgumentException("Missing key: " + key);
        return v.trim();
    }

    public static WebDriver getDriver() { return driver; }

    @Before(order = 0)
    public void setUp() {
        if (driver != null) return;

        String browser   = cfg("browser").toLowerCase();
        boolean headless = Boolean.parseBoolean(cfg("headless"));
        boolean maximize = Boolean.parseBoolean(cfg("maximize"));
        int implicit     = Integer.parseInt(cfg("implicitWait"));
        int pageLoad     = Integer.parseInt(cfg("pageLoadTimeout"));

        switch (browser) {
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions o = new EdgeOptions();
                if (headless) o.addArguments("--headless=new");
                driver = new EdgeDriver(o);
            }
            case "firefox", "ff" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions o = new FirefoxOptions();
                if (headless) o.addArguments("--headless");
                driver = new FirefoxDriver(o);
            }
            default -> { // chrome
                WebDriverManager.chromedriver().setup();
                ChromeOptions o = new ChromeOptions();
                if (headless) o.addArguments("--headless=new");
                o.addArguments("--remote-allow-origins=*");
                driver = new ChromeDriver(o);
            }
        }

        if (maximize) driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicit));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoad));

        // open URL from config.properties
        driver.get(cfg("baseUrl"));
    }

    // ---------- Screenshot helper ----------
    private void captureAndAttach(Scenario scenario, String label) {
        if (driver == null) return;
        try {
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(png, "image/png", label);

            // also save to disk so it appears in Extent "media"
            File outDir = new File("target/extent/screenshots");
            if (!outDir.exists()) outDir.mkdirs();
            File file = new File(outDir, System.currentTimeMillis() + "_" + label.replaceAll("\\s+", "_") + ".png");
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), file.toPath());
        } catch (Throwable ignored) {}
    }

    // ---------- Screenshot after EVERY step (+ve and -ve) ----------
    @AfterStep
    public void afterEachStep(Scenario scenario) {
        captureAndAttach(scenario, "Step");
    }

    // ---------- Always capture on failure; quit driver ----------
    @After(order = 1)
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                captureAndAttach(scenario, "Failure");
            }
        } finally {
            if (driver != null) {
                driver.quit();
                driver = null;
            }
        }
    }
}
