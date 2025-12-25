# Automation Testing iXigo - Architecture & Design

## Architecture Overview

**Automation Testing iXigo** follows a layered architecture pattern with clear separation of concerns. The framework is built on the **Page Object Model (POM)** design pattern combined with a utility-driven architecture for maximum maintainability and scalability.

```
┌─────────────────────────────────────────────────────────────┐
│                    Test Layer                               │
│              (FlightSearchTest.java)                        │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│                 Page Object Layer                           │
│   HomePage │ FlightsPage │ SearchResultsPage              │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│                   Base Layer                                │
│         BasePage │ BaseTest │ DriverManager               │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│                   Utilities Layer                           │
│   ConfigReader │ DateUtils │ ScreenshotUtils              │
└─────────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────────┐
│              Selenium WebDriver Layer                       │
│         (WebDriver interactions, element operations)       │
└─────────────────────────────────────────────────────────────┘
```

## Design Patterns

### 1. Page Object Model (POM)

**Concept**: Encapsulates page-specific elements and operations into separate classes.

**Benefits**:
- ✅ Maintainability: Locators in one place, easy to update
- ✅ Readability: Clear separation of page logic
- ✅ Reusability: Pages can be used across multiple tests
- ✅ Scalability: Easy to add new pages

**Example**:
```java
public class FlightsPage extends BasePage {
    private final By fromInput = By.id("from-city");
    
    public void enterFromCity(String city) {
        sendKeys(fromInput, city);
    }
}

// In test
flightsPage.enterFromCity("Mumbai");
```

### 2. Factory Pattern

**Usage**: `DriverManager` creates and manages WebDriver instances.

```java
public class DriverManager {
    public static WebDriver createDriver() {
        return new ChromeDriver();
    }
}
```

### 3. Singleton Pattern

**Usage**: Ensures single instance of configuration.

```java
public class ConfigReader {
    private static ConfigReader instance;
    
    public static ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }
}
```

### 4. Waiter Pattern

**Usage**: Explicit waits with timeout handling.

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.presenceOfElementLocated(locator));
```

### 5. Strategy Pattern

**Usage**: Multiple locator strategies for element finding.

```java
private final List<By> cityInputCandidates = Arrays.asList(
    By.id("city-input"),
    By.name("city-field"),
    By.cssSelector("[placeholder*='From']")
);

// Try each strategy until element found
for (By locator : cityInputCandidates) {
    if (driver.findElements(locator).size() > 0) {
        return driver.findElements(locator).get(0);
    }
}
```

## Core Components

### 1. Base Classes

#### BasePage.java
```
Purpose: Common functionality for all page objects
├── Locator strategies
├── Element interaction methods (click, sendKeys, getText)
├── Wait mechanisms (explicit waits)
├── Error handling
└── Logging
```

**Key Methods**:
- `findElement(List<By> locators)`: Finds element using multiple strategies
- `safeClick(By locator)`: Click with error handling
- `sendKeys(By locator, String text)`: Send text with retry logic
- `getText(By locator)`: Extract text safely

#### BaseTest.java
```
Purpose: Common setup/teardown for all tests
├── WebDriver initialization
├── Browser setup
├── Test data preparation
├── Screenshot on failure
└── Test teardown
```

**Lifecycle**:
```
@BeforeClass
    ↓
setUp() - Initialize WebDriver
    ↓
@BeforeMethod
    ↓
[Test Execution]
    ↓
@AfterMethod
    ↓
tearDown() - Close WebDriver
    ↓
