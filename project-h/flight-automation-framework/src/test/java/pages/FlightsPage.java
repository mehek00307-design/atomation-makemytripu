

package pages;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BasePage;
import utils.DateUtils;

public class FlightsPage extends BasePage {
    private final List<By> fromInputCandidates = Arrays.asList(
            By.cssSelector("input[placeholder*='From']"),
            By.cssSelector("input[aria-label*='From']"),
            By.cssSelector("input[id*='origin']"),
            By.cssSelector("input[name*='origin']"),
            By.cssSelector("[data-testid='originId'] input"),
            By.cssSelector("[data-testid*='origin'] input"),
            By.cssSelector("[data-testid*='source'] input"),
            By.xpath("//label[contains(normalize-space(.),'From')]/following::input[1]")
    );

    private final List<By> toInputCandidates = Arrays.asList(
            By.cssSelector("input[placeholder*='To']"),
            By.cssSelector("input[aria-label*='To']"),
            By.cssSelector("input[id*='destination']"),
            By.cssSelector("input[name*='destination']"),
            By.cssSelector("[data-testid='destinationId'] input"),
            By.cssSelector("[data-testid*='destination'] input"),
            By.cssSelector("[data-testid*='dest'] input"),
            By.xpath("//label[contains(normalize-space(.),'To')]/following::input[1]")
    );

    private final List<By> originTriggerCandidates = Arrays.asList(
            By.cssSelector("[data-testid='originId']"),
            By.cssSelector("[data-testid*='origin']"),
            By.cssSelector("[data-testid*='from']"),
            By.xpath("//*[@data-testid='originId']"),
            By.xpath("//*[contains(@data-testid,'origin')]"),
            By.xpath("//*[contains(@data-testid,'from')]")
    );

    private final List<By> destinationTriggerCandidates = Arrays.asList(
            By.cssSelector("[data-testid='destinationId']"),
            By.cssSelector("[data-testid*='destination']"),
            By.cssSelector("[data-testid*='to']"),
            By.xpath("//*[@data-testid='destinationId']"),
            By.xpath("//*[contains(@data-testid,'destination')]"),
            By.xpath("//*[contains(@data-testid,'to')]")
    );

    private final List<By> citySearchInputCandidates = Arrays.asList(
            By.cssSelector("div[role='dialog'] input"),
            By.cssSelector("div[class*='shadow-500'] input"),
            By.cssSelector("div[role='dialog'] [contenteditable='true']"),
            By.cssSelector("input[placeholder*='Search']"),
            By.cssSelector("input[placeholder*='From']"),
            By.cssSelector("input[placeholder*='To']"),
            By.cssSelector("input[aria-label*='Search']"),
            By.cssSelector("input[aria-label*='From']"),
            By.cssSelector("input[aria-label*='To']"),
            By.cssSelector("input[type='search']"),
            By.cssSelector("input[role='combobox']"),
            By.cssSelector("input[role='textbox']"),
            By.cssSelector("input[type='text']"),
            By.cssSelector("[contenteditable='true'][role='textbox']")
    );

    private final List<By> fromFieldCandidates = Arrays.asList(
            By.cssSelector("[data-testid*='from']"),
            By.cssSelector("[data-testid*='origin']"),
            By.cssSelector("[aria-label*='From']"),
            By.xpath("//*[normalize-space(.)='From']/ancestor::*[self::div or self::button][1]"),
            By.xpath("//*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'from')][@role='combobox' or @role='textbox']")
    );

    private final List<By> toFieldCandidates = Arrays.asList(
            By.cssSelector("[data-testid*='to']"),
            By.cssSelector("[data-testid*='destination']"),
            By.cssSelector("[aria-label*='To']"),
            By.xpath("//*[normalize-space(.)='To']/ancestor::*[self::div or self::button][1]"),
            By.xpath("//*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'to')][@role='combobox' or @role='textbox']")
    );

    private final List<By> dateTriggerCandidates = Arrays.asList(
            By.cssSelector("[data-testid='departureDate']"),
            By.cssSelector("[data-testid*='depart']"),
            By.cssSelector("input[placeholder*='Depart']"),
            By.cssSelector("input[placeholder*='Departure']"),
            By.cssSelector("input[aria-label*='Depart']"),
            By.cssSelector("input[aria-label*='Departure']"),
            By.cssSelector("button[aria-label*='Depart']"),
            By.xpath("//*[self::div or self::span or self::button][contains(normalize-space(.),'Depart')]"),
            By.xpath("//*[normalize-space(.)='Departure']/ancestor::*[self::div or self::button][1]")
    );

    private final List<By> searchButtonCandidates = Arrays.asList(
            By.cssSelector("[data-testid='searchFlight']"),
            By.cssSelector("button[type='submit']"),
            By.cssSelector("button[class*='search']"),
            By.xpath("//button[contains(normalize-space(.),'Search')]"),
            By.cssSelector("div[role='button'][class*='search']"),
            By.xpath("//*[self::div or self::span][@role='button' and contains(normalize-space(.),'Search')]")
    );

    private final By suggestionOptions = By.cssSelector(
            "[role='option'], [data-testid*='auto'], ul[role='listbox'] li, div[role='listbox'] div,"
                    + " div[class*='suggest'], li[class*='suggest'], div[class*='airport'],"
                    + " ul[class*='dropdown'], li[class*='dropdown'], div[class*='dropdown'],"
                    + " [data-testid*='suggestion'], [data-testid*='dropdown'],"
                    + " .suggestion-item, .autocomplete-item, .dropdown-item,"
                    + " .ui-menu-item, .typeahead-item, .select2-result"
    );

