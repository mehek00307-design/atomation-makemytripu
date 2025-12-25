# Automation Testing iXigo - Flight Search Automation Framework

## 📋 Project Overview

**Automation Testing iXigo** is a comprehensive, production-grade Selenium-based automation testing framework designed specifically for validating flight search functionality on the iXigo travel platform. This framework provides end-to-end test automation for flight search, filtering, sorting, and results validation using industry best practices.

The framework follows the **Page Object Model (POM)** design pattern, ensuring maintainability, scalability, and code reusability. It is built with Java, Maven, Selenium WebDriver, and TestNG, making it suitable for continuous integration and deployment pipelines.

### 🎯 Key Features

- **Robust Flight Search Automation**: Automates complete flight search workflows including city selection, date picking, and results validation
- **Dynamic Element Handling**: Intelligently handles dynamic web elements with multiple fallback locator strategies
- **Price Sorting & Filtering**: Validates flight price sorting in ascending order and filters results by price range
- **Screenshot Capture**: Automatically captures screenshots of test results for evidence and debugging
- **Configurable Test Parameters**: Externalized configuration for cities, dates, and test behaviors
- **Comprehensive Logging**: Detailed logging at each step for easy troubleshooting
- **Stale Element Resilience**: Graceful handling of stale element references during DOM mutations
- **Cross-Browser Support**: Configured for Chrome with extensibility for other browsers
- **CI/CD Ready**: Includes Maven configuration and GitHub Actions workflow for automated test execution

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Java | 11+ |
| **Build Tool** | Maven | 3.6+ |
| **WebDriver** | Selenium WebDriver | 4.18.1 |
| **Test Framework** | TestNG | 7.9.0 |
| **Driver Management** | WebDriverManager | 5.7.0 |
| **Target Website** | iXigo Flights | Live |
| **Browser** | Google Chrome | Latest |

## 📁 Project Structure

```
flight-automation-framework/
├── src/
│   ├── test/
│   │   ├── java/
│   │   │   ├── base/
│   │   │   │   ├── BasePage.java          # Base class for all page objects
│   │   │   │   ├── BaseTest.java          # Base class for all test classes
│   │   │   │   └── DriverManager.java     # WebDriver initialization and management
│   │   │   ├── pages/
│   │   │   │   ├── HomePage.java          # iXigo home page object
│   │   │   │   ├── FlightsPage.java       # Flight search page object
│   │   │   │   └── SearchResultsPage.java # Flight results page object
│   │   │   ├── tests/
│   │   │   │   └── FlightSearchTest.java  # Main test class with end-to-end flight search test
│   │   │   └── utils/
│   │   │       ├── ConfigReader.java      # Configuration property reader
│   │   │       ├── DateUtils.java         # Date utilities for test data
│   │   │       └── ScreenshotUtils.java   # Screenshot capture functionality
│   │   └── resources/
│   │       ├── config.properties          # Test configuration (cities, dates, URLs)
│   │       └── log4j.properties           # Logging configuration
├── .github/
│   └── workflows/
│       └── automation-tests.yml           # GitHub Actions CI/CD workflow
├── target/                                 # Maven build output
├── pom.xml                                 # Maven project configuration
├── testng.xml                             # TestNG suite configuration
└── README.md                              # This file
```

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: Version 11 or higher
- **Maven**: Version 3.6 or higher
- **Google Chrome**: Latest stable version
- **Git**: For cloning the repository (optional)

### Installation Steps

1. **Clone or Download the Repository**
   ```bash
   git clone <repository-url>
   cd flight-automation-framework
   ```

2. **Install Java and Maven** (if not already installed)
   ```bash
   # Verify Java installation
   java -version
   
   # Verify Maven installation
   mvn -version
   ```

3. **Ensure Google Chrome is Installed**
   - Download from: https://www.google.com/chrome/
   - WebDriverManager will automatically manage the ChromeDriver

4. **Update Configuration (Optional)**
   - Edit `src/test/resources/config.properties`
   - Customize test parameters as needed:
     ```properties
     fromCity=Mumbai
     toCity=Bangalore
     departureDay=20
     departureMonth=January
     ```

