package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import base.BasePage;

public class HomePage extends BasePage {
    private final By loginModalClose = By.cssSelector("span.commonModal__close");
    private final By flightsTab = By.cssSelector("a[href*='/flights']");
    private final By flightsTabAlt = By.xpath("//a[normalize-space()='Flights']");
    private final By flightsTabLegacy = By.cssSelector("li[data-cy='menu_Flights']");
    private final By flightsHome = By.cssSelector("a.brand[href='index.php'], a.brand[href='home']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void closeLoginIfPresent() {
        dismissCommonOverlays();
        if (isPresent(loginModalClose)) {
            safeClick(loginModalClose);
        }
        dismissCommonOverlays();
    }

    public FlightsPage openFlightsSection() {
        closeLoginIfPresent();
        boolean clicked = clickIfVisible(flightsTab)
                || clickIfVisible(flightsTabAlt)
                || clickIfVisible(flightsTabLegacy)
                || clickIfVisible(flightsHome);
        if (!clicked) {
            System.out.println("Flights tab not visible, continuing on current page");
        }
        return new FlightsPage(driver);
    }

    private boolean clickIfVisible(By locator) {
        for (org.openqa.selenium.WebElement element : driver.findElements(locator)) {
            try {
                if (element.isDisplayed()) {
                    safeClick(element);
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }
}
