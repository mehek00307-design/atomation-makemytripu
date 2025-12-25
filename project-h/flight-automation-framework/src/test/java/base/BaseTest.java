package base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import pages.FlightsPage;
import pages.HomePage;
import pages.SearchResultsPage;
import utils.ConfigReader;
import utils.DriverFactory;

public class BaseTest {
    protected WebDriver driver;
    protected HomePage homePage;
    protected FlightsPage flightsPage;
    protected SearchResultsPage searchResultsPage;

    @BeforeClass
    public void setUp() {
        try {
            driver = DriverFactory.createInstance();
            String baseUrl = ConfigReader.getProperty("baseUrl");
            if (baseUrl == null || baseUrl.trim().isEmpty()) {
                throw new IllegalStateException("baseUrl is missing in config.properties");
            }
            driver.get(baseUrl);
            homePage = new HomePage(driver);
            flightsPage = new FlightsPage(driver);
            searchResultsPage = new SearchResultsPage(driver);
        } catch (Exception e) {
            System.out.println("Setup failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
