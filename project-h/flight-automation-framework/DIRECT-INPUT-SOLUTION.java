// SOLUTION: Direct Input Without Suggestions
// This bypasses suggestion detection entirely

public void enterCityDirectly(String city, WebElement input) {
    try {
        // Clear and type city name
        input.clear();
        input.sendKeys(city);
        
        // Approach 1: Wait and press Enter twice
        Thread.sleep(1000);
        input.sendKeys(Keys.ENTER);
        Thread.sleep(500);
        input.sendKeys(Keys.ENTER);
        
        // Approach 2: If still not selected, try JavaScript
        if (!isCitySelected(city)) {
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];" +
                "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));" +
                "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));" +
                "arguments[0].blur();",
                input, city
            );
        }
        
        // Approach 3: If still not selected, try Tab+Enter
        if (!isCitySelected(city)) {
            input.sendKeys(Keys.TAB);
            Thread.sleep(200);
            input.sendKeys(Keys.ENTER);
        }
        
        System.out.println("Direct input completed for: " + city);
        
    } catch (Exception e) {
        System.out.println("Direct input failed for " + city + ": " + e.getMessage());
    }
}

// Modified enterFromCity:
public void enterFromCity(String fromCity) {
    dismissCommonOverlays();
    ensureFlightsFrame();
    
    WebElement input = resolveCityInput("from city", fromFieldCandidates, fromInputCandidates, buildFromKeywords());
    if (input != null) {
        enterCityDirectly(fromCity, input);
        ensureCitySelectedOrThrow(fromCity, buildFromDisplayCandidates(), "from city");
    } else {
        throw new RuntimeException("Unable to locate from city input on Ixigo");
    }
}

// Modified enterToCity:
public void enterToCity(String toCity) {
    dismissCommonOverlays();
    ensureFlightsFrame();
    
    WebElement input = resolveCityInput("to city", toFieldCandidates, toInputCandidates, buildToKeywords());
    if (input != null) {
        enterCityDirectly(toCity, input);
        ensureCitySelectedOrThrow(toCity, buildToDisplayCandidates(), "to city");
    } else {
        throw new RuntimeException("Unable to locate to city input on Ixigo");
    }
}
