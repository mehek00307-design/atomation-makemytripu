package base;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected List<WebElement> waitForAllVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected List<WebElement> waitForAllPresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        waitForClickable(locator).click();
    }

    protected void safeClick(By locator) {
        try {
            click(locator);
        } catch (ElementClickInterceptedException | TimeoutException e) {
            jsClick(locator);
        }
    }

    protected void jsClick(By locator) {
        WebElement element = waitForVisible(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void safeClick(WebElement element) {
        try {
            element.click();
        } catch (ElementClickInterceptedException | StaleElementReferenceException e) {
            jsClick(element);
        }
    }

    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void type(By locator, String text) {
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected void setValue(By locator, String value, String attributeValue) {
        WebElement element = waitForVisible(locator);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];"
                        + "if (arguments[2] !== null) { arguments[0].setAttribute('value', arguments[2]); }"
                        + "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));"
                        + "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                element, value, attributeValue
        );
    }

    protected void pressEnter(By locator) {
        waitForVisible(locator).sendKeys(Keys.ENTER);
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected void scrollIntoView(By locator) {
        WebElement element = waitForVisible(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    protected void waitForTitleContains(String text) {
        wait.until(ExpectedConditions.titleContains(text));
    }

    protected void dismissCommonOverlays() {
        try {
            if (driver.getWindowHandles().isEmpty()) {
                return;
            }
        } catch (WebDriverException ignored) {
            return;
        }

        try {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        } catch (WebDriverException ignored) {
        }

        List<By> closeLocators = Arrays.asList(
                By.cssSelector("span.commonModal__close"),
                By.cssSelector("span[aria-label='Close'], span[aria-label='close']"),
                By.cssSelector("button[aria-label='Close'], button[aria-label='close']"),
                By.cssSelector("div[role='dialog'] button, div[role='dialog'] span"),
                By.cssSelector("span.close, button.close")
        );

        for (By locator : closeLocators) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed()) {
                        jsClick(element);
                    }
                } catch (WebDriverException ignored) {
                }
            }
        }

        try {
            ((JavascriptExecutor) driver).executeScript(
                    "var imgs = document.querySelectorAll(\"img[src*='promos.makemytrip.com/notification']\");"
                            + "imgs.forEach(function(img){var blocker=img.closest('div');"
                            + "if(blocker){blocker.style.display='none';}});"
            );
        } catch (WebDriverException ignored) {
        }
    }
}
