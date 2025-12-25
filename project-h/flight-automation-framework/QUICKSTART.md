# ⚡ Quick Start Guide - Automation Testing iXigo

## 🎯 Get Started in 5 Minutes

### 1️⃣ **Clone/Download the Project**
```bash
cd flight-automation-framework
```

### 2️⃣ **Verify Prerequisites**
```bash
# Check Java (11 or higher required)
java -version

# Check Maven (3.6 or higher required)
mvn -version

# Check Chrome is installed
# Download: https://www.google.com/chrome/
```

### 3️⃣ **Run Tests Immediately**
```bash
# Run all tests
mvn clean test

# That's it! Tests will execute and generate reports
```

### 4️⃣ **View Results**
- ✅ Console output shows test results
- 📸 Screenshots saved to: `target/screenshots/`
- 📊 Reports generated in: `target/surefire-reports/`

---

## 📚 Quick Documentation Links

| Document | Purpose | Read Time |
|----------|---------|-----------|
| [README.md](README.md) | Complete project guide | 10 min |
| [ARCHITECTURE.md](ARCHITECTURE.md) | Technical details | 8 min |
| [CONTRIBUTING.md](CONTRIBUTING.md) | Contribution guide | 7 min |
| [PROJECT-SUMMARY.md](PROJECT-SUMMARY.md) | What's new | 5 min |

---

## 🔧 Common Commands

### Run Tests
```bash
# Run all tests
mvn clean test

# Run specific test
mvn test -Dtest=FlightSearchTest

# Run with detailed output
mvn test -X
```

### Build Project
```bash
# Build without tests
mvn clean compile

# Build with tests
mvn clean install
```

### Generate Reports
```bash
# Generate Surefire report
mvn surefire-report:report

# View report
# Open: target/site/surefire-report.html
```

---

## ⚙️ Configure Tests

Edit `src/test/resources/config.properties`:

```properties
# Change departure city
fromCity=Delhi

# Change destination city
toCity=Goa

# Change departure date
departureDay=25
departureMonth=January
```

---

## 🐛 Troubleshooting

### Issue: Chrome Driver not found
```
✅ Solution: WebDriverManager downloads automatically
           Ensure you have internet connection
```

### Issue: Tests timeout
```
✅ Solution: Increase wait times in config.properties
           Check if website is accessible
```

### Issue: Element not found
```
✅ Solution: Website structure may have changed
           Check console output for detailed errors
           Review locators in page objects
```

---

## 📁 Key Files

```
src/test/java/
├── tests/
│   └── FlightSearchTest.java      ← Main test
├── pages/
│   ├── HomePage.java              ← Home page
│   ├── FlightsPage.java           ← Search page
│   └── SearchResultsPage.java     ← Results page
├── base/
│   ├── BasePage.java              ← Common page methods
│   ├── BaseTest.java              ← Common test setup
│   └── DriverManager.java         ← WebDriver management
└── utils/
    ├── ConfigReader.java          ← Configuration
    ├── DateUtils.java             ← Date utilities
    └── ScreenshotUtils.java       ← Screenshot capture

src/test/resources/
├── config.properties              ← Test configuration
└── log4j.properties               ← Logging config
```

---

## ✨ What This Framework Does

✅ **Automates flight search** on iXigo website
✅ **Validates results** are loaded correctly
✅ **Sorts flights** by cheapest price
✅ **Extracts flight details** (airline, price, times)
✅ **Filters flights** by price range
✅ **Captures screenshots** of results
✅ **Generates reports** automatically
✅ **Runs on CI/CD** via GitHub Actions

---

## 🚀 Next Steps

1. **Review README.md** for complete documentation
2. **Read ARCHITECTURE.md** to understand design
3. **Check CONTRIBUTING.md** if contributing
4. **Run tests**: `mvn clean test`
5. **View results** in console and target folder

---

## 💡 Pro Tips

### Faster Test Execution
```bash
# Skip tests during build
mvn clean compile -DskipTests

# Run in headless mode (faster)
# Edit config.properties: headless=true
```

### Debugging
```bash
# Run with debug output
mvn test -X

# Run single test method
mvn test -Dtest=FlightSearchTest#endToEndFlightSearch

# Keep browser open for debugging
# Edit BaseTest.java: comment out driver.quit()
```

### Screenshots
```bash
# Screenshots auto-saved in: target/screenshots/
# Named with timestamp: results_YYYYMMDD_HHMMSS.png

# View latest screenshot
# Windows: start target/screenshots/
# Mac: open target/screenshots/
# Linux: xdg-open target/screenshots/
```

---

## 🎓 Learning Path

1. **Beginner**: Run tests and view output
2. **Intermediate**: Edit config.properties, run different cities
3. **Advanced**: Add new test methods, create new page objects
4. **Expert**: Extend framework, add new features

---

## 📞 Help & Support

| Issue | Solution |
|-------|----------|
| Tests won't run | Check Java/Maven versions |
| Chrome driver error | Update Chrome browser |
| Element not found | Website structure may have changed |
| Timeout errors | Slow internet, increase wait time |
| Want to contribute | Read CONTRIBUTING.md |

---

## ✅ Checklist to Get Started

- [ ] Java 11+ installed
- [ ] Maven 3.6+ installed
- [ ] Chrome browser installed
- [ ] Project downloaded/cloned
- [ ] Ran `mvn clean test`
- [ ] Tests passed ✅
- [ ] Viewed results in target folder
- [ ] Read README.md
- [ ] Ready to contribute!

---

**Happy Testing! 🚀**

For complete information, see [README.md](README.md)
For contributing, see [CONTRIBUTING.md](CONTRIBUTING.md)
For technical details, see [ARCHITECTURE.md](ARCHITECTURE.md)
