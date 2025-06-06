import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

public class BaseTest {
    protected WebDriver driver;

    @BeforeMethod
    public void setup() {
        // CDP uyarısını gidermek için WebDriverManager'ı güncelleyin
        System.setProperty("webdriver.chrome.silentOutput", "true");

        ChromeOptions options = new ChromeOptions();

        // Agresif popup ve overlay engelleme
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-features=VizDisplayCompositor");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--no-first-run");
        options.addArguments("--no-default-browser-check");
        options.addArguments("--disable-extensions-http-throttling");
        options.addArguments("--disable-component-extensions-with-background-pages");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-renderer-backgrounding");
        options.addArguments("--disable-backgrounding-opaque-render-widgets");

        // Google servislerini engelleme
        options.addArguments("--disable-sync");
        options.addArguments("--disable-background-networking");
        options.addArguments("--disable-client-side-phishing-detection");
        options.addArguments("--disable-cloud-import");

        // Host resolver kuralları - Google servislerini engelle
        options.addArguments("--host-resolver-rules=MAP googleads.g.doubleclick.net 127.0.0.1,MAP googlesyndication.com 127.0.0.1,MAP google-analytics.com 127.0.0.1");

        // Gelişmiş preferences
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("profile.default_content_settings.popups", 0);
        prefs.put("profile.managed_default_content_settings.popups", 0);
        prefs.put("profile.content_settings.exceptions.plugins.*,*.per_resource.plugins", 2);
        prefs.put("profile.default_content_setting_values.media_stream", 2);
        prefs.put("profile.default_content_setting_values.geolocation", 2);
        prefs.put("profile.ads_setting", 2);
        prefs.put("profile.enable_referrers", false);

        options.setExperimentalOption("prefs", prefs);
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation", "enable-blink-features"));

        // Güncel User-Agent
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36");

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // Aggressive JavaScript ile overlay engelleme
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "window.alert = function() {};" +
                        "window.confirm = function() { return true; };" +
                        "window.prompt = function() { return ''; };" +
                        "Object.defineProperty(navigator, 'webdriver', {get: () => undefined});" +

                        // URL hash'ini temizleme fonksiyonu
                        "window.cleanUrl = function() {" +
                        "  if(window.location.hash.includes('google_vignette')) {" +
                        "    history.replaceState(null, null, window.location.pathname + window.location.search);" +
                        "  }" +
                        "};" +

                        // CSS ile overlay'leri gizle
                        "var style = document.createElement('style');" +
                        "style.textContent = '" +
                        "[id*=\"google_vignette\"] { display: none !important; visibility: hidden !important; } " +
                        "[class*=\"google-vignette\"] { display: none !important; visibility: hidden !important; } " +
                        "iframe[src*=\"google\"] { display: none !important; visibility: hidden !important; } " +
                        "iframe[src*=\"doubleclick\"] { display: none !important; visibility: hidden !important; } " +
                        ".overlay { display: none !important; visibility: hidden !important; } " +
                        "#overlay { display: none !important; visibility: hidden !important; } " +
                        "[data-google-vignette] { display: none !important; visibility: hidden !important; }" +
                        "';" +
                        "document.head.appendChild(style);"
        );
    }

    /**
     * Herhangi bir zamanda çağrılabilecek agresif overlay temizleyici
     */
    public void forceRemoveAllOverlays() {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    // Tüm bilinen overlay selector'larını kaldır
                    "var selectors = [" +
                            "'[id*=\"google_vignette\"]'," +
                            "'[class*=\"google-vignette\"]'," +
                            "'[data-google-vignette]'," +
                            "'iframe[src*=\"google\"]'," +
                            "'iframe[src*=\"doubleclick\"]'," +
                            "'iframe[src*=\"googlesyndication\"]'," +
                            "'.overlay'," +
                            "'#overlay'," +
                            "'[style*=\"z-index: 999\"]'," +
                            "'[style*=\"position: fixed\"]'" +
                            "];" +

                            "selectors.forEach(function(selector) {" +
                            "  var elements = document.querySelectorAll(selector);" +
                            "  elements.forEach(function(el) {" +
                            "    if (el && el.parentNode) {" +
                            "      el.parentNode.removeChild(el);" +
                            "    }" +
                            "  });" +
                            "});" +

                            // Body scroll'unu etkinleştir
                            "document.body.style.overflow = 'auto';" +
                            "document.documentElement.style.overflow = 'auto';" +
                            "document.body.style.position = 'static';" +

                            // URL hash'ini temizle
                            "if(window.location.hash.includes('google_vignette')) {" +
                            "  history.replaceState(null, null, window.location.pathname + window.location.search);" +
                            "}"
            );

            System.out.println("Tüm overlay'ler zorla kaldırıldı");
        } catch (Exception e) {
            System.out.println("Overlay kaldırma hatası: " + e.getMessage());
        }
    }

    /**
     * URL'den google_vignette hash'ini temizler
     */
    public void cleanGoogleVignetteFromUrl() {
        try {
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("#google_vignette")) {
                String cleanUrl = currentUrl.replace("#google_vignette", "");
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                        "history.replaceState(null, null, arguments[0]);", cleanUrl
                );
                System.out.println("URL temizlendi: " + cleanUrl);
            }
        } catch (Exception e) {
            System.out.println("URL temizleme hatası: " + e.getMessage());
        }
    }

    /**
     * Sayfa yüklendikten sonra overlay ve URL temizliği
     */
    public void handlePageLoad(String url) {
        driver.get(url);

        // Biraz bekle
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Overlay'leri kaldır
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "var overlays = document.querySelectorAll('[id*=\"google_vignette\"], [class*=\"google-vignette\"], [data-google-vignette]');" +
                        "overlays.forEach(function(el) { el.remove(); });" +

                        "var iframes = document.querySelectorAll('iframe');" +
                        "iframes.forEach(function(iframe) {" +
                        "  if(iframe.src && (iframe.src.includes('google') || iframe.src.includes('doubleclick'))) {" +
                        "    iframe.remove();" +
                        "  }" +
                        "});"
        );

        // URL'i temizle
        cleanGoogleVignetteFromUrl();

        // Tekrar bekle
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
