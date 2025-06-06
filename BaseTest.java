import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {
    protected WebDriver driver;

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // Reklam engelleyici eklenti (AdBlock.crx) ekleniyor
        options.addExtensions(new File("D:\\4. Sınıf\\Kalite\\Proje\\kalite_selenium\\Adblock.crx"));

        // Pop-up ve bildirim engelleme ayarları
        options.setExperimentalOption("excludeSwitches", Arrays.asList("disable-popup-blocking"));

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2); // Bildirimleri engelle
        prefs.put("profile.default_content_setting_values.ads", 2); // Reklamları engelle
        prefs.put("profile.managed_default_content_settings.popups", 0); // Pop-up'ları engelle
        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
