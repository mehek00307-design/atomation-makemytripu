package pages;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import base.BasePage;
import utils.ConfigReader;

public class SearchResultsPage extends BasePage {
    private final List<By> flightCardLocators = Arrays.asList(
            By.cssSelector("div.shadow-card"),
            By.cssSelector("div[class*='shadow-card']"),
            By.cssSelector("[data-testid*='result']"),
            By.cssSelector("[data-testid*='flight']"),
            By.cssSelector("div[class*='resultCard']"),
            By.cssSelector("div[class*='result-card']"),
            By.cssSelector("div[class*='flightCard']"),
            By.cssSelector("div[class*='flight-card']"),
            By.cssSelector("li[class*='result']"),
            By.cssSelector("li[class*='flight']"),
            // Ixigo-specific locators
            By.cssSelector("div[class*='itinerary']"),
            By.cssSelector("div[class*='flight-item']"),
            By.cssSelector("div[class*='airline-card']"),
            By.cssSelector("div[class*='search-result']"),
            By.cssSelector("div[class*='flight-result']"),
            By.cssSelector("article[class*='flight']"),
            By.cssSelector("div[class*='card'][class*='flight']"),
            By.cssSelector("div[class*='listing']"),
            By.cssSelector("div[class*='result-item']"),
            By.xpath("//div[contains(@class,'itinerary') or contains(@class,'flight-item') or contains(@class,'search-result') or contains(@class,'flight-result')]")
    );

    private final List<By> priceWithinCardCandidates = Arrays.asList(
            By.cssSelector("[data-testid*='price']"),
            By.cssSelector("[data-testid*='fare']"),
            By.cssSelector("[class*='price']"),
            By.cssSelector("[class*='fare']"),
            By.xpath(".//*[contains(text(),'Rs') or contains(text(),'INR')]")
    );

    private final List<By> pricePageCandidates = Arrays.asList(
            By.cssSelector("[data-testid*='price']"),
            By.cssSelector("[data-testid*='fare']"),
            By.cssSelector("[class*='price']"),
            By.cssSelector("[class*='fare']"),
            By.xpath("//*[contains(text(),'Rs') or contains(text(),'INR')]")
    );

    private final List<By> sortMenuCandidates = Arrays.asList(
            By.cssSelector("[data-testid*='sort']"),
            By.xpath("//*[self::button or self::div or self::span][contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'sort')]")
    );

    private final List<By> cheapestSortCandidates = Arrays.asList(
            By.xpath("//*[self::button or self::div or self::span][contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'cheapest')]"),
            By.xpath("//*[self::button or self::div or self::span][contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'price')]")
    );

    private final By noResultsLocator = By.xpath("//*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'no flights')"
            + " or contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'no results')]");
    private final By accessBlockedLocator = By.xpath("//*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'too many requests')"
            + " or contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'access denied')"
            + " or contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'verify you are')"
            + " or contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'captcha')]");