## ▶️ Running Tests

### Using Maven (Recommended)

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn clean test -Dtest=FlightSearchTest

# Run specific test method
mvn clean test -Dtest=FlightSearchTest#endToEndFlightSearch

# Run with batch/quiet mode
mvn clean test -B

# Generate HTML test report
mvn clean test site
```

### Using TestNG XML

```bash
# Run from IDE or command line with testng.xml
# Make sure testng.xml is configured correctly in your IDE
```

### Using IDE

1. Right-click on `testng.xml` → Select "Run"
2. Or right-click on test class → "Run As" → "TestNG Class"

## 📊 Test Execution Output

### Console Output Example

```
[INFO] Running tests.FlightSearchTest
Ensuring flights frame, URL: https://www.ixigo.com/flights
Building URL with date: 2026-01-26 -> 26012026
Search form unavailable, navigating directly to results: https://www.ixigo.com/search/result/flight?from=BOM&to=BLR...
Attempting to apply cheapest sort...
Prices BEFORE sort: [5322, 899, 591]
Successfully clicked cheapest sort option
Prices AFTER sort: [591, 899, 5322]
Sort DID change the order of results
=========================================
≡ CHEAPEST FLIGHT FOUND ≡
Airline: 6E
Price: ₹591
Departure: 02:45
Arrival: 08:35
Route: Mumbai → Bangalore
=========================================
Cheapest Flight Details: FlightInfo[Airline=6E, Price=₹591, Departure=02:45, Arrival=08:35, From=, To=]
Second Cheapest Flight: FlightInfo[Airline=SG, Price=₹899, Departure=01:40, Arrival=03:40, From=, To=]
✓ UI sorting validation passed for cheapest order
Flights under price filter (<= INR 6591): 3
Screenshot saved to: target/screenshots/results_20250101_120000.png
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## 🧪 Test Case Details

### End-to-End Flight Search Test (`endToEndFlightSearch`)

**Test Flow:**
1. ✅ Navigate to iXigo flights page
2. ✅ Close login dialog if present
3. ✅ Enter departure city (Mumbai)
4. ✅ Enter destination city (Bangalore)
5. ✅ Select departure date (configurable)
6. ✅ Click search button
7. ✅ Validate flight results are loaded
8. ✅ Apply "cheapest" sort filter
9. ✅ Extract and validate flight information:
   - Validate prices are in ascending order
   - Extract cheapest flight details
   - Display airline, price, departure, arrival times
10. ✅ Filter flights by price range
11. ✅ Capture screenshot of results
12. ✅ Validate page navigation to Google

**Expected Results:**
- Flight results are loaded successfully
- Prices are sorted in ascending order after applying sort
- Cheapest flight details are extracted correctly
- Multiple price-filtered results are available
- Screenshot is saved successfully

## ⚙️ Configuration

### config.properties

Located in `src/test/resources/config.properties`, contains:

```properties
# Flight Search Configuration
fromCity=Mumbai
toCity=Bangalore
fromCode=BOM
toCode=BLR
departureDay=20
departureMonth=January

# Test Behavior
skipSearchForm=false
headless=false
```

### Key Configuration Options

| Property | Description | Example |
|----------|-------------|---------|
| `fromCity` | Source city for flight search | Mumbai, Delhi, Bangalore |
| `toCity` | Destination city for flight search | Bangalore, Delhi, Goa |
| `fromCode` | IATA airport code for source | BOM (Mumbai), DEL (Delhi) |
| `toCode` | IATA airport code for destination | BLR (Bangalore), CCU (Kolkata) |
| `departureDay` | Departure date (day of month) | 20, 15, 25 |
| `departureMonth` | Departure date (month) | January, February, March |
| `skipSearchForm` | Skip form and navigate directly to results | true/false |
| `headless` | Run browser in headless mode | true/false |

## 📸 Screenshots

Screenshots are automatically captured during test execution and saved to:
- **Location**: `target/screenshots/`
- **Naming**: `results_YYYYMMDD_HHMMSS.png`
- **Purpose**: Evidence of test results and debugging

## 🔍 Key Classes and Methods

### Base Classes

**BasePage.java**
- Base class for all page objects
- Common methods: `safeClick()`, `sendKeys()`, `getText()`, `waitFor()`
- Element interaction utilities

**BaseTest.java**
- Base class for all test classes
- WebDriver initialization and teardown
- Common test utilities

### Page Objects

**HomePage.java**
- Models the iXigo home page
- Methods: `openFlightsSection()`, `closeLoginIfPresent()`

**FlightsPage.java**
- Models the flight search page
- Methods: `enterFromCity()`, `enterToCity()`, `selectDepartureDate()`, `clickSearch()`
- Handles dynamic form elements with fallback locators

**SearchResultsPage.java**
- Models the flight results page
- Methods: `getFlightCards()`, `applyCheapestSort()`, `getDisplayedPricesInOrder()`, `getAllFlightInfo()`
- Price extraction and sorting logic

### Utility Classes

**ConfigReader.java**
- Reads configuration from `config.properties`
- Methods: `getProperty()`, `getIntProperty()`, `getBooleanProperty()`

**DateUtils.java**
- Date manipulation and formatting
- Methods: `getNextOccurrence()`, `formatDate()`

**ScreenshotUtils.java**
- Screenshot capture functionality
- Methods: `takeScreenshot()`

## 🐛 Troubleshooting

### Issue: "Chrome Driver not found"
**Solution**: WebDriverManager should automatically download it. Ensure internet connectivity.

### Issue: "Stale Element Reference Exception"
**Solution**: The framework gracefully handles stale elements. Check if the page structure has changed.

### Issue: "Element not found"
**Solution**: 
- Verify the website structure hasn't changed
- Check browser console for JavaScript errors
- Update locators in page objects if needed

### Issue: "Tests timing out"
**Solution**:
- Increase wait times in `BasePage.java` for slow connections
- Check if the website is accessible
- Verify network connectivity

## 📈 Test Reports

### Maven Reports

After running tests, generate reports using:

```bash
mvn site
```

Reports are generated in: `target/site/`

### TestNG Reports

TestNG generates reports in: `test-output/` directory

## 🔄 CI/CD Integration

### GitHub Actions Workflow

The project includes a GitHub Actions workflow (`.github/workflows/automation-tests.yml`) that:
- ✅ Triggers on every push and pull request
- ✅ Sets up Java and Maven
- ✅ Runs all automation tests
- ✅ Generates test reports
- ✅ Uploads results as artifacts

### Setting Up CI/CD

1. Push code to GitHub repository
2. GitHub Actions automatically triggers test workflow
3. View test results in "Actions" tab
4. Download test reports and screenshots from artifacts

## 🤝 Contributing

To contribute improvements:

1. Create a feature branch
2. Make your changes
3. Ensure all tests pass: `mvn clean test`
4. Commit with descriptive messages
5. Push and create a Pull Request

## 📝 Best Practices Used

- ✅ **Page Object Model**: Separates test logic from page interactions
- ✅ **Explicit Waits**: No hard sleeps; uses Selenium WebDriverWait
- ✅ **Fallback Locators**: Multiple strategies for finding elements
- ✅ **Stale Element Handling**: Graceful retry logic for dynamic pages
- ✅ **Configuration Externalization**: Easy test parameter customization
- ✅ **Screenshot Evidence**: Automatic capture for test validation
- ✅ **Comprehensive Logging**: Detailed output for debugging
- ✅ **Error Handling**: Proper exception handling and logging
- ✅ **Code Reusability**: Common utilities and base classes
- ✅ **Continuous Integration**: GitHub Actions workflow included

## 📄 License

This project is provided as-is for automation testing purposes.

## 📧 Contact & Support

For issues, questions, or improvements, please open an issue in the repository or contact the development team.

## 🎓 Learning Resources

- [Selenium Documentation](https://www.selenium.dev/)
- [TestNG Documentation](https://testng.org/)
- [Maven Documentation](https://maven.apache.org/)
- [Page Object Model Pattern](https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/)

---

**Last Updated**: December 2025  
**Status**: ✅ Active & Maintained  
**Maintenance**: Regular updates and improvements ongoing