    private final By nextMonthButton = By.cssSelector(
            "button[aria-label*='Next'], button[aria-label*='next'], div[aria-label*='Next'], div[aria-label*='next'],"
                    + " button[data-testid*='next'], div[data-testid*='next'], button[data-testid*='forward'], div[data-testid*='forward']"
    );
    private final By prevMonthButton = By.cssSelector(
            "button[aria-label*='Prev'], button[aria-label*='Previous'], button[aria-label*='Back'],"
                    + " div[aria-label*='Prev'], div[aria-label*='Previous'], div[aria-label*='Back'],"
                    + " button[data-testid*='prev'], div[data-testid*='prev'], button[data-testid*='back'], div[data-testid*='back']"
    );

    public FlightsPage(WebDriver driver) {
        super(driver);
    }

    public void enterFromCity(String fromCity) {
        dismissCommonOverlays();
        ensureFlightsFrame();
        if (!enterCityUsingTrigger(originTriggerCandidates, fromCity, "from city")) {
            WebElement input = resolveCityInput("from city", fromFieldCandidates, fromInputCandidates, buildFromKeywords());
            if (input == null) {
                logInputDiagnostics();
                throw new RuntimeException("Unable to locate from city input on Ixigo");
            }
            focusAndType(input, fromCity);
            selectSuggestion(fromCity, input);
            logInputValue("From", input);
        }
        ensureCitySelectedOrThrow(fromCity, buildFromDisplayCandidates(), "from city");
    }

    public void enterToCity(String toCity) {
        dismissCommonOverlays();
        ensureFlightsFrame();
        if (!enterCityUsingTrigger(destinationTriggerCandidates, toCity, "to city")) {
            WebElement input = resolveCityInput("to city", toFieldCandidates, toInputCandidates, buildToKeywords());
            if (input == null) {
                logInputDiagnostics();
                throw new RuntimeException("Unable to locate to city input on Ixigo");
            }
            focusAndType(input, toCity);
            selectSuggestion(toCity, input);
            logInputValue("To", input);
        }
        ensureCitySelectedOrThrow(toCity, buildToDisplayCandidates(), "to city");
    }