@AfterClass
```

#### DriverManager.java
```
Purpose: WebDriver lifecycle management
├── Browser initialization
├── Driver configuration
├── Window management
└── Driver cleanup
```

### 2. Page Objects

#### HomePage.java
Represents iXigo home page:
```java
Methods:
├── openFlightsSection() → FlightsPage
├── closeLoginIfPresent() → void
└── verifyPageLoaded() → boolean
```

#### FlightsPage.java
Represents flight search form:
```java
Methods:
├── enterFromCity(String city) → void
├── enterToCity(String city) → void
├── selectDepartureDate(LocalDate date) → void
├── selectReturnDate(LocalDate date) → void
├── selectPassengerCount(int count) → void
├── clickSearch() → SearchResultsPage
└── syncSearchBarWithUrl(...) → void
```

#### SearchResultsPage.java
Represents flight results page:
```java
Methods:
├── waitForResultsToLoad() → void
├── getResultsCount() → int
├── getAllFlightInfo() → List<FlightInfo>
├── getDisplayedPricesInOrder() → List<Integer>
├── applyCheapestSort() → boolean
├── filterByPrice(int minPrice, int maxPrice) → List<FlightInfo>
└── getFlightCards() → List<WebElement>

Nested Classes:
└── FlightInfo
    ├── airline
    ├── price
    ├── departure
    ├── arrival
    └── toString()
```

### 3. Utility Classes

#### ConfigReader.java
Manages configuration properties:
```java
Methods:
├── getProperty(String key) → String
├── getIntProperty(String key, int default) → int
├── getBooleanProperty(String key, boolean default) → boolean
└── getProperty(String key, String default) → String
```

#### DateUtils.java
Date manipulation utilities:
```java
Methods:
├── getNextOccurrence(String month, int day) → LocalDate
├── formatDate(LocalDate date, String pattern) → String
├── addDays(LocalDate date, int days) → LocalDate
└── isFutureDate(LocalDate date) → boolean
```

#### ScreenshotUtils.java
Screenshot capture functionality:
```java
Methods:
├── takeScreenshot(WebDriver driver, String name) → String
├── takeScreenshot(WebDriver driver) → String
└── ensureScreenshotDir() → void
```

## Element Handling Strategy

### Dynamic Element Resolution

The framework handles dynamic web elements using a fallback strategy:

```java
private List<By> fromInputCandidates = Arrays.asList(
    By.cssSelector("input[placeholder*='From']"),  // Try first
    By.cssSelector("input[aria-label*='From']"),   // Try second
    By.cssSelector("input[id*='origin']"),         // Try third
    By.xpath("//label[contains(., 'From')]/following::input[1]")  // Try fourth
);

// Method iterates through strategies
private WebElement findFromInput() {
    for (By locator : fromInputCandidates) {
        List<WebElement> elements = driver.findElements(locator);
        if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
            return elements.get(0);
        }
    }
    throw new NoSuchElementException("From input not found");
}
```

### Stale Element Handling

Gracefully handles DOM mutations:

```java
private List<Integer> extractPricesDirectly() {
    try {
        // Extract prices from cards
    } catch (StaleElementReferenceException e) {
        System.out.println("Stale element, retrying...");
        Thread.sleep(500);
        // Retry
        return extractPricesDirectly();
    }
}
```

## Wait Strategies

### Explicit Waits (Preferred)

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
```

### Custom Wait Conditions

```java
public void waitForSortToComplete() {
    new WebDriverWait(driver, Duration.ofSeconds(30)).until(webDriver -> {
        if (!hasAnyResults(webDriver)) {
            return false;
        }
        List<Integer> prices = extractPricesDirectly();
        // Check if sorted
        for (int i = 1; i < prices.size(); i++) {
            if (prices.get(i) < prices.get(i - 1)) {
                return false;
            }
        }
        return true;
    });
}
```

## Test Execution Flow

### EndToEndFlightSearch Test

```
1. Setup Phase
   ├── Initialize WebDriver
   ├── Navigate to iXigo flights
   └── Load page

2. Precondition Phase
   ├── Close login dialog if present
   └── Verify page loaded

3. Execution Phase
   ├── Enter from city (Mumbai)
   ├── Enter to city (Bangalore)
   ├── Select departure date
   ├── Click search button
   ├── Wait for results
   └── Apply cheapest sort

4. Validation Phase
   ├── Extract flight information
   ├── Validate cheapest flight details
   ├── Validate price sorting
   ├── Filter by price range
   ├── Validate filter results
   └── Capture screenshot

5. Cleanup Phase
   ├── Close browser
   └── Generate report
```

