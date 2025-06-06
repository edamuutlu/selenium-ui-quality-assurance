import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;


public class LoginTest extends BaseTest {

    // Geçerli e-posta ve şifre ile başarılı giriş testi
    @Test
    public void testValidLogin() {
        driver.get("https://practice.expandtesting.com/notes/app/login");

        driver.findElement(By.id("email")).sendKeys("grup10@marmara.com");
        driver.findElement(By.id("password")).sendKeys("marmaraGrup10Selenium");
        driver.findElement(By.cssSelector("button[data-testid='login-submit']")).click();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/notes/app"));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/notes/app"), "Girişten sonra beklenen sayfaya yönlendirilmedi.");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(driver.getCurrentUrl().contains("/notes/app"), "Başarılı giriş yapılmadı.");
    }


    // Hatalı kullanıcı adı ve şifre ile giriş testi
    @Test
    public void testInvalidLogin() {
        driver.get("https://practice.expandtesting.com/notes/app/login");

        driver.findElement(By.id("email")).sendKeys("wrong@wrong.com");
        driver.findElement(By.id("password")).sendKeys("wrongpass");
        WebElement submitButton = driver.findElement(By.cssSelector("button[data-testid='login-submit']"));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
        submitButton.click();

        // Yeni hata mesajı elementi için bekle
        WebElement errorMessage = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.toast-body[data-testid='alert-message']")));

        // Mesajın boş olmadığını kontrol et
        String messageText = errorMessage.getText().trim();
        Assert.assertFalse(messageText.isEmpty(), "Hata mesajı görünmedi veya boş.");
        Assert.assertEquals(messageText, "Incorrect email address or password", "Hata mesajı beklenenden farklı.");
    }

    // E-posta alanı boş bırakılarak yapılan giriş testi
    @Test
    public void testEmptyEmailField() {
        driver.get("https://practice.expandtesting.com/notes/app/login");

        // Email alanını boş bırakıyoruz
        driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
        driver.findElement(By.cssSelector("button[data-testid='login-submit']")).click();

        // Hata mesajının görünmesini bekle
        WebElement emailError = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(By.className("invalid-feedback")));

        String errorText = emailError.getText().trim();

        Assert.assertFalse(errorText.isEmpty(), "Boş e-posta alanı için hata mesajı görünmeli.");
        Assert.assertEquals(errorText, "Email address is required", "Hata mesajı beklenenden farklı.");
    }

    // Şifre alanı boş bırakılarak yapılan giriş testi
    @Test
    public void testEmptyPasswordField() {
        driver.get("https://practice.expandtesting.com/notes/app/login");

        // Password boş, sadece email giriliyor
        driver.findElement(By.id("email")).sendKeys("practice@expandtesting.com");
        driver.findElement(By.cssSelector("button[data-testid='login-submit']")).click();

        // Password alanına ait error div'ini daha net hedefliyoruz
        By passwordErrorLocator = By.cssSelector("#password + .invalid-feedback");

        WebElement passwordError = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(passwordErrorLocator));

        String errorText = passwordError.getText().trim();

        Assert.assertFalse(errorText.isEmpty(), "Boş şifre alanı için hata mesajı görünmeli.");
        Assert.assertEquals(errorText, "Password is required", "Hata mesajı beklenenden farklı.");
    }

    // Geçersiz formatta e-posta ile giriş denemesi
    @Test
    public void testInvalidEmailFormat() {
        driver.get("https://practice.expandtesting.com/notes/app/login");

        driver.findElement(By.id("email")).sendKeys("invalid-email");
        driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
        driver.findElement(By.cssSelector("button[data-testid='login-submit']")).click();

        WebElement error = driver.findElement(By.className("invalid-feedback"));
        Assert.assertTrue(error.isDisplayed(), "Geçersiz e-posta formatı için hata mesajı bekleniyordu.");
    }

    // Sosyal medya (Google) ile giriş butonunun çalışırlık testi
    @Test
    public void testLoginWithGoogleRedirect() {
        driver.get("https://practice.expandtesting.com/notes/app/login");

        WebElement googleBtn = driver.findElement(By.cssSelector("a[data-testid='login-with-google']"));
        Assert.assertTrue(googleBtn.isDisplayed(), "Google ile giriş butonu görünmüyor.");

        googleBtn.click();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("accounts.google.com"));

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("google"), "Google giriş yönlendirmesi başarısız.");
    }

    // Sosyal medya (LinkedIn) ile giriş butonunun çalışırlık testi
    @Test
    public void testLoginWithLinkedInRedirect() {
        driver.get("https://practice.expandtesting.com/notes/app/login");

        WebElement linkedinBtn = driver.findElement(By.cssSelector("a[data-testid='login-with-linkedin']"));
        Assert.assertTrue(linkedinBtn.isDisplayed(), "LinkedIn ile giriş butonu görünmüyor.");

        linkedinBtn.click();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("linkedin.com"));

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("linkedin"), "LinkedIn giriş yönlendirmesi başarısız.");
    }

    // Kullanıcı adı büyük/küçük harf duyarlılığı testi
    @Test
    public void testCaseSensitivityInUsername() {
        driver.get("https://practice.expandtesting.com/notes/app/login");

        driver.findElement(By.id("email")).sendKeys("PRACTICE@expandtesting.com");
        driver.findElement(By.id("password")).sendKeys("Super.SecrACTICEd!");
        driver.findElement(By.cssSelector("button[data-testid='login-submit']")).click();

        By alertLocator = By.cssSelector("[data-testid='alert-message']");
        WebElement alert = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(alertLocator));

        Assert.assertTrue(alert.getText().contains("Incorrect"), "Hata mesajı beklenenden farklı.");
    }

}
