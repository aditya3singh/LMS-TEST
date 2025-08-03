package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.*;
import utils.ConfigReader;
// import utils.DatabaseUtils; // COMMENTED OUT FOR NOW

public class BaseTest {
    protected WebDriver driver;
    protected ConfigReader config;
    // protected DatabaseUtils dbUtils; // COMMENTED OUT FOR NOW

    @BeforeSuite
    public void suiteSetup() {
        config = new ConfigReader();
        // dbUtils = new DatabaseUtils(); // COMMENTED OUT FOR NOW
        System.out.println("Test Suite Started - LMS Application Testing");
        System.out.println("Database validation temporarily disabled for initial testing");
    }

    @BeforeMethod
    public void setUp() {
        setupDriver();
        driver.manage().window().maximize();
        driver.get(config.getProperty("app.url"));
    }

    private void setupDriver() {
        String browser = config.getProperty("browser");

        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (Boolean.parseBoolean(config.getProperty("headless"))) {
                    chromeOptions.addArguments("--headless");
                }
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                driver = new ChromeDriver(chromeOptions);
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;

            default:
                throw new RuntimeException("Browser not supported: " + browser);
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterSuite
    public void suiteTearDown() {
        // dbUtils.closeConnection(); // COMMENTED OUT FOR NOW
        System.out.println("Test Suite Completed");
    }
}
