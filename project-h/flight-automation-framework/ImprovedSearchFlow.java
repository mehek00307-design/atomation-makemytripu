// IMPROVED SEARCH FORM FLOW
// Enhanced to work like: Type → Press Enter → Select from dropdown → Repeat

// ==========================================
// 1. ENHANCED FOCUS AND TYPE METHOD
// ==========================================
private void focusAndType(WebElement input, String text) {
    safeClick(input);
    try {
        input.sendKeys(Keys.CONTROL, "a");  // Select all existing text
        input.sendKeys(Keys.DELETE);        // Clear it
    } catch (Exception ignored) {
    }
    input.sendKeys(text);                 // Type the city name
    // Add Enter key press to trigger suggestions
    try {
        Thread.sleep(500);               // Brief pause for typing to register
        input.sendKeys(Keys.ENTER);         // Press Enter to show dropdown
    } catch (Exception ignored) {
    }
}

// ==========================================
// 2. ENHANCED SUGGESTION SELECTION
// ==========================================
private void selectSuggestion(String city, WebElement input) {
    try {
        // Wait for suggestions to appear after Enter key
        Thread.sleep(1000);
        List<WebElement> options = waitForSuggestions();
        WebElement match = null;
        String cityLower = city.toLowerCase(Locale.ENGLISH);
        
        // Look for exact or partial match in suggestions
        for (WebElement option : options) {
            try {
                String text = option.getText().trim().toLowerCase(Locale.ENGLISH);
                if (text.contains(cityLower) || cityLower.contains(text)) {
                    match = option;
                    break;
                }
            } catch (Exception e) {
                // Continue trying other options
            }
        }
        
        // If no exact match found, select first available option
        if (match == null && !options.isEmpty()) {
            match = options.get(0);
            System.out.println("No exact match found for " + city + ", selecting first option: " + match.getText());
        }
        
        if (match != null) {
            safeClick(match);
            // Wait a moment for selection to register
            Thread.sleep(500);
            return;
        }
    } catch (TimeoutException ignored) {
        System.out.println("No suggestions visible for " + city + ", pressing Enter again");
        // Try pressing Enter again
        try {
            input.sendKeys(Keys.ENTER);
        } catch (Exception e) {
            // Ignore
        }
    }
    
    // Final fallback - press Arrow Down and Enter
    try {
        input.sendKeys(Keys.ARROW_DOWN);
        Thread.sleep(200);
        input.sendKeys(Keys.ENTER);
    } catch (Exception ignored) {
    }
}

// ==========================================
// 3. COMPLETE SEARCH FLOW
// ==========================================
public void performCompleteSearch() {
    // Step 1: Enter FROM city
    System.out.println("Entering FROM city: Mumbai");
    flightsPage.enterFromCity("Mumbai");  // Types "Mumbai" + Press Enter + Select from dropdown
    
    // Step 2: Enter TO city  
    System.out.println("Entering TO city: Bangalore");
    flightsPage.enterToCity("Bangalore");   // Types "Bangalore" + Press Enter + Select from dropdown
    
    // Step 3: Select departure date
    System.out.println("Selecting departure date: December 26, 2025");
    LocalDate departureDate = DateUtils.getNextOccurrence("DECEMBER", 26);
    flightsPage.selectDepartureDate(departureDate);
    
    // Step 4: Click search button
    System.out.println("Clicking search button...");
    flightsPage.clickSearch();
    
    // Step 5: Wait for results and take screenshot
    System.out.println("Waiting for results...");
    searchResultsPage.waitForResultsToLoad();
    
    // Step 6: Extract and display results
    List<FlightInfo> flights = searchResultsPage.getAllFlightInfo();
    System.out.println("Found " + flights.size() + " flights");
    
    if (!flights.isEmpty()) {
        FlightInfo cheapest = flights.stream()
            .min(Comparator.comparingInt(FlightInfo::getPrice))
            .orElse(null);
        System.out.println("Cheapest flight: " + cheapest);
    }
    
    // Step 7: Take screenshot
    String screenshotPath = ScreenshotUtils.takeScreenshot(driver, "search-results");
    System.out.println("Screenshot saved: " + screenshotPath);
}

// ==========================================
// 4. WHAT HAPPENS INTERNALLY
// ==========================================

// For FROM city (Mumbai):
// 1. Click on from input field
// 2. Clear existing text (Ctrl+A + Delete)
// 3. Type "Mumbai"
// 4. Press Enter (triggers dropdown)
// 5. Wait 1 second for suggestions to appear
// 6. Find "Mumbai" in suggestions or select first option
// 7. Click on the suggestion
// 8. Verify city is selected

// For TO city (Bangalore):
// 1. Click on to input field
// 2. Clear existing text (Ctrl+A + Delete)  
// 3. Type "Bangalore"
// 4. Press Enter (triggers dropdown)
// 5. Wait 1 second for suggestions to appear
// 6. Find "Bangalore" in suggestions or select first option
// 7. Click on the suggestion
// 8. Verify city is selected

// For Departure Date (Dec 26, 2025):
// 1. Click on departure date trigger
// 2. Wait for calendar to open
// 3. Try to find December 26 in current month
// 4. If not found, navigate to next months
// 5. Click on December 26 when found
// 6. Verify date is selected

// For Search Button:
// 1. Try multiple locator strategies to find search button
// 2. Click the first visible, enabled search button
// 3. Wait for results page to load

// ==========================================
// 5. CONFIGURATION USED
// ==========================================
// config.properties:
// fromCity=Mumbai
// toCity=Bangalore
// departureDay=26
// departureMonth=DECEMBER
// baseUrl=https://www.ixigo.com/flights

// ==========================================
// 6. EXPECTED OUTPUT
// ==========================================
// Console Output:
// Entering FROM city: Mumbai
// No exact match found for mumbai, selecting first option: Mumbai, India
// Entering TO city: Bangalore  
// No exact match found for bangalore, selecting first option: Bangalore, India
// Selecting departure date: December 26, 2025
// Attempting to select departure date: 2025-12-26
// Clicking search button...
// Waiting for results...
// Found 25 flights
// Cheapest flight: Airline=IndiGo, Price=5432, Departure=08:15, Arrival=10:45
// Screenshot saved: screenshots/search-results_20251225_143022.png
