import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.Set;

public class GoogleVignetteHandler {
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    public GoogleVignetteHandler(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.js = (JavascriptExecutor) driver;
    }

    /**
     * Google Vignette overlay'ini tespit eder ve kaldırır
     */
    public void handleGoogleVignette() {
        try {
            // Overlay'i JavaScript ile kaldırma
            js.executeScript(
                    "var overlays = document.querySelectorAll('[id*=\"google_vignette\"], [class*=\"google-vignette\"], [id*=\"goog-\"]');" +
                            "overlays.forEach(function(overlay) { overlay.remove(); });" +

                            "var iframes = document.querySelectorAll('iframe');" +
                            "iframes.forEach(function(iframe) {" +
                            "  if(iframe.src && (iframe.src.includes('google') || iframe.src.includes('doubleclick'))) {" +
                            "    iframe.remove();" +
                            "  }" +
                            "});" +

                            "document.body.style.overflow = 'auto';" +
                            "document.documentElement.style.overflow = 'auto';"
            );

            // Bilinen Google Vignette elementlerini arama
            String[] selectors = {
                    "[id*='google_vignette']",
                    "[class*='google-vignette']",
                    "[id*='goog-']",
                    "iframe[src*='google']",
                    "iframe[src*='doubleclick']",
                    ".overlay",
                    "#overlay",
                    "[data-google-vignette]"
            };

            for (String selector : selectors) {
                try {
                    WebElement element = driver.findElement(By.cssSelector(selector));
                    if (element.isDisplayed()) {
                        js.executeScript("arguments[0].remove();", element);
                        System.out.println("Google Vignette overlay kaldırıldı: " + selector);
                    }
                } catch (Exception e) {
                    // Element bulunamazsa devam et
                }
            }

        } catch (Exception e) {
            System.out.println("Google Vignette handler hatası: " + e.getMessage());
        }
    }

    /**
     * Iframe'ler arasında geçiş yaparak overlay'i arar
     */
    public void handleVignetteInIframes() {
        try {
            String mainWindow = driver.getWindowHandle();

            // Tüm iframe'leri kontrol et
            var iframes = driver.findElements(By.tagName("iframe"));

            for (WebElement iframe : iframes) {
                try {
                    driver.switchTo().frame(iframe);

                    // Close butonunu ara
                    String[] closeSelectors = {
                            "[aria-label*='Close']",
                            "[title*='Close']",
                            ".close",
                            "#close",
                            "[onclick*='close']"
                    };

                    for (String selector : closeSelectors) {
                        try {
                            WebElement closeBtn = driver.findElement(By.cssSelector(selector));
                            if (closeBtn.isDisplayed()) {
                                closeBtn.click();
                                System.out.println("Iframe içinde close butonu tıklandı");
                                break;
                            }
                        } catch (Exception e) {
                            // Continue
                        }
                    }

                    driver.switchTo().defaultContent();
                } catch (Exception e) {
                    driver.switchTo().defaultContent();
                }
            }
        } catch (Exception e) {
            System.out.println("Iframe handler hatası: " + e.getMessage());
        }
    }

    /**
     * Sayfa yüklendikten sonra overlay kontrolü
     */
    public void waitAndHandleOverlay() {
        try {
            // Sayfanın yüklenmesini bekle
            Thread.sleep(3000);

            // Google Vignette'i işle
            handleGoogleVignette();

            // Iframe'leri kontrol et
            handleVignetteInIframes();

            // Son bir kez overlay kontrolü
            Thread.sleep(1000);
            handleGoogleVignette();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * CSS ile overlay'leri gizleme
     */
    public void hideOverlaysWithCSS() {
        js.executeScript(
                "var style = document.createElement('style');" +
                        "style.textContent = '" +
                        "[id*=\"google_vignette\"] { display: none !important; } " +
                        "[class*=\"google-vignette\"] { display: none !important; } " +
                        "iframe[src*=\"google\"] { display: none !important; } " +
                        "iframe[src*=\"doubleclick\"] { display: none !important; } " +
                        ".overlay { display: none !important; } " +
                        "#overlay { display: none !important; }" +
                        "';" +
                        "document.head.appendChild(style);"
        );
    }
}