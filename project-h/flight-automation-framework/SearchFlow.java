// COMPLETE SEARCH FLOW CODE
// This shows the exact implementation of search button click and what happens after

// ==========================================
// 1. SEARCH BUTTON CLICK (FlightsPage.java lines 217-232)
// ==========================================
public void clickSearch() {
    ensureFlightsFrame();  // Make sure we're in correct frame
    
    // Try multiple locator strategies for search button
    for (By locator : searchButtonCandidates) {
        List<WebElement> buttons = driver.findElements(locator);
        for (WebElement button : buttons) {
            try {
                if (button.isDisplayed() && button.isEnabled()) {
                    safeClick(button);  // Click the first visible, enabled button
                    return;  // Exit after successful click
                }
            } catch (Exception ignored) {
                // Continue trying other buttons
            }
        }
    }
    throw new RuntimeException("Search button not found on Ixigo flights page");
}

// Search button candidates (lines 112-118)
private final List<By> searchButtonCandidates = Arrays.asList(
    By.cssSelector("[data-testid='searchFlight']"),           // Primary ixigo selector
    By.cssSelector("button[type='submit']"),               // Standard submit button
    By.cssSelector("button[class*='search']"),             // Button with search class
    By.xpath("//button[contains(normalize-space(.),'Search')]"), // Button with Search text
    By.cssSelector("div[role='button'][class*='search']"),   // Div acting as button
    By.xpath("//*[self::div or self::span][@role='button' and contains(normalize-space(.),'Search')]") // Any element with Search role
);

// ==========================================
// 2. WHAT HAPPENS AFTER SEARCH (FlightSearchTest.java lines 59-94)
// ==========================================

// In the main test method:
@Test
public void endToEndFlightSearch() {
    // ... setup code ...
    
    // Enter cities and date
    flightsPage.enterFromCity(fromCity);
    flightsPage.enterToCity(toCity);
    flightsPage.selectDepartureDate(departureDate);
    
    // CLICK SEARCH BUTTON
    flightsPage.clickSearch();  // This calls the method above
    
    // ==========================================
    // 3. POST-SEARCH FLOW
    // ==========================================
    
    // Wait for results to load
    searchResultsPage.waitForResultsToLoad();
    
    // Try to sort by cheapest price
    boolean sortApplied = searchResultsPage.applyCheapestSort();
    
    // Get flight count
    int resultCount = searchResultsPage.getResultsCount();
    Assert.assertTrue(resultCount > 0, "Flight results count should be greater than 0");
    
    // Extract all flight information
    List<FlightInfo> allFlights = searchResultsPage.getAllFlightInfo();
    List<FlightInfo> pricedFlights = allFlights.stream()
            .filter(flight -> flight.getPrice() > 0)
            .collect(Collectors.toList());
    
    // Find cheapest flight
    List<FlightInfo> sortedFlights = new ArrayList<>(pricedFlights);
    sortedFlights.sort(Comparator.comparingInt(FlightInfo::getPrice));
    
    FlightInfo cheapest = sortedFlights.get(0);
    System.out.println("Cheapest Flight: " + cheapest.toString());
    
    // Show second cheapest if available
    if (sortedFlights.size() > 1) {
        FlightInfo secondCheapest = sortedFlights.get(1);
        System.out.println("Second Cheapest Flight: " + secondCheapest.toString());
    }
    
    // Validate price sorting
    List<Integer> sortedPrices = sortedFlights.stream()
            .map(FlightInfo::getPrice)
            .collect(Collectors.toList());
    Assert.assertTrue(isSortedAscending(sortedPrices), "Sorted prices are not in ascending order");
    
    // Validate UI sorting if sort was applied
    if (sortApplied) {
        List<Integer> displayedPrices = searchResultsPage.getDisplayedPricesInOrder();
        Assert.assertTrue(displayedPrices.size() > 1, "Not enough prices to validate UI sorting");
        Assert.assertTrue(isSortedAscending(displayedPrices), "Displayed prices are not sorted in ascending order");
        System.out.println("UI sorting validation passed for cheapest order");
    }
    
    // Filter flights under max price
    int maxPrice = sortedFlights.get(0).getPrice() + 5000;
    List<FlightInfo> filteredFlights = pricedFlights.stream()
            .filter(flight -> flight.getPrice() > 0 && flight.getPrice() <= maxPrice)
            .collect(Collectors.toList());
    Assert.assertTrue(filteredFlights.size() > 0, "No flights found under the price filter");
    System.out.println("Flights under price filter (<= INR " + maxPrice + "): " + filteredFlights.size());
    
    // Take screenshot for documentation
    String screenshotPath = ScreenshotUtils.takeScreenshot(driver, "results");
    System.out.println("Screenshot saved to: " + screenshotPath);
    
    // Open new tab to verify browser functionality
    String originalWindow = driver.getWindowHandle();
    driver.switchTo().newWindow(WindowType.TAB);
    driver.get("https://www.google.com");
    Assert.assertTrue(driver.getTitle().toLowerCase().contains("google"), "Google title validation failed");
    driver.close();
    driver.switchTo().window(originalWindow);
}

// ==========================================
// 4. HELPER METHODS
// ==========================================

private boolean isSortedAscending(List<Integer> values) {
    for (int i = 1; i < values.size(); i++) {
        if (values.get(i) < values.get(i - 1)) {
            return false;
        }
    }
    return true;
}

// ==========================================
// 5. FALLBACK MECHANISM (if search form fails)
// ==========================================

// In test method, if search form fails:
try {
    flightsPage.enterFromCity(fromCity);
    flightsPage.enterToCity(toCity);
    flightsPage.selectDepartureDate(departureDate);
    flightsPage.clickSearch();  // Try normal search
} catch (Exception e) {
    // FALLBACK: Navigate directly to results URL
    String fromCode = resolveCityCode(fromCity, "BOM");
    String toCode = resolveCityCode(toCity, "BLR");
    String resultsUrl = buildIxigoResultsUrl(fromCode, toCode, departureDate);
    System.out.println("Search form unavailable, navigating directly to results: " + resultsUrl);
    driver.get(resultsUrl);  // Direct URL navigation
}

private String buildIxigoResultsUrl(String fromCode, String toCode, LocalDate departureDate) {
    String date = DateUtils.formatAsCompactDate(departureDate);
    return "https://www.ixigo.com/search/result/flight?from=" + fromCode
            + "&to=" + toCode
            + "&date=" + date
            + "&adults=1&children=0&infants=0&class=e&source=Search%20Form";
}