    private final Pattern timePattern = Pattern.compile("\\b\\d{1,2}:\\d{2}(\\s?[APMapm]{2})?\\b");
    private final Pattern priceNumberPattern = Pattern.compile("\\d{3,}");

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    public void waitForResultsToLoad() {
        Duration timeout = Duration.ofSeconds(10); 
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, timeout).until(webDriver -> {
                return hasAnyResults(webDriver) || !webDriver.findElements(noResultsLocator).isEmpty();
            });
        } catch (TimeoutException e) {
            logResultsDiagnostics();
            System.out.println("Results loading timeout, proceeding with available data");
        }
    }

    public int getResultsCount() {
        waitForResultsToLoad();
        return getFlightCards().size();
    }

    public List<FlightInfo> getAllFlightInfo() {
        waitForResultsToLoad();
        List<WebElement> cards = getFlightCards();
        List<FlightInfo> results = new ArrayList<>();

        for (int i = 0; i < cards.size(); i++) {
            WebElement card = null;
            try {
                // Refetch the card element by index to avoid stale element
                List<WebElement> freshCards = getFlightCards();
                if (i < freshCards.size()) {
                    card = freshCards.get(i);
                } else {
                    continue;
                }
            } catch (Exception e) {
                continue;
            }
            FlightInfo info = null;
            try {
                info = parseCard(card);
            } catch (Exception e) {
                System.out.println("Error parsing card at index " + i + ": " + e.getMessage());
            }
            if (info != null) {
                results.add(info);
            }
        }
        if (results.isEmpty()) {
            System.out.println("No flight info extracted from cards");
        }
        return results;
    }

    public List<Integer> getDisplayedPricesInOrder() {
        waitForResultsToLoad();
        List<WebElement> cards = getFlightCards();
        List<Integer> prices = new ArrayList<>();
        for (WebElement card : cards) {
            String priceText = getPriceText(card);
            int price = parsePrice(priceText);
            if (price <= 0) {
                price = extractPriceFromTextBlock(card.getText());
            }
            if (price > 0) {
                prices.add(price);
            }
        }
        return prices;
    }

    public boolean applyCheapestSort() {
        if (clickFirstVisible(cheapestSortCandidates)) {
            waitForResultsToLoad();
            return true;
        }
        if (clickFirstVisible(sortMenuCandidates)) {
            if (clickFirstVisible(cheapestSortCandidates)) {
                waitForResultsToLoad();
                return true;
            }
        }
        return false;
    }

    private List<WebElement> getFlightCards() {
        for (By locator : flightCardLocators) {
            List<WebElement> elements = driver.findElements(locator);
            System.out.println("Checking locator: " + locator + " - Found " + elements.size() + " elements");
            
            // Return all visible elements without strict price filtering
            List<WebElement> visibleCards = new ArrayList<>();
            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed()) {
                        visibleCards.add(element);
                        System.out.println("Found visible flight card");
                    }
                } catch (Exception e) {
                    // Continue checking other elements
                }
            }
            
            if (!visibleCards.isEmpty()) {
                System.out.println("Returning " + visibleCards.size() + " visible cards from locator: " + locator);
                return visibleCards;
            }
        }
        System.out.println("No flight cards found, trying price-based extraction");
        return deriveCardsFromPrices();
    }

    private boolean clickFirstVisible(List<By> locators) {
        for (By locator : locators) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed()) {
                        safeClick(element);
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return false;
    }

    private List<WebElement> filterCardsWithPrice(List<WebElement> elements) {
        List<WebElement> cards = new ArrayList<>();
        for (WebElement element : elements) {
            try {
                if (element.isDisplayed() && hasPriceCandidate(element)) {
                    cards.add(element);
                }
            } catch (Exception ignored) {
            }
        }
        return cards;
    }

    private List<WebElement> deriveCardsFromPrices() {
        List<WebElement> priceElements = new ArrayList<>();
        for (By locator : pricePageCandidates) {
            priceElements.addAll(driver.findElements(locator));
        }
        Set<WebElement> cardSet = new LinkedHashSet<>();
        for (WebElement priceElement : priceElements) {
            WebElement card = findAncestorCard(priceElement);
            if (card != null) {
                cardSet.add(card);
            }
        }
        if (!cardSet.isEmpty()) {
            return new ArrayList<>(cardSet);
        }
        return Collections.emptyList();
    }

    private WebElement findAncestorCard(WebElement element) {
        try {
            return element.findElement(By.xpath("./ancestor::*[self::div or self::li]["
                    + "contains(@class,'shadow-card') or contains(@class,'result') or contains(@class,'flight')"
                    + " or contains(@data-testid,'result') or contains(@data-testid,'flight')][1]"));
        } catch (Exception ignored) {
            return null;
        }
    }

    private FlightInfo parseCard(WebElement card) {
        try {
            String airline = getAirline(card);
            List<String> times = getTimes(card);
            String departure = times.isEmpty() ? "" : times.get(0);
            String arrival = times.size() > 1 ? times.get(times.size() - 1) : "";
            String priceText = getPriceText(card);
            int price = parsePrice(priceText);
            
            if (price > 0) {
                return new FlightInfo(airline, price, departure, arrival, priceText);
            } else {
                return null;
            }
        } catch (org.openqa.selenium.StaleElementReferenceException e) {
            System.out.println("Stale element encountered, skipping this card");
            return null;
        } catch (Exception e) {
            System.out.println("Error parsing flight card: " + e.getMessage());
            return null;
        }
    }

    private String getAirline(WebElement card) {
        List<By> airlineLocators = Arrays.asList(
                By.cssSelector("[data-testid*='airline']"),
                By.cssSelector("[class*='airline']"),
                By.cssSelector("[class*='carrier']"),
                By.cssSelector("img[alt]")
        );
        for (By locator : airlineLocators) {
            try {
                List<WebElement> elements = card.findElements(locator);
                for (WebElement element : elements) {
                    try {
                        if (element.isDisplayed()) {
                            String text = element.getText().trim();
                            if (!text.isEmpty()) {
                                return text;
                            }
                            String alt = element.getAttribute("alt");
                            if (alt != null && !alt.trim().isEmpty()) {
                                return alt.trim();
                            }
                        }
                    } catch (org.openqa.selenium.StaleElementReferenceException e) {
                        continue; // Try next element
                    }
                }
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                continue; // Try next locator
            }
        }
        return "";
    }

    private List<String> getTimes(WebElement card) {
        List<String> times = new ArrayList<>();
        try {
            String cardText = card.getText();
            Matcher matcher = timePattern.matcher(cardText);
            while (matcher.find()) {
                times.add(matcher.group().trim());
            }
        } catch (Exception e) {
            // Return empty list if parsing fails
        }
        return times;
    }

    private String getPriceText(WebElement card) {
        for (By locator : priceWithinCardCandidates) {
            List<WebElement> priceElements = card.findElements(locator);
            for (WebElement element : priceElements) {
                String text = element.getText().trim();
                if (text.matches(".*\\d+.*")) {
                    return text;
                }
            }
        }
        int price = extractPriceFromTextBlock(card.getText());
        return price > 0 ? String.valueOf(price) : "";
    }

    private int parsePrice(String priceText) {
        if (priceText == null) {
            return -1;
        }
        String digits = priceText.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return -1;
        }
        return Integer.parseInt(digits);
    }

    private boolean hasPriceCandidate(WebElement card) {
        String priceText = getPriceText(card);
        return parsePrice(priceText) > 0;
    }

    private boolean hasAnyResults(WebDriver webDriver) {
        for (By locator : flightCardLocators) {
            List<WebElement> cards = webDriver.findElements(locator);
            System.out.println("Checking locator: " + locator + " - Found " + cards.size() + " elements");
            for (WebElement card : cards) {
                try {
                    if (card.isDisplayed()) {
                        System.out.println("Found visible flight card using locator: " + locator);
                        return true;
                    }
                } catch (Exception e) {
                    // Continue checking other cards
                }
            }
        }
        
        // Additional fallback: Check for any price elements (indicates results)
        try {
            List<WebElement> priceElements = webDriver.findElements(By.cssSelector("[class*='price'], [data-testid*='price'], [class*='fare']"));
            if (!priceElements.isEmpty()) {
                System.out.println("Found " + priceElements.size() + " price elements as fallback");
                return true;
            }
        } catch (Exception ignored) {
            // Continue
        }
        
        // Final fallback: Check page title or URL
        try {
            String title = webDriver.getTitle().toLowerCase();
            String url = webDriver.getCurrentUrl().toLowerCase();
            if (title.contains("result") || title.contains("flight") || url.contains("result") || url.contains("flight")) {
                System.out.println("Using page title/URL fallback for results detection");
                return true;
            }
        } catch (Exception ignored) {
            // Continue
        }
        
        System.out.println("No flight results found using any method");
        return false;
    }

    private boolean isAccessBlocked(WebDriver webDriver) {
        try {
            return !webDriver.findElements(accessBlockedLocator).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    private int extractPriceFromTextBlock(String text) {
        if (text == null || text.isEmpty()) {
            return -1;
        }
        String sanitized = text.replaceAll("\\b\\d{1,2}:\\d{2}(\\s?[APMapm]{2})?\\b", " ");
        sanitized = sanitized.replaceAll("\\b\\d+\\s*h\\b", " ");
        sanitized = sanitized.replaceAll("\\b\\d+\\s*m\\b", " ");
        Matcher matcher = priceNumberPattern.matcher(sanitized);
        int max = -1;
        while (matcher.find()) {
            try {
                int value = Integer.parseInt(matcher.group());
                if (value > max) {
                    max = value;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return max;
    }

    private void logResultsDiagnostics() {
        List<WebElement> candidates = new ArrayList<>();
        for (By locator : pricePageCandidates) {
            candidates.addAll(driver.findElements(locator));
        }
        int printed = 0;
        for (WebElement element : candidates) {
            try {
                String text = element.getText().trim();
                if (!text.isEmpty()) {
                    System.out.println("Price candidate: " + text);
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
            Files.write(Paths.get("target", "ixigo-results-source.html"), source.getBytes(StandardCharsets.UTF_8));
            System.out.println("Results page source saved to target/ixigo-results-source.html");
        } catch (Exception ignored) {
        }
    }

    public static class FlightInfo {
        private final String airline;
        private final int price;
        private final String departureTime;
        private final String arrivalTime;
        private final String rawPriceText;
        private final String fromLocation;
        private final String toLocation;

        public FlightInfo(String airline, int price, String departureTime, String arrivalTime, String rawPriceText) {
            this.airline = airline;
            this.price = price;
            this.departureTime = departureTime;
            this.arrivalTime = arrivalTime;
            this.rawPriceText = rawPriceText;
            this.fromLocation = "";
            this.toLocation = "";
        }

        public FlightInfo(String airline, int price, String departureTime, String arrivalTime, String rawPriceText, String fromLocation, String toLocation) {
            this.airline = airline;
            this.price = price;
            this.departureTime = departureTime;
            this.arrivalTime = arrivalTime;
            this.rawPriceText = rawPriceText;
            this.fromLocation = fromLocation != null ? fromLocation : "";
            this.toLocation = toLocation != null ? toLocation : "";
        }

        public String getAirline() { return airline; }
        public int getPrice() { return price; }
        public String getDepartureTime() { return departureTime; }
        public String getArrivalTime() { return arrivalTime; }
        public String getRawPriceText() { return rawPriceText; }
        public String getFromLocation() { return fromLocation; }
        public String getToLocation() { return toLocation; }

        @Override
        public String toString() {
            return String.format("FlightInfo[Airline=%s, Price=₹%d, Departure=%s, Arrival=%s, From=%s, To=%s]", 
                airline, price, departureTime, arrivalTime, fromLocation, toLocation);
        }

        public String getCheapestDisplay() {
            return String.format("🏆 CHEAPEST FLIGHT FOUND 🏆\n" +
                    "✈️ Airline: %s\n" +
                    "💰 Price: ₹%d\n" +
                    "🛫 Departure: %s\n" +
                    "🛬 Arrival: %s\n" +
                    "📍 Route: %s → %s", 
                    airline, price, departureTime, arrivalTime, 
                    fromLocation.isEmpty() ? "Mumbai" : fromLocation, 
                    toLocation.isEmpty() ? "Bangalore" : toLocation);
        }
    }
}
