package utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

public final class DriverFactory {
    private DriverFactory() {
    }

    public static WebDriver createInstance() {
        String browser = ConfigReader.getProperty("browser");
        int implicitWait = ConfigReader.getIntProperty("implicitWait", 0);

        if (browser == null || browser.trim().isEmpty()) {
            browser = "chrome";
        }

        if (!"chrome".equalsIgnoreCase(browser)) {
            throw new IllegalArgumentException("Only Chrome is supported in this framework");
        }

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        String profileOverride = System.getProperty("chromeProfileDir");
        if (profileOverride == null || profileOverride.trim().isEmpty()) {
            profileOverride = ConfigReader.getProperty("chromeProfileDir");
        }
        boolean reuseProfile = ConfigReader.getBooleanProperty("reuseProfile", false);
        Path profilePath;
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--lang=en-US");
        options.addArguments("--no-default-browser-check");
        options.addArguments("--disable-features=TranslateUI");
        options.addArguments("--disable-features=UserAgentClientHint");
        if (profileOverride != null && !profileOverride.trim().isEmpty()) {
            profilePath = Paths.get(profileOverride.trim());
        } else if (reuseProfile) {
            profilePath = Paths.get("target", "chrome-profile");
        } else {
            profilePath = Paths.get("target", "chrome-profile-" + System.currentTimeMillis());
        }
        try {
            Files.createDirectories(profilePath);
        } catch (Exception ignored) {
        }
        options.addArguments("--user-data-dir=" + profilePath.toAbsolutePath());
        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        String headlessValue = System.getProperty("headless");
        if (headlessValue == null || headlessValue.trim().isEmpty()) {
            headlessValue = ConfigReader.getProperty("headless");
        }
        if ("true".equalsIgnoreCase(headlessValue)) {
            options.addArguments("--headless=new");
        }

        ChromeDriver driver = new ChromeDriver(options);
        applyStealth(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().window().maximize();
        return driver;
    }

    private static void applyStealth(ChromeDriver driver) {
        Map<String, Object> uaParams = new HashMap<>();
        uaParams.put("userAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        uaParams.put("acceptLanguage", "en-US,en");
        uaParams.put("platform", "Windows");
        driver.executeCdpCommand("Network.setUserAgentOverride", uaParams);

        String script = "Object.defineProperty(navigator, 'webdriver', {get: () => undefined});"
                + "Object.defineProperty(navigator, 'languages', {get: () => ['en-US', 'en']});"
                + "Object.defineProperty(navigator, 'platform', {get: () => 'Win32'});"
                + "Object.defineProperty(navigator, 'plugins', {get: () => [1, 2, 3, 4, 5]});"
                + "window.chrome = window.chrome || { runtime: {} };"
                + "const originalQuery = window.navigator.permissions.query;"
                + "window.navigator.permissions.query = (parameters) => (parameters && parameters.name === 'notifications')"
                + " ? Promise.resolve({ state: Notification.permission }) : originalQuery(parameters);";
        Map<String, Object> params = new HashMap<>();
        params.put("source", script);
        driver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", params);
    }
}
