import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class RegisterTest extends BaseTest {

    // Geçerli ve benzersiz bilgilerle başarılı kayıt işlemini test eder.
    @Test
    public void testValidRegistration() throws InterruptedException {
        driver.get("https://practice.expandtesting.com/notes/app/register");
        Thread.sleep(1000);

        String uniqueEmail = "user" + System.currentTimeMillis() + "@example.com";

        WebElement emailInput = driver.findElement(By.id("email"));
        emailInput.clear();
        emailInput.sendKeys(uniqueEmail);

        WebElement nameInput = driver.findElement(By.id("name"));
        nameInput.clear();
        nameInput.sendKeys("Test User");

        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.clear();
        passwordInput.sendKeys("TestPassword123!");

        WebElement confirmPasswordInput = driver.findElement(By.id("confirmPassword"));
        confirmPasswordInput.clear();
        confirmPasswordInput.sendKeys("TestPassword123!");

        Thread.sleep(1000);

        driver.findElement(By.cssSelector("button[data-testid='register-submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement successAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.alert.alert-success")
        ));

        String alertText = successAlert.getText();
        Assert.assertTrue(alertText.contains("User account created successfully"),
                "Kayıt başarılı mesajı görünmedi veya beklenen mesajla uyuşmuyor.");
    }

    // Boş form gönderildiğinde doğrulama hatalarının görünüp görünmediğini test eder.
    @Test
    public void testInvalidRegistration_ShowsValidationErrors() throws InterruptedException {
        driver.get("https://practice.expandtesting.com/notes/app/register");
        Thread.sleep(1000);

        driver.findElement(By.cssSelector("button[data-testid='register-submit']")).click();
        Thread.sleep(1000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        var errorFields = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("invalid-feedback"))
        );

        boolean hasAnyError = errorFields.stream().anyMatch(e -> !e.getText().isEmpty());
        Assert.assertTrue(hasAnyError, "Boş form gönderildiğinde hata mesajı görünmedi.");
    }

    // Parola ve parola onayı uyuşmadığında hata mesajının görünüp görünmediğini test eder.
    @Test
    public void testPasswordMismatch() throws InterruptedException {
        driver.get("https://practice.expandtesting.com/notes/app/register");
        Thread.sleep(1000);

        String uniqueEmail = "user" + System.currentTimeMillis() + "@example.com";

        driver.findElement(By.id("email")).clear();
        driver.findElement(By.id("email")).sendKeys(uniqueEmail);

        driver.findElement(By.id("name")).clear();
        driver.findElement(By.id("name")).sendKeys("Test User");

        // password ve confirmPassword alanlarını doğru şekilde bulup gönderiyoruz
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys("Password1!");

        driver.findElement(By.id("confirmPassword")).clear();
        driver.findElement(By.id("confirmPassword")).sendKeys("DifferentPassword!");

        Thread.sleep(1000);

        driver.findElement(By.cssSelector("button[data-testid='register-submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // password alanıyla ilgili invalid-feedback sınıfı altında hata mesajı çıkıyor olabilir,
        // burada biraz daha spesifik olabiliriz:
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@id='confirmPassword']/following-sibling::div[contains(@class,'invalid-feedback')]")
        ));

        Assert.assertTrue(error.getText().toLowerCase().contains("match") ||
                        error.getText().toLowerCase().contains("aynı"),
                "Parola uyuşmazlığı için hata mesajı görünmedi.");
    }

    // Geçersiz e-posta formatı girildiğinde hata mesajı çıkıp çıkmadığını kontrol eder.
    @Test
    public void testInvalidEmailFormat() throws InterruptedException {
        driver.get("https://practice.expandtesting.com/notes/app/register");
        Thread.sleep(1000);

        driver.findElement(By.id("email")).sendKeys("invalid-email.com");
        driver.findElement(By.id("name")).sendKeys("Test User");
        driver.findElement(By.id("password")).sendKeys("Password123!");
        driver.findElement(By.id("confirmPassword")).sendKeys("Password123!");
        Thread.sleep(1000);

        driver.findElement(By.cssSelector("button[data-testid='register-submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        WebElement emailError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("invalid-feedback")));
        Assert.assertTrue(emailError.getText().toLowerCase().contains("valid"), "Geçersiz e-posta hatası bekleniyordu.");
    }

    // E-posta alanı boş bırakıldığında sistemin nasıl tepki verdiğini kontrol eder.
    @Test
    public void testEmptyEmailField() throws InterruptedException {
        driver.get("https://practice.expandtesting.com/notes/app/register");
        Thread.sleep(1000);

        driver.findElement(By.id("name")).sendKeys("Test User");
        driver.findElement(By.id("password")).sendKeys("Password123!");
        driver.findElement(By.id("confirmPassword")).sendKeys("Password123!");
        Thread.sleep(1000);

        driver.findElement(By.cssSelector("button[data-testid='register-submit']")).click();

        WebElement error = driver.findElement(By.className("invalid-feedback"));
        Assert.assertTrue(error.isDisplayed(), "Boş e-posta alanı için hata mesajı gösterilmeli.");
    }

    // Aşırı uzun e-posta ve parola değerleri girildiğinde sistemin nasıl davrandığını test eder.
    @Test
    public void testExcessivelyLongInputFields() throws InterruptedException {
        driver.get("https://practice.expandtesting.com/notes/app/register");
        Thread.sleep(1000);

        String longEmail = "user" + "a".repeat(300) + "@test.com";
        String longPassword = "P".repeat(300);

        driver.findElement(By.id("email")).sendKeys(longEmail);
        driver.findElement(By.id("name")).sendKeys("User With Very Long Input");
        driver.findElement(By.id("password")).sendKeys(longPassword);
        driver.findElement(By.id("confirmPassword")).sendKeys(longPassword);
        Thread.sleep(1000);

        driver.findElement(By.cssSelector("button[data-testid='register-submit']")).click();

        WebElement error = driver.findElement(By.className("invalid-feedback"));
        Assert.assertTrue(error.isDisplayed(), "Çok uzun girişler için hata mesajı bekleniyordu.");
    }

    // Ad alanına script (XSS) kodu girildiğinde sistemin koruma sağlayıp sağlamadığını test eder.
    @Test
    public void testScriptInjectionInNameField() throws InterruptedException {
        driver.get("https://practice.expandtesting.com/notes/app/register");
        Thread.sleep(1000);

        String email = "xss" + System.currentTimeMillis() + "@test.com";
        String maliciousScript = "<script>alert('xss')</script>";

        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("name")).sendKeys(maliciousScript);
        driver.findElement(By.id("password")).sendKeys("StrongPass123!");
        driver.findElement(By.id("confirmPassword")).sendKeys("StrongPass123!");
        Thread.sleep(1000);

        driver.findElement(By.cssSelector("button[data-testid='register-submit']")).click();

        WebElement error = driver.findElement(By.className("invalid-feedback"));
        Assert.assertTrue(error.isDisplayed() || !driver.getPageSource().contains("script"),
                "Script injection girişimine karşı koruma yok.");
    }

    // Daha önce kayıtlı bir e-posta adresiyle tekrar kayıt olmaya çalışıldığında sistemin hata vermesini test eder.
    @Test
    public void testDuplicateEmailRegistration() throws InterruptedException {
        driver.get("https://practice.expandtesting.com/notes/app/register");
        Thread.sleep(1000);

        String existingEmail = "practice@expandtesting.com";

        WebElement emailInput = driver.findElement(By.id("email"));
        emailInput.clear();
        emailInput.sendKeys(existingEmail);

        WebElement nameInput = driver.findElement(By.id("name"));
        nameInput.clear();
        nameInput.sendKeys("Test Again");

        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.clear();
        passwordInput.sendKeys("Password123!");

        WebElement confirmPasswordInput = driver.findElement(By.id("confirmPassword"));
        confirmPasswordInput.clear();
        confirmPasswordInput.sendKeys("Password123!");

        Thread.sleep(1000);

        driver.findElement(By.cssSelector("button[data-testid='register-submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div[data-testid='alert-message']")
        ));

        String errorText = error.getText().toLowerCase();
        Assert.assertTrue(errorText.contains("already") || errorText.contains("exist"),
                "Aynı e-posta ile ikinci kayıt hatası bekleniyordu.");
    }

    @Test
    public void testRegisterWithGoogleButton() throws InterruptedException {
        driver.get("https://practice.expandtesting.com/notes/app/register");
        Thread.sleep(1000);

        WebElement googleButton = driver.findElement(By.cssSelector("a[data-testid='login-with-google']"));
        Assert.assertTrue(googleButton.isDisplayed(), "Google ile kayıt butonu görünmüyor.");
        Assert.assertTrue(googleButton.isEnabled(), "Google ile kayıt butonu tıklanabilir değil.");

        // Butona tıkla ve URL'nin Google OAuth sayfasına yönlendirdiğini doğrula
        googleButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.urlContains("accounts.google.com"));

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("accounts.google.com"), "Google OAuth sayfasına yönlendirilmedi.");
    }

    @Test
    public void testRegisterWithLinkedInButton() throws InterruptedException {
        driver.get("https://practice.expandtesting.com/notes/app/register");
        Thread.sleep(1000);

        WebElement linkedinButton = driver.findElement(By.cssSelector("a[data-testid='login-with-linkedin']"));
        Assert.assertTrue(linkedinButton.isDisplayed(), "LinkedIn ile kayıt butonu görünmüyor.");
        Assert.assertTrue(linkedinButton.isEnabled(), "LinkedIn ile kayıt butonu tıklanabilir değil.");

        // Butona tıkla ve URL'nin LinkedIn OAuth sayfasına yönlendirdiğini doğrula
        linkedinButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("linkedin.com"));

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("linkedin.com"), "LinkedIn OAuth sayfasına yönlendirilmedi.");
    }

}