## Error Handling Strategy

### Exception Hierarchy

```
Throwable
├── Exception
│   ├── TimeoutException
│   │   └── NoSuchElementException
│   ├── StaleElementReferenceException
│   ├── NoSuchElementException
│   └── [Custom Exceptions]
└── Error
```

### Error Handling Pattern

```java
try {
    // WebDriver operation
    element.click();
} catch (TimeoutException e) {
    // Element not found within timeout
    System.out.println("Element not found: " + e.getMessage());
    // Log and fail test
    Assert.fail("Expected element not found");
} catch (StaleElementReferenceException e) {
    // DOM changed, retry
    System.out.println("Stale element, retrying: " + e.getMessage());
    // Implement retry logic
} catch (Exception e) {
    // Unexpected error
    System.out.println("Unexpected error: " + e.getMessage());
    e.printStackTrace();
    throw e;
}
```

## Configuration Management

### Property Hierarchy

```
config.properties (source)
        ↓
ConfigReader (singleton)
        ↓
Test Classes (access via ConfigReader.getProperty)
```

### Configuration Properties

```properties
# Web Configuration
baseUrl=https://www.ixigo.com
flights_url=https://www.ixigo.com/flights

# Test Data
fromCity=Mumbai
toCity=Bangalore
fromCode=BOM
toCode=BLR
departureDay=20
departureMonth=January

# Browser Configuration
headless=false
windowSize=1920x1080

# Timeout Configuration
implicitWait=0
explicitWait=10
pageLoadWait=30

# Test Behavior
skipSearchForm=false
```

## Data Flow

### Flight Search Flow

```
User Input → Page Object → WebDriver → Web Element → Return Value
   ↓              ↓             ↓             ↓            ↓
"Mumbai" → enterFromCity → sendKeys → <input> → void
            (FlightsPage)
```

### Result Extraction Flow

```
Page → Elements → Parse → Extract → Return
 ↓       ↓         ↓        ↓        ↓
Results → Cards → Price → Integer → List<Integer>
         Text    Parse    Value
```

## Performance Considerations

### Optimization Strategies

1. **Reuse WebDriver**: Don't create new driver per test
2. **Minimal Waits**: Use explicit waits only when needed
3. **Smart Locators**: Use fast CSS selectors over XPath
4. **Parallel Execution**: TestNG supports parallel test running
5. **Screenshot on Demand**: Only take screenshots when needed

### Example - Efficient Element Finding

```java
// ❌ Slow: Creates list of all matching elements
List<WebElement> allInputs = driver.findElements(By.tagName("input"));

// ✅ Fast: Returns first matching element
WebElement firstInput = driver.findElement(By.cssSelector("input[name='city']"));
```

## Logging Architecture

### Log Levels

- **INFO**: Test flow information
- **WARN**: Non-critical issues, warnings
- **ERROR**: Test failures, exceptions
- **DEBUG**: Detailed debugging information

### Logging Points

```java
// Test start
System.out.println("Starting flight search test...");

// Interaction
System.out.println("Clicking search button...");

// Assertion
System.out.println("Validating cheapest flight: " + cheapest);

// Error
System.out.println("ERROR: " + e.getMessage());
```

## Extensibility

### Adding New Pages

```java
public class NewPage extends BasePage {
    public NewPage(WebDriver driver) {
        super(driver);
    }
    
    // Add page-specific methods
}
```

### Adding New Tests

```java
public class NewTest extends BaseTest {
    @Test
    public void newTestCase() {
        // Test implementation
    }
}
```

### Adding New Utilities

```java
public class NewUtils {
    public static String helpfulMethod(String param) {
        // Implementation
        return result;
    }
}
```

## Continuous Integration

### GitHub Actions Workflow

```yaml
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - Checkout code
      - Setup Java
      - Build project
      - Run tests
      - Upload artifacts
```

---

**Architecture Last Updated**: December 2025
**Design Principles**: SOLID, DRY, KISS
**Maintainability**: ⭐⭐⭐⭐⭐