    public void selectDepartureDate(LocalDate targetDate) {
        dismissCommonOverlays();
        ensureFlightsFrame();
        WebElement trigger = findDepartureTrigger();
        if (trigger == null) {
            throw new RuntimeException("Departure date trigger not found on the page");
        }
        
        System.out.println("Attempting to select departure date: " + DateUtils.toIsoDate(targetDate));
        safeClick(trigger);
        waitForCalendarReady();
        
        if (!selectDateFromCalendar(targetDate)) {
            System.out.println("Calendar selection failed, trying fallback methods");
            String isoDate = DateUtils.toIsoDate(targetDate);
            if ("input".equalsIgnoreCase(trigger.getTagName())) {
                setInputValue(trigger, isoDate);
                System.out.println("Departure date set via input field: " + isoDate);
            } else {
                // Try JavaScript approach
                try {
                    ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].value = arguments[1];" +
                        "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));" +
                        "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                        trigger, isoDate
                    );
                    System.out.println("Departure date set via JavaScript: " + isoDate);
                } catch (Exception e) {
                    System.out.println("JavaScript fallback also failed: " + e.getMessage());
                }
            }
        }
        logDepartureValue(targetDate);
        
        // Verify the date was actually set
        if (!isDepartureSetToDate(targetDate)) {
            throw new RuntimeException("Failed to set departure date to: " + DateUtils.toIsoDate(targetDate));
        }
    }

    public void selectDepartureDateForNextMonth(int dayOfMonth) {
        selectDepartureDate(DateUtils.getNextMonthDate(dayOfMonth));
    }

    public void clickSearch() {
        ensureFlightsFrame();
        for (By locator : searchButtonCandidates) {
            List<WebElement> buttons = driver.findElements(locator);
            for (WebElement button : buttons) {
                try {
                    if (button.isDisplayed() && button.isEnabled()) {
                        safeClick(button);
                        return;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        throw new RuntimeException("Search button not found on Ixigo flights page");
    }

    public void syncSearchBarWithUrl(String fromCity, String toCity, LocalDate targetDate) {
        dismissCommonOverlays();
        ensureFlightsFrame();
        WebElement fromField = findFirstVisibleOrNull(buildFromDisplayCandidates(), Duration.ofSeconds(6));
        if (fromField != null) {
            setDisplayTextIfPossible(fromField, fromCity);
        }
        WebElement toField = findFirstVisibleOrNull(buildToDisplayCandidates(), Duration.ofSeconds(6));
        if (toField != null) {
            setDisplayTextIfPossible(toField, toCity);
        }
        WebElement departureField = findDepartureTrigger();
        setDisplayTextIfPossible(departureField, DateUtils.formatForAriaLabel(targetDate));
    }

    private void ensureFlightsFrame() {
        driver.switchTo().defaultContent();
        System.out.println("Ensuring flights frame, URL: " + driver.getCurrentUrl());
        boolean defaultHasInputs = hasVisibleElement(fromInputCandidates) || hasVisibleElement(toInputCandidates)
                || hasVisibleElement(fromFieldCandidates) || hasVisibleElement(toFieldCandidates);
        if (defaultHasInputs) {
            return;
        }
        List<WebElement> frames = driver.findElements(By.tagName("iframe"));
        System.out.println("Frames detected: " + frames.size());
        if (frames.isEmpty()) {
            try {
                WebDriverWait frameWait = new WebDriverWait(driver, Duration.ofSeconds(25));
                frames = frameWait.until(webDriver -> {
                    List<WebElement> found = webDriver.findElements(By.tagName("iframe"));
                    return found.isEmpty() ? null : found;
                });
                System.out.println("Frames detected after wait: " + frames.size());
            } catch (TimeoutException ignored) {
                return;
            }
        }
        int index = 0;
        for (WebElement frame : frames) {
            try {
                driver.switchTo().defaultContent();
                String src = frame.getAttribute("src");
                if (src != null && !src.isEmpty()) {
                    String normalized = src.toLowerCase(Locale.ENGLISH);
                    if (!normalized.contains("ixigo") && !normalized.contains("flight") && !normalized.contains("vimaan")) {
                        index++;
                        continue;
                    }
                }
                driver.switchTo().frame(frame);
                System.out.println("Checking iframe[" + index + "] src=" + src);
                if (hasVisibleElement(fromInputCandidates) || hasVisibleElement(toInputCandidates)
                        || hasVisibleElement(fromFieldCandidates) || hasVisibleElement(toFieldCandidates)) {
                    System.out.println("Switched to flights iframe");
                    return;
                }
                logInputDiagnostics();
            } catch (Exception ignored) {
            }
            index++;
        }
        driver.switchTo().defaultContent();
        if (defaultHasInputs) {
            System.out.println("Using default content for flights search");
        }
    }

    private void focusAndType(WebElement input, String text) {
        safeClick(input);
        try {
            input.sendKeys(Keys.CONTROL, "a");
            input.sendKeys(Keys.DELETE);
        } catch (Exception ignored) {
        }
        input.sendKeys(text);
        // Add Enter key press to trigger suggestions
        try {
            Thread.sleep(200); // Reduced from 500ms to 200ms for faster execution
            input.sendKeys(Keys.ENTER);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception ignored) {
        }
    }

    private boolean enterCityUsingTrigger(List<By> triggerCandidates, String city, String label) {
        WebElement trigger = findFirstVisibleOrNull(triggerCandidates, Duration.ofSeconds(6));
        if (trigger == null) {
            return false;
        }
        safeClick(trigger);
        if (isInputElement(trigger)) {
            focusAndType(trigger, city);
            selectSuggestion(city, trigger);
            return true;
        }
        WebElement activeInput = findActiveInput(Duration.ofSeconds(2));
        if (activeInput != null) {
            focusAndType(activeInput, city);
            selectSuggestion(city, activeInput);
            return true;
        }
        WebElement searchInput = findFirstVisibleOrNull(citySearchInputCandidates, Duration.ofSeconds(8));
        if (searchInput == null) {
            searchInput = findBestCityInput(label, buildKeywords(label));
            if (searchInput == null) {
                return false;
            }
        }
        focusAndType(searchInput, city);
        selectSuggestion(city, searchInput);
        return true;
    }

    private boolean hasVisibleElement(List<By> locators) {
        for (By locator : locators) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed()) {
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return false;
    }

    private void selectSuggestion(String city, WebElement input) {
        try {
            // Reduced wait time for faster execution
            try {
                Thread.sleep(500); // Reduced from 1000ms to 500ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // First try to find suggestions with multiple strategies
            List<WebElement> options = waitForSuggestions();
            if (options.isEmpty()) {
                // Try alternative suggestion locators
                List<By> altSuggestionLocators = Arrays.asList(
                    By.cssSelector("ul[role='listbox'] li"),
                    By.cssSelector("div[role='option']"),
                    By.cssSelector(".suggestion-item"),
                    By.cssSelector(".autocomplete-item"),
                    By.cssSelector("[data-testid*='suggestion']"),
                    By.xpath("//li[contains(@class,'suggestion') or contains(@class,'option') or contains(@class,'item')]")
                );
                
                for (By locator : altSuggestionLocators) {
                    options = driver.findElements(locator);
                    if (!options.isEmpty()) {
                        System.out.println("Found suggestions using alternative locator: " + locator);
                        break;
                    }
                }
            }
            
            WebElement match = null;
            String cityLower = city.toLowerCase(Locale.ENGLISH);
            
            // Look for exact or partial match
            for (WebElement option : options) {
                try {
                    String text = option.getText().trim().toLowerCase(Locale.ENGLISH);
                    if (text.contains(cityLower) || cityLower.contains(text)) {
                        match = option;
                        System.out.println("Found match for " + city + ": " + text);
                        break;
                    }
                } catch (Exception e) {
                    // Continue trying other options
                }
            }
            
            // If no match found, select first available option
            if (match == null && !options.isEmpty()) {
                match = options.get(0);
                System.out.println("No exact match found for " + city + ", selecting first option: " + match.getText());
            }
            
            if (match != null) {
                safeClick(match);
                // Reduced wait time
                try {
                    Thread.sleep(200); // Reduced from 500ms to 200ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return;
            }
        } catch (TimeoutException ignored) {
            System.out.println("No suggestions visible for " + city + ", trying alternative approaches");
            
            // Try multiple alternative approaches
            try {
                // Approach 1: Press Arrow Down then Enter
                input.sendKeys(Keys.ARROW_DOWN);
                Thread.sleep(100); // Reduced from 200ms to 100ms
                input.sendKeys(Keys.ENTER);
                return;
            } catch (Exception e1) {
                try {
                    // Approach 2: Press Tab then Enter
                    input.sendKeys(Keys.TAB);
                    Thread.sleep(100); // Reduced from 200ms to 100ms
                    input.sendKeys(Keys.ENTER);
                    return;
                } catch (Exception e2) {
                    try {
                        // Approach 3: Just Enter and hope for the best
                        input.sendKeys(Keys.ENTER);
                        return;
                    } catch (Exception e3) {
                        System.out.println("All suggestion selection methods failed for " + city);
                    }
                }
            }
        }
        
        // Final fallback - press Arrow Down and Enter
        try {
            input.sendKeys(Keys.ARROW_DOWN);
            try {
                Thread.sleep(100); // Reduced from 200ms to 100ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            input.sendKeys(Keys.ENTER);
        } catch (Exception ignored) {
        }
    }

    private List<WebElement> waitForSuggestions() {
        WebDriverWait suggestionWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return suggestionWait.until(webDriver -> {
            List<WebElement> options = webDriver.findElements(suggestionOptions);
            
            // Debug: Print what we found
            if (!options.isEmpty()) {
                System.out.println("Primary suggestion locators found " + options.size() + " options");
                
                // Try even more comprehensive locators
                List<By> emergencyLocators = Arrays.asList(
                    By.cssSelector("*[role='option']"),
                    By.cssSelector("*[role='listitem']"),
                    By.cssSelector("li"),
                    By.cssSelector("div[class*='item']"),
                    By.cssSelector("a[class*='suggestion']"),
                    By.cssSelector("span[class*='suggestion']"),
                    By.xpath("//*[contains(@class,'suggestion') or contains(@class,'option') or contains(@class,'item') or contains(@class,'result')]"),
                    By.xpath("//li | //div | //span | //a"),
                    By.cssSelector(".ui-autocomplete-item"),
                    By.cssSelector(".tt-suggestion"),
                    By.cssSelector(".select2-results__option")
                );
                
                for (By locator : emergencyLocators) {
                    List<WebElement> emergencyOptions = webDriver.findElements(locator);
                    if (!emergencyOptions.isEmpty()) {
                        System.out.println("Emergency locator found " + emergencyOptions.size() + " options: " + locator);
                        return emergencyOptions;
                    }
                }
            }
            
            for (WebElement option : options) {
                try {
                    if (option.isDisplayed()) {
                        System.out.println("Found visible suggestion: " + option.getText());
                        return options;
                    }
                } catch (Exception e) {
                    // Continue checking other options
                }
            }
            return null;
        });
    }

    private boolean selectDateFromCalendar(LocalDate date) {
        String iso = DateUtils.toIsoDate(date);
        String compact = DateUtils.formatAsCompactDate(date);
        
        // Try current month first
        if (tryClickDate(date, iso, compact)) {
            return true;
        }
        
        // Try next months (up to 12)
        for (int i = 0; i < 12; i++) {
            if (!clickNextMonth()) {
                break;
            }
            if (tryClickDate(date, iso, compact)) {
                return true;
            }
        }
        
        // Try previous months (up to 3)
        for (int i = 0; i < 3; i++) {
            if (!clickPrevMonth()) {
                break;
            }
            if (tryClickDate(date, iso, compact)) {
                return true;
            }
        }
        
        logCalendarDiagnostics(date);
        return false;
    }

    private boolean tryClickDate(LocalDate date, String iso, String compact) {
        List<By> candidates = buildDateLocators(date, iso, compact);
        if (clickDateCandidates(candidates, date)) {
            return true;
        }
        return clickDayInTargetMonth(date);
    }

    private List<By> buildDateLocators(LocalDate date, String iso, String compact) {
        String day = String.valueOf(date.getDayOfMonth());
        String monthShort = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        String monthFull = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String monthNumber = String.format("%02d", date.getMonthValue());
        String year = String.valueOf(date.getYear());
        List<By> locators = new ArrayList<>();
        
        // Standard date attributes
        locators.add(By.cssSelector("[data-date='" + iso + "']"));
        locators.add(By.cssSelector("[data-date='" + compact + "']"));
        locators.add(By.cssSelector("[data-testid*='" + iso + "']"));
        locators.add(By.cssSelector("[data-testid*='" + compact + "']"));
        locators.add(By.cssSelector("[data-value='" + iso + "']"));
        locators.add(By.cssSelector("[data-value='" + compact + "']"));
        
        // Day-specific attributes
        locators.add(By.cssSelector("[data-day='" + day + "'][data-month='" + monthNumber + "']"));
        locators.add(By.cssSelector("[data-day='" + day + "'][data-year='" + year + "']"));
        locators.add(By.cssSelector("[data-day='" + day + "'][data-month*='" + monthShort + "']"));
        locators.add(By.cssSelector("[data-day='" + day + "'][data-month*='" + monthFull + "']"));
        
        // Aria label candidates
        for (String label : DateUtils.getAriaLabelCandidates(date)) {
            locators.add(By.cssSelector("[aria-label='" + label + "']"));
            locators.add(By.xpath("//*[contains(@aria-label,'" + label + "')]"));
        }
        
        // Text-based locators
        locators.add(By.xpath("//*[contains(@aria-label,'" + day + "') and (contains(@aria-label,'" + monthShort + "') or contains(@aria-label,'" + monthFull + "') or contains(@aria-label,'" + year + "'))]"));
        locators.add(By.xpath("//*[(@role='gridcell' or @role='button' or @role='option') and normalize-space()='" + day + "' and not(contains(@class,'disabled'))]"));
        
        // Additional ixigo-specific patterns
        locators.add(By.xpath("//button[contains(@class,'day') and normalize-space()='" + day + "' and not(@disabled)]"));
        locators.add(By.xpath("//div[contains(@class,'day') and normalize-space()='" + day + "' and not(contains(@class,'disabled'))]"));
        locators.add(By.xpath("//span[contains(@class,'day') and normalize-space()='" + day + "' and not(contains(@class,'disabled'))]"));
        
        // Date text patterns
        locators.add(By.xpath("//*[contains(@data-date,'" + iso + "')]"));
        locators.add(By.xpath("//*[contains(@data-date,'" + compact + "')]"));
        locators.add(By.xpath("//*[contains(@data-month,'" + monthNumber + "') and normalize-space()='" + day + "']"));
        locators.add(By.xpath("//*[contains(@data-month,'" + monthShort + "') and normalize-space()='" + day + "']"));
        locators.add(By.xpath("//*[contains(@data-month,'" + monthFull + "') and normalize-space()='" + day + "']"));
        
        return locators;
    }

    private boolean clickFirstVisible(List<By> locators) {
        for (By locator : locators) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed() && element.isEnabled()) {
                        safeClick(element);
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return false;
    }

    private boolean clickNextMonth() {
        List<WebElement> buttons = driver.findElements(nextMonthButton);
        for (WebElement button : buttons) {
            try {
                if (button.isDisplayed()) {
                    safeClick(button);
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private boolean clickPrevMonth() {
        List<WebElement> buttons = driver.findElements(prevMonthButton);
        for (WebElement button : buttons) {
            try {
                if (button.isDisplayed()) {
                    safeClick(button);
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private void waitForCalendarReady() {
        List<By> calendarCandidates = Arrays.asList(
                By.cssSelector("[data-date]"),
                By.cssSelector("[role='gridcell']"),
                By.cssSelector("button[aria-label][class*='day']"),
                By.cssSelector("div[aria-label][class*='day']")
        );
        try {
            WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(8));
            localWait.until(webDriver -> {
                for (By locator : calendarCandidates) {
                    List<WebElement> elements = webDriver.findElements(locator);
                    for (WebElement element : elements) {
                        try {
                            if (element.isDisplayed()) {
                                return true;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
                return false;
            });
        } catch (TimeoutException ignored) {
        }
    }

    private boolean clickDayInTargetMonth(LocalDate date) {
        if (clickDayInMonthContainer(date)) {
            return true;
        }
        return clickDateCandidates(buildDayLocators(date.getDayOfMonth()), date);
    }

    private boolean clickDayInMonthContainer(LocalDate date) {
        String monthFull = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toLowerCase(Locale.ENGLISH);
        String monthShort = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toLowerCase(Locale.ENGLISH);
        String year = String.valueOf(date.getYear());
        String lower = "translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')";
        List<WebElement> headers = driver.findElements(By.xpath("//*[contains(" + lower + ",'" + monthFull + "')"
                + " or contains(" + lower + ",'" + monthShort + "')][contains(.,'" + year + "')]"));
        if (headers.isEmpty()) {
            headers = driver.findElements(By.xpath("//*[contains(" + lower + ",'" + monthFull + "')"
                    + " or contains(" + lower + ",'" + monthShort + "')]"));
        }
        for (WebElement header : headers) {
            try {
                WebElement container = findMonthContainer(header, date.getDayOfMonth());
                if (container != null && clickDayInContainer(container, date)) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private boolean clickDayInContainer(WebElement container, LocalDate date) {
        int day = date.getDayOfMonth();
        for (By locator : buildDayLocators(day)) {
            List<WebElement> elements = container.findElements(locator);
            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed() && element.isEnabled()) {
                        if (clickDateElement(element, date)) {
                            return true;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return false;
    }

    private List<By> buildDayLocators(int day) {
        String value = String.valueOf(day);
        List<By> locators = new ArrayList<>();
        locators.add(By.xpath("//button[normalize-space()='" + value + "' and not(@disabled) and not(contains(@class,'disabled'))]"));
        locators.add(By.xpath("//div[normalize-space()='" + value + "' and not(contains(@class,'disabled'))]"));
        locators.add(By.xpath("//span[normalize-space()='" + value + "' and not(contains(@class,'disabled'))]"));
        locators.add(By.xpath("//*[normalize-space()='" + value + "' and not(contains(@class,'disabled'))]"));
        return locators;
    }

    private WebElement findMonthContainer(WebElement header, int day) {
        WebElement current = header;
        for (int i = 0; i < 7; i++) {
            if (current == null) {
                return null;
            }
            if (containerHasDay(current, day)) {
                return current;
            }
            try {
                current = current.findElement(By.xpath(".."));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private boolean containerHasDay(WebElement container, int day) {
        String value = String.valueOf(day);
        try {
            List<WebElement> candidates = container.findElements(By.xpath(".//*[normalize-space()='" + value + "']"));
            for (WebElement candidate : candidates) {
                try {
                    if (candidate.isDisplayed()) {
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private void logDepartureValue(LocalDate targetDate) {
        String value = readDepartureText();
        if (value == null || value.trim().isEmpty()) {
            System.out.println("Departure field value not readable after selection: " + DateUtils.toIsoDate(targetDate));
            return;
        }
        System.out.println("Departure field shows: " + value);
    }

    private boolean clickDateCandidates(List<By> locators, LocalDate targetDate) {
        for (By locator : locators) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed()) {
                        if (clickDateElement(element, targetDate)) {
                            return true;
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return false;
    }

    private boolean clickDateElement(WebElement element, LocalDate targetDate) {
        safeClick(element);
        if (waitForDepartureUpdate(targetDate)) {
            return true;
        }
        WebElement ancestor = findClickableAncestor(element);
        if (ancestor != null) {
            safeClick(ancestor);
            return waitForDepartureUpdate(targetDate);
        }
        return false;
    }

    private WebElement findClickableAncestor(WebElement element) {
        try {
            return element.findElement(By.xpath("./ancestor::*[self::button or self::div or self::span][@role='button' or @role='gridcell' or @role='option' or @role='combobox' or @role='textbox' or contains(@class,'day') or contains(@class,'field')][1]"));
        } catch (Exception ignored) {
            try {
                return element.findElement(By.xpath("./ancestor::*[self::div or self::button][1]"));
            } catch (Exception ignoredAgain) {
                return null;
            }
        }
    }

    private boolean waitForDepartureUpdate(LocalDate targetDate) {
        try {
            WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            return localWait.until(webDriver -> isDepartureSetToDate(targetDate));
        } catch (TimeoutException e) {
            // Additional check after timeout
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            return isDepartureSetToDate(targetDate);
        }
    }

    private boolean isDepartureSetToDate(LocalDate targetDate) {
        String value = readDepartureText();
        if (value == null) {
            return false;
        }
        String normalized = value.toLowerCase(Locale.ENGLISH);
        String day = String.valueOf(targetDate.getDayOfMonth());
        String monthShort = targetDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toLowerCase(Locale.ENGLISH);
        String monthFull = targetDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toLowerCase(Locale.ENGLISH);
        String monthNumber = String.format("%02d", targetDate.getMonthValue());
        return normalized.contains(day) && (normalized.contains(monthShort) || normalized.contains(monthFull) || normalized.contains(monthNumber));
    }

    private String readDepartureText() {
        for (By locator : dateTriggerCandidates) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                try {
                    if (!element.isDisplayed()) {
                        continue;
                    }
                    String text = element.getText();
                    if (text != null && !text.trim().isEmpty()) {
                        return text.trim();
                    }
                    String value = element.getAttribute("value");
                    if (value != null && !value.trim().isEmpty()) {
                        return value.trim();
                    }
                    String aria = element.getAttribute("aria-label");
                    if (aria != null && !aria.trim().isEmpty()) {
                        return aria.trim();
                    }
                } catch (Exception ignored) {
                }
            }
        }
        List<WebElement> labels = driver.findElements(By.xpath("//*[normalize-space(.)='Departure' or normalize-space(.)='Depart']"));
        for (WebElement label : labels) {
            try {
                WebElement container = findClickableAncestor(label);
                if (container != null) {
                    String text = container.getText();
                    if (text != null && !text.trim().isEmpty()) {
                        return text.replace("Departure", "").replace("Depart", "").trim();
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return "";
    }

    private void logCalendarDiagnostics(LocalDate date) {
        try {
            String day = String.valueOf(date.getDayOfMonth());
            String monthShort = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            String monthFull = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            String year = String.valueOf(date.getYear());
            List<WebElement> candidates = driver.findElements(By.xpath("//*[contains(@aria-label,'" + day + "') or normalize-space()='" + day + "']"));
            int printed = 0;
            for (WebElement element : candidates) {
                try {
                    String aria = element.getAttribute("aria-label");
                    String dataDate = element.getAttribute("data-date");
                    String dataDay = element.getAttribute("data-day");
                    String dataMonth = element.getAttribute("data-month");
                    String dataYear = element.getAttribute("data-year");
                    String text = element.getText();
                    if ((aria != null && !aria.isEmpty()) || (dataDate != null && !dataDate.isEmpty()) || (text != null && !text.isEmpty())) {
                        System.out.println("Calendar candidate: text=" + text + ", aria=" + aria + ", data-date=" + dataDate
                                + ", data-day=" + dataDay + ", data-month=" + dataMonth + ", data-year=" + dataYear);
                        printed++;
                    }
                    if (printed >= 10) {
                        break;
                    }
                } catch (Exception ignored) {
                }
            }
            String source = driver.getPageSource();
            Files.createDirectories(Paths.get("target"));
            Files.write(Paths.get("target", "ixigo-calendar-source.html"), source.getBytes(StandardCharsets.UTF_8));
            System.out.println("Calendar page source saved to target/ixigo-calendar-source.html for " + monthFull + " " + year);
        } catch (Exception ignored) {
        }
    }

    private WebElement findActiveInput(Duration timeout) {
        try {
            WebDriverWait localWait = new WebDriverWait(driver, timeout);
            return localWait.until(webDriver -> {
                WebElement active = webDriver.switchTo().activeElement();
                if (active != null && isInputElement(active)) {
                    return active;
                }
                return null;
            });
        } catch (TimeoutException e) {
            return null;
        }
    }

    private WebElement resolveCityInput(String label, List<By> fieldCandidates, List<By> inputCandidates, List<String> keywords) {
        WebElement field = findFirstVisibleOrNull(fieldCandidates, Duration.ofSeconds(6));
        if (field != null) {
            try {
                // Use JavaScript to scroll the WebElement into view
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", field);
            } catch (Exception ignored) {
            }
            safeClick(field);
            WebElement active = findActiveInput(Duration.ofSeconds(3));
            if (active != null) {
                return active;
            }
        }
        
        // Try input candidates with more comprehensive search
        WebElement input = findFirstVisibleOrNull(inputCandidates, Duration.ofSeconds(6));
        if (input == null) {
            // Try additional input locators
            List<By> additionalInputLocators = Arrays.asList(
                By.cssSelector("input[placeholder*='" + label + "']"),
                By.cssSelector("input[aria-label*='" + label + "']"),
                By.cssSelector("input[name*='" + label + "']"),
                By.cssSelector("input[id*='" + label + "']"),
                By.cssSelector("input[data-testid*='" + label + "']"),
                By.xpath("//input[contains(@placeholder,'" + label + "')]"),
                By.xpath("//input[contains(@aria-label,'" + label + "')]"),
                By.xpath("//input[contains(@name,'" + label + "')]"),
                By.xpath("//input[contains(@id,'" + label + "')]"),
                By.xpath("//input[contains(@data-testid,'" + label + "')]")
            );
            
            for (By locator : additionalInputLocators) {
                input = findFirstVisibleOrNull(Arrays.asList(locator), Duration.ofSeconds(2));
                if (input != null) {
                    System.out.println("Found " + label + " input using additional locator: " + locator);
                    break;
                }
            }
        }
        
        // Last resort - try any input that might be relevant
        if (input == null) {
            List<WebElement> allInputs = driver.findElements(By.cssSelector("input[type='text'], input:not([type]), input"));
            for (WebElement inp : allInputs) {
                try {
                    if (inp.isDisplayed() && inp.isEnabled()) {
                        String placeholder = inp.getAttribute("placeholder");
                        String ariaLabel = inp.getAttribute("aria-label");
                        String name = inp.getAttribute("name");
                        String id = inp.getAttribute("id");
                        
                        if ((placeholder != null && placeholder.toLowerCase().contains(label.toLowerCase())) ||
                            (ariaLabel != null && ariaLabel.toLowerCase().contains(label.toLowerCase())) ||
                            (name != null && name.toLowerCase().contains(label.toLowerCase())) ||
                            (id != null && id.toLowerCase().contains(label.toLowerCase()))) {
                            input = inp;
                            System.out.println("Found " + label + " input by attribute matching: " + 
                                (placeholder != null ? "placeholder=" + placeholder : "") +
                                (ariaLabel != null ? " aria-label=" + ariaLabel : "") +
                                (name != null ? " name=" + name : "") +
                                (id != null ? " id=" + id : ""));
                            break;
                        }
                    }
                } catch (Exception e) {
                    // Continue trying
                }
            }
        }
        
        return input;
    }

    private WebElement findBestCityInput(String label, List<String> keywords) {
        List<WebElement> candidates = driver.findElements(By.cssSelector("input, [contenteditable='true'], [role='combobox'], [role='textbox']"));
        WebElement active = null;
        try {
            active = driver.switchTo().activeElement();
        } catch (Exception ignored) {
        }
        WebElement best = null;
        int bestScore = -1;
        for (WebElement candidate : candidates) {
            try {
                if (!candidate.isDisplayed() || !candidate.isEnabled()) {
                    continue;
                }
                String attrs = (candidate.getAttribute("placeholder") + " "
                        + candidate.getAttribute("aria-label") + " "
                        + candidate.getAttribute("name") + " "
                        + candidate.getAttribute("id") + " "
                        + candidate.getAttribute("data-testid") + " "
                        + candidate.getAttribute("class")).toLowerCase(Locale.ENGLISH);
                int score = 0;
                if (active != null && candidate.equals(active)) {
                    score += 6;
                }
                for (String keyword : keywords) {
                    if (attrs.contains(keyword)) {
                        score += 5;
                    }
                }
                if (attrs.contains("search") || attrs.contains("city") || attrs.contains("airport")) {
                    score += 2;
                }
                if ("input".equalsIgnoreCase(candidate.getTagName())) {
                    score += 1;
                }
                if (score > bestScore) {
                    bestScore = score;
                    best = candidate;
                }
            } catch (Exception ignored) {
            }
        }
        if (best != null) {
            System.out.println("Selected " + label + " input with score " + bestScore);
        }
        return best;
    }

    private List<String> buildFromKeywords() {
        return Arrays.asList("from", "origin", "source");
    }

    private List<String> buildToKeywords() {
        return Arrays.asList("to", "destination", "dest");
    }

    private List<String> buildKeywords(String label) {
        if (label != null && label.toLowerCase(Locale.ENGLISH).contains("to")) {
            return buildToKeywords();
        }
        return buildFromKeywords();
    }

    private List<By> buildFromDisplayCandidates() {
        List<By> candidates = new ArrayList<>();
        candidates.addAll(fromInputCandidates);
        candidates.addAll(fromFieldCandidates);
        candidates.addAll(originTriggerCandidates);
        return candidates;
    }

    private List<By> buildToDisplayCandidates() {
        List<By> candidates = new ArrayList<>();
        candidates.addAll(toInputCandidates);
        candidates.addAll(toFieldCandidates);
        candidates.addAll(destinationTriggerCandidates);
        return candidates;
    }

    private WebElement findDepartureTrigger() {
        WebElement trigger = findFirstVisibleOrNull(dateTriggerCandidates, Duration.ofSeconds(6));
        if (trigger != null) {
            return trigger;
        }
        
        // Try to find by label text
        WebElement label = findFirstVisibleOrNull(Arrays.asList(
                By.xpath("//*[normalize-space(.)='Departure' or normalize-space(.)='Depart']"),
                By.xpath("//*[contains(text(),'Departure') or contains(text(),'Depart')]")
        ), Duration.ofSeconds(4));
        if (label != null) {
            WebElement clickable = findClickableAncestor(label);
            if (clickable != null) {
                return clickable;
            }
        }
        
        // Try input-based triggers
        List<By> inputCandidates = Arrays.asList(
                By.cssSelector("input[placeholder*='Depart']"),
                By.cssSelector("input[placeholder*='Date']"),
                By.cssSelector("input[type='date']"),
                By.cssSelector("input[name*='date']")
        );
        trigger = findFirstVisibleOrNull(inputCandidates, Duration.ofSeconds(3));
        if (trigger != null) {
            return trigger;
        }
        
        return findFirstVisible(dateTriggerCandidates, "departure date");
    }

    private void setDisplayTextIfPossible(WebElement element, String value) {
        if (element == null || value == null) {
            return;
        }
        try {
            if ("input".equalsIgnoreCase(element.getTagName())) {
                setInputValue(element, value);
                return;
            }
        } catch (Exception ignored) {
        }
        try {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "arguments[0].setAttribute('data-automation-text', arguments[1]);"
                            + "if(arguments[0].innerText !== undefined){arguments[0].innerText = arguments[1];}"
                            + "if(arguments[0].textContent !== undefined){arguments[0].textContent = arguments[1];}",
                    element, value
            );
        } catch (Exception ignored) {
        }
    }

    private WebElement findFirstVisible(List<By> locators, String name) {
        WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            return localWait.until(webDriver -> {
                for (By locator : locators) {
                    List<WebElement> elements = webDriver.findElements(locator);
                    for (WebElement element : elements) {
                        try {
                            if (element.isDisplayed()) {
                                return element;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
                return null;
            });
        } catch (TimeoutException e) {
            logInputDiagnostics();
            throw new RuntimeException("Unable to locate " + name + " input on Ixigo", e);
        }
    }

    private WebElement findFirstVisibleOrNull(List<By> locators, Duration timeout) {
        try {
            WebDriverWait localWait = new WebDriverWait(driver, timeout);
            return localWait.until(webDriver -> {
                for (By locator : locators) {
                    List<WebElement> elements = webDriver.findElements(locator);
                    for (WebElement element : elements) {
                        try {
                            if (element.isDisplayed()) {
                                return element;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
                return null;
            });
        } catch (TimeoutException e) {
            return null;
        }
    }

    private boolean isInputElement(WebElement element) {
        if (element == null) {
            return false;
        }
        String tag = element.getTagName();
        if ("input".equalsIgnoreCase(tag) || "textarea".equalsIgnoreCase(tag)) {
            return true;
        }
        String contentEditable = element.getAttribute("contenteditable");
        return "true".equalsIgnoreCase(contentEditable);
    }

    private void logInputValue(String label, WebElement input) {
        try {
            System.out.println(label + " value: " + input.getAttribute("value"));
        } catch (Exception ignored) {
        }
    }

    private void ensureCitySelectedOrThrow(String city, List<By> displayCandidates, String label) {
        if (isCitySelected(displayCandidates, city)) {
            return;
        }
        WebElement trigger = findFirstVisibleOrNull(displayCandidates, Duration.ofSeconds(5));
        if (trigger != null) {
            safeClick(trigger);
            WebElement searchInput = findFirstVisibleOrNull(citySearchInputCandidates, Duration.ofSeconds(6));
            if (searchInput != null) {
                focusAndType(searchInput, city);
                selectSuggestion(city, searchInput);
            }
        }
        if (!isCitySelected(displayCandidates, city)) {
            throw new RuntimeException("Failed to set " + label + " on Ixigo");
        }
    }

    private boolean isCitySelected(List<By> displayCandidates, String city) {
        String displayed = readDisplayedText(displayCandidates);
        if (displayed == null || displayed.trim().isEmpty()) {
            return false;
        }
        String normalized = displayed.trim().toLowerCase(Locale.ENGLISH);
        if (normalized.equals("from") || normalized.equals("to")
                || normalized.contains("enter origin") || normalized.contains("enter destination")) {
            return false;
        }
        String cityLower = city.toLowerCase(Locale.ENGLISH);
        return normalized.contains(cityLower) || !normalized.isEmpty();
    }

    private String readDisplayedText(List<By> candidates) {
        for (By locator : candidates) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed()) {
                        String value = element.getAttribute("value");
                        if (value != null && !value.trim().isEmpty()) {
                            return value.trim();
                        }
                        String text = element.getText();
                        if (text != null && !text.trim().isEmpty()) {
                            return text.trim();
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    private void logInputDiagnostics() {
        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        System.out.println("Input elements found: " + inputs.size());
        int printed = 0;
        for (WebElement input : inputs) {
            try {
                String placeholder = input.getAttribute("placeholder");
                String aria = input.getAttribute("aria-label");
                String name = input.getAttribute("name");
                String id = input.getAttribute("id");
                String summary = "placeholder=" + placeholder + ", aria-label=" + aria
                        + ", name=" + name + ", id=" + id;
                System.out.println("Input candidate: " + summary);
                printed++;
                if (printed >= 10) {
                    break;
                }
            } catch (Exception ignored) {
            }
        }

        List<WebElement> frames = driver.findElements(By.tagName("iframe"));
        System.out.println("Iframes found: " + frames.size());

        List<WebElement> roleInputs = driver.findElements(By.cssSelector("[role='combobox'], [role='textbox'], [contenteditable='true']"));
        System.out.println("Role-based input candidates: " + roleInputs.size());
        printed = 0;
        for (WebElement element : roleInputs) {
            try {
                String role = element.getAttribute("role");
                String testId = element.getAttribute("data-testid");
                String aria = element.getAttribute("aria-label");
                String tag = element.getTagName();
                System.out.println("Role candidate: tag=" + tag + ", role=" + role + ", data-testid=" + testId + ", aria-label=" + aria);
                printed++;
                if (printed >= 10) {
                    break;
                }
            } catch (Exception ignored) {
            }
        }

        List<WebElement> dataTestIds = driver.findElements(By.cssSelector("[data-testid]"));
        printed = 0;
        for (WebElement element : dataTestIds) {
            try {
                String testId = element.getAttribute("data-testid");
                if (testId != null && !testId.isEmpty()) {
                    System.out.println("data-testid: " + testId);
                    printed++;
                }
                if (printed >= 10) {
                    break;
                }
            } catch (Exception ignored) {
            }
        }

        try {
            String source = driver.getPageSource();
            Files.createDirectories(Paths.get("target"));
            Files.write(Paths.get("target", "ixigo-page-source.html"), source.getBytes(StandardCharsets.UTF_8));
            System.out.println("Page source saved to target/ixigo-page-source.html");
        } catch (Exception ignored) {
        }
    }

    private void setInputValue(WebElement element, String value) {
        try {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1];"
                            + "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));"
                            + "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                    element, value
            );
        } catch (Exception e) {
            element.sendKeys(value);
        }
    }
}
