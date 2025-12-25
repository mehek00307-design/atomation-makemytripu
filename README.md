# atomation-makemytripu
atomation-makemytripu

# Flight Automation Framework

## Project Overview
End-to-end Selenium automation framework for flight search validation using Java, Maven, TestNG, and Page Object Model. The framework is configurable, reusable, and uses explicit waits only.

## Tech Stack
- Java 11+
- Maven
- Selenium WebDriver
- TestNG
- WebDriverManager

## Setup Steps
1. Install JDK 11+ and Maven.
2. Ensure Google Chrome is installed.
3. Update `src/test/resources/config.properties` if needed.
4. Run tests using Maven or TestNG.

## How to Run Tests
- Maven:
  mvn clean test
- TestNG (IDE):
  Run `testng.xml`

## Sample Output
- Flight results count: 25
- Cheapest Flight: Airline=IndiGo, Price=5432, Departure=08:15, Arrival=10:45
- Second Cheapest Flight: Airline=Air India, Price=5600, Departure=09:25, Arrival=11:55
- Screenshot saved to: screenshots/results_20240101_101010.png
