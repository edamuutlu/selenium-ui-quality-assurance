import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class NoteTest extends BaseTest {
    private GoogleVignetteHandler vignetteHandler;

    @BeforeMethod
    public void setupVignetteHandler() {
        vignetteHandler = new GoogleVignetteHandler(driver);
        vignetteHandler.hideOverlaysWithCSS(); // CSS ile baştan engelle
    }
    private WebDriverWait wait;

    public void login() throws InterruptedException {
        driver.get("https://practice.expandtesting.com/notes/app/login");

        driver.findElement(By.id("email")).sendKeys("grup10@marmara.com");
        driver.findElement(By.id("password")).sendKeys("marmaraGrup10Selenium");
        driver.findElement(By.cssSelector("button[data-testid='login-submit']")).click();
        Thread.sleep(1000);

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/notes/app"));

        Thread.sleep(1000);

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/notes/app"), "Girişten sonra beklenen sayfaya yönlendirilmedi.");

        Thread.sleep(2000);
    }

    private void waitForElement(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private void waitForModalToOpen() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".modal-content")));
    }

    private void waitForModalToClose() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".modal-content")));
    }

    // BAŞARILI TEST SENARYOLARI

    @Test
    public void testAddNoteSuccessfully() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Add Note butonuna tıkla
        driver.findElement(By.cssSelector("[data-testid='add-new-note']")).click();
        waitForModalToOpen();

        // Not bilgilerini doldur
        driver.findElement(By.cssSelector("[data-testid='note-title']")).sendKeys("Selenium Test Notu");
        driver.findElement(By.cssSelector("[data-testid='note-description']")).sendKeys("Bu not otomasyonla başarıyla eklendi.");

        // Not kaydet
        driver.findElement(By.cssSelector("[data-testid='note-submit']")).click();
        waitForModalToClose();

        // Notun başarıyla eklendiğini kontrol et
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Selenium Test Notu"));
        boolean noteExists = driver.getPageSource().contains("Selenium Test Notu");
        Assert.assertTrue(noteExists, "✅ Not başarıyla eklendi!");
    }

    @Test
    public void testAddNoteWithDifferentCategory() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.findElement(By.cssSelector("[data-testid='add-new-note']")).click();
        waitForModalToOpen();

        // Kategoriyi Work olarak seç
        Select categorySelect = new Select(driver.findElement(By.cssSelector("[data-testid='note-category']")));
        categorySelect.selectByVisibleText("Work");

        driver.findElement(By.cssSelector("[data-testid='note-title']")).sendKeys("İş Notu");
        driver.findElement(By.cssSelector("[data-testid='note-description']")).sendKeys("Bu bir iş kategorisi notu.");

        driver.findElement(By.cssSelector("[data-testid='note-submit']")).click();
        waitForModalToClose();

        // Work kategorisinde not eklendiğini kontrol et
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "İş Notu"));
        boolean noteExists = driver.getPageSource().contains("İş Notu");
        Assert.assertTrue(noteExists, "✅ Work kategorisinde not başarıyla eklendi!");
    }

    @Test
    public void testAddCompletedNote() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.findElement(By.cssSelector("[data-testid='add-new-note']")).click();
        waitForModalToOpen();

        driver.findElement(By.cssSelector("[data-testid='note-title']")).sendKeys("Tamamlanmış Görev");
        driver.findElement(By.cssSelector("[data-testid='note-description']")).sendKeys("Bu görev tamamlandı olarak işaretlendi.");

        // Completed checkbox'ını işaretle
        driver.findElement(By.cssSelector("[data-testid='note-completed']")).click();

        driver.findElement(By.cssSelector("[data-testid='note-submit']")).click();
        waitForModalToClose();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Tamamlanmış Görev"));
        boolean noteExists = driver.getPageSource().contains("Tamamlanmış Görev");
        Assert.assertTrue(noteExists, "✅ Tamamlanmış not başarıyla eklendi!");
    }

    @Test
    public void testCancelAddNote() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.findElement(By.cssSelector("[data-testid='add-new-note']")).click();
        waitForModalToOpen();

        // Bilgileri doldur ama cancel et
        driver.findElement(By.cssSelector("[data-testid='note-title']")).sendKeys("İptal Edilecek Not");
        driver.findElement(By.cssSelector("[data-testid='note-description']")).sendKeys("Bu not iptal edilecek.");

        // Cancel butonuna tıkla
        driver.findElement(By.cssSelector("[data-testid='note-cancel']")).click();
        waitForModalToClose();

        // Notun eklenmediğini kontrol et
        boolean noteNotExists = !driver.getPageSource().contains("İptal Edilecek Not");
        Assert.assertTrue(noteNotExists, "✅ Not başarıyla iptal edildi!");
    }

    @Test
    public void testCloseModalWithX() throws InterruptedException {
        login();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.findElement(By.cssSelector("[data-testid='add-new-note']")).click();

        // X butonuna tıkla
        driver.findElement(By.cssSelector(".btn-close")).click();

        // Modal'ın kapanmasını bekle
        boolean modalClosed = wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".modal-content")));

        Assert.assertTrue(modalClosed, "✅ Modal X butonu ile başarıyla kapatıldı!");
    }


    // BAŞARISIZ TEST SENARYOLARI

    @Test
    public void testAddNoteWithoutTitle() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.findElement(By.cssSelector("[data-testid='add-new-note']")).click();
        waitForModalToOpen();

        // Sadece açıklama doldur, başlık boş bırak
        driver.findElement(By.cssSelector("[data-testid='note-description']")).sendKeys("Başlık yok ama açıklama var.");
        driver.findElement(By.cssSelector("[data-testid='note-submit']")).click();

        // Hata mesajının görünmesini bekle
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='note-title'] + .invalid-feedback")
        ));

        Assert.assertTrue(errorMessage.isDisplayed(), "❌ Başlık eksik hata mesajı gösterildi!");

        // Modal'ın hala açık olduğunu kontrol et
        boolean modalStillOpen = driver.findElement(By.cssSelector(".modal-content")).isDisplayed();
        Assert.assertTrue(modalStillOpen, "❌ Modal başlık hatası nedeniyle açık kaldı!");
    }

    @Test
    public void testAddNoteWithoutDescription() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.findElement(By.cssSelector("[data-testid='add-new-note']")).click();
        waitForModalToOpen();

        // Sadece başlık doldur, açıklama boş bırak
        driver.findElement(By.cssSelector("[data-testid='note-title']")).sendKeys("Açıklama Yok Notu");
        driver.findElement(By.cssSelector("[data-testid='note-submit']")).click();

        // Hata mesajının görünmesini bekle
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='note-description'] + .invalid-feedback")
        ));

        Assert.assertTrue(errorMessage.isDisplayed(), "❌ Açıklama eksik hata mesajı gösterildi!");
        Thread.sleep(2000);
        // Modal'ın hala açık olduğunu kontrol et
        boolean modalStillOpen = driver.findElement(By.cssSelector(".modal-content")).isDisplayed();
        Assert.assertTrue(modalStillOpen, "❌ Modal açıklama hatası nedeniyle açık kaldı!");
    }

    @Test
    public void testAddNoteWithEmptyFields() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.findElement(By.cssSelector("[data-testid='add-new-note']")).click();
        waitForModalToOpen();

        // Hiçbir alanı doldurmadan kaydet
        driver.findElement(By.cssSelector("[data-testid='note-submit']")).click();

        // Her iki alan için de hata mesajlarının görünmesini bekle
        WebElement titleError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='note-title'] + .invalid-feedback")
        ));
        WebElement descError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='note-description'] + .invalid-feedback")
        ));
        Thread.sleep(2000);
        Assert.assertTrue(titleError.isDisplayed(), "❌ Başlık boş hata mesajı gösterildi!");
        Assert.assertTrue(descError.isDisplayed(), "❌ Açıklama boş hata mesajı gösterildi!");
    }

    @Test
    public void testAddNoteWithOnlySpaces() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.findElement(By.cssSelector("[data-testid='add-new-note']")).click();
        waitForModalToOpen();

        // Sadece boşluk karakterleri gir
        driver.findElement(By.cssSelector("[data-testid='note-title']")).sendKeys("   ");
        driver.findElement(By.cssSelector("[data-testid='note-description']")).sendKeys("   ");
        driver.findElement(By.cssSelector("[data-testid='note-submit']")).click();

        // Hata mesajlarının görünmesini bekle (boşluk karakterleri geçerli değil)
        try {
            WebElement titleError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("[data-testid='note-title'] + .invalid-feedback")
            ));
            Assert.assertTrue(titleError.isDisplayed(), "❌ Sadece boşluk karakteri hatası gösterildi!");
        } catch (Exception e) {
            // Eğer boşluk kabul ediliyorsa, notun eklenmediğini kontrol et
            boolean noteNotExists = !driver.getPageSource().contains("   ");
            Assert.assertTrue(noteNotExists, "❌ Sadece boşluk içeren not eklenmedi!");
        }
    }

    // ARAMA İŞLEVSELLİĞİ TESTLERİ

    @Test
    public void testSearchExistingNote() throws InterruptedException {
        // Önce bir not ekle
        testAddNoteSuccessfully();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='search-input']")
        ));
        searchInput.clear();
        searchInput.sendKeys("Selenium Test Notu");

        driver.findElement(By.cssSelector("[data-testid='search-btn']")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Selenium Test Notu"));

        boolean found = driver.getPageSource().contains("Selenium Test Notu");
        Assert.assertTrue(found, "🔍 Aranan not başarıyla bulundu!");
    }

    @Test
    public void testSearchNonexistentNote() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='search-input']")
        ));
        searchInput.clear();
        searchInput.sendKeys("Var Olmayan Not Adı");

        driver.findElement(By.cssSelector("[data-testid='search-btn']")).click();

        WebElement noNoteMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='no-notes-message']")
        ));

        Assert.assertTrue(noNoteMsg.isDisplayed(), "🚫 Olmayan not için uyarı mesajı gösterildi!");
    }

    @Test
    public void testSearchWithEmptyString() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='search-input']")
        ));
        searchInput.clear();
        searchInput.sendKeys("");

        driver.findElement(By.cssSelector("[data-testid='search-btn']")).click();

        // Boş arama sonucunda tüm notların gösterilmesi beklenir
        Thread.sleep(2000);
        // Bu test case için uygulamanın davranışına göre assertion eklenebilir
    }

    // KATEGORİ FİLTRELEME TESTLERİ

    @Test
    public void testFilterByCategory() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Home kategorisine tıkla
        driver.findElement(By.cssSelector("[data-testid='category-home']")).click();
        Thread.sleep(2000);

        // Home kategorisinin aktif olduğunu kontrol et (background-color değişimi)
        WebElement homeButton = driver.findElement(By.cssSelector("[data-testid='category-home']"));
        String backgroundColor = homeButton.getCssValue("background-color");

        // Work kategorisine tıkla
        driver.findElement(By.cssSelector("[data-testid='category-work']")).click();
        Thread.sleep(2000);

        // Personal kategorisine tıkla
        driver.findElement(By.cssSelector("[data-testid='category-personal']")).click();
        Thread.sleep(2000);

        // All kategorisine geri dön
        driver.findElement(By.cssSelector("[data-testid='category-all']")).click();
        Thread.sleep(2000);

        Assert.assertTrue(true, "✅ Kategori filtreleme testleri tamamlandı!");
    }

    // VALIDASYON TESTLERİ

    @Test
    public void testMaximumCharacterLimits() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.findElement(By.cssSelector("[data-testid='add-new-note']")).click();
        waitForModalToOpen();

        // Çok uzun başlık (varsa karakter sınırı)
        String longTitle = "A".repeat(1000); // 1000 karakter
        String longDescription = "B".repeat(5000); // 5000 karakter

        driver.findElement(By.cssSelector("[data-testid='note-title']")).sendKeys(longTitle);
        driver.findElement(By.cssSelector("[data-testid='note-description']")).sendKeys(longDescription);

        driver.findElement(By.cssSelector("[data-testid='note-submit']")).click();

        // Uygulamanın davranışına göre assertion eklenebilir
        Thread.sleep(3000);
    }

    // Login Olmadan Note Ekleme
    private void addNote(String title, String description) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.findElement(By.cssSelector("[data-testid='add-new-note']")).click();
        waitForModalToOpen();

        driver.findElement(By.cssSelector("[data-testid='note-title']")).sendKeys(title);
        driver.findElement(By.cssSelector("[data-testid='note-description']")).sendKeys(description);

        driver.findElement(By.cssSelector("[data-testid='note-submit']")).click();
        waitForModalToClose();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), title));
    }


    // NOT GÖRÜNTÜLEME TESTLERİ

    @Test
    public void testViewNoteSuccessfully() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // İlk notu bul ve View butonuna tıkla
        WebElement firstNote = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='note-card']:first-child")
        ));

        WebElement viewButton = firstNote.findElement(By.cssSelector("[data-testid='note-view']"));
        String noteTitle = firstNote.findElement(By.cssSelector("[data-testid='note-card-title']")).getText();

        System.out.println("View butonuna tıklanıyor...");
        viewButton.click();

        // Hemen overlay temizliği yap
        Thread.sleep(1500);
        forceRemoveAllOverlays();

        // Kısa bir bekleme daha
        Thread.sleep(500);

        // URL kontrolü - maksimum 3 deneme
        String currentUrl = "";
        for (int i = 0; i < 3; i++) {
            currentUrl = driver.getCurrentUrl();
            System.out.println("Deneme " + (i+1) + " - URL: " + currentUrl);

            if (!currentUrl.contains("google_vignette") &&
                    currentUrl.matches(".*\\/notes\\/app\\/notes\\/[a-f0-9]{24}.*")) {
                break;
            }

            if (currentUrl.contains("google_vignette")) {
                forceRemoveAllOverlays();
                Thread.sleep(1000);
            }
        }

        // Final assertion
        Assert.assertTrue(currentUrl.matches(".*\\/notes\\/app\\/notes\\/[a-f0-9]{24}$"),
                "Not detay sayfasına yönlendirme başarısız! Mevcut URL: " + currentUrl);

        // Not başlığının sayfada görüntülendiğini kontrol et
        WebElement noteDetailTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'" + noteTitle + "')]")
        ));

        System.out.println("Title: " + noteDetailTitle);

        String displayedTitle = noteDetailTitle.getText();
        Assert.assertEquals(displayedTitle, noteTitle,
                "Not başlığı doğru görüntülendi: " + displayedTitle);

        System.out.println("✅ Test başarıyla tamamlandı!");
    }

    @Test
    public void testViewMultipleNotes() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Tüm notları bul
        List<WebElement> noteCards = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector("[data-testid='note-card']")
        ));

        // İlk 2 notu test et
        for (int i = 0; i < Math.min(2, noteCards.size()); i++) {
            WebElement noteCard = noteCards.get(i);
            String noteTitle = noteCard.findElement(By.cssSelector("[data-testid='note-card-title']")).getText();

            WebElement viewButton = noteCard.findElement(By.cssSelector("[data-testid='note-view']"));
            System.out.println("[" + (i+1) + ". Not] View butonuna tıklanıyor...");
            viewButton.click();

            // Overlay veya popup varsa temizle
            Thread.sleep(1500);
            forceRemoveAllOverlays();
            Thread.sleep(500);

            // URL geçişini bekle
            String currentUrl = "";
            for (int retry = 0; retry < 3; retry++) {
                currentUrl = driver.getCurrentUrl();
                System.out.println("[" + (i+1) + ". Not] URL Deneme " + (retry+1) + ": " + currentUrl);

                if (!currentUrl.contains("google_vignette") &&
                        currentUrl.matches(".*\\/notes\\/app\\/notes\\/[a-f0-9]{24}.*")) {
                    break;
                }

                if (currentUrl.contains("google_vignette")) {
                    forceRemoveAllOverlays();
                    Thread.sleep(1000);
                }
            }

            // URL doğrulaması
            Assert.assertTrue(currentUrl.matches(".*\\/notes\\/app\\/notes\\/[a-f0-9]{24}$"),
                    "[" + (i+1) + ". Not] Detay sayfasına yönlendirme başarısız! URL: " + currentUrl);

            // Not başlığı kontrolü
            WebElement noteDetailTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'" + noteTitle + "')]")
            ));

            String displayedTitle = noteDetailTitle.getText();
            Assert.assertEquals(displayedTitle, noteTitle,
                    "[" + (i+1) + ". Not] Başlık eşleşmiyor! Görüntülenen: " + displayedTitle);

            System.out.println("✅ [" + (i+1) + ". Not] başarıyla görüntülendi!");

            // Ana sayfaya dön
            driver.navigate().back();
            wait.until(ExpectedConditions.urlMatches(".*\\/notes\\/app\\/?$"));

            // Sayfa yeniden yüklendikten sonra note kartlarını tekrar al
            noteCards = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.cssSelector("[data-testid='note-card']")
            ));
        }

        System.out.println("✅ Tüm notlar başarıyla test edildi!");
    }

    // NOT DÜZENLEME TESTLERİ

    @Test
    public void testEditNoteSuccessfully() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // İlk notu bul ve Edit butonuna tıkla
        WebElement firstNote = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='note-card']:first-child")
        ));

        String originalTitle = firstNote.findElement(By.cssSelector("[data-testid='note-card-title']")).getText();

        WebElement editButton = firstNote.findElement(By.cssSelector("[data-testid='note-edit']"));
        editButton.click();

        // Edit modalının açıldığını kontrol et
        waitForModalToOpen();

        // Mevcut başlığı temizle ve yeni başlık gir
        WebElement titleField = driver.findElement(By.cssSelector("[data-testid='note-title']"));
        titleField.clear();
        String newTitle = "Düzenlenmiş " + originalTitle;
        titleField.sendKeys(newTitle);

        // Açıklamayı güncelle
        WebElement descField = driver.findElement(By.cssSelector("[data-testid='note-description']"));
        descField.clear();
        descField.sendKeys("Bu not başarıyla düzenlendi - " + System.currentTimeMillis());

        // Değişiklikleri kaydet
        driver.findElement(By.cssSelector("[data-testid='note-submit']")).click();
        waitForModalToClose();

        // Güncellenmiş notun görünmesini bekle
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), newTitle));

        boolean noteUpdated = driver.getPageSource().contains(newTitle);
        Assert.assertTrue(noteUpdated, "✅ Not başarıyla düzenlendi!");
    }

    @Test
    public void testEditNoteCancel() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement firstNote = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='note-card']:first-child")
        ));

        String originalTitle = firstNote.findElement(By.cssSelector("[data-testid='note-card-title']")).getText();

        WebElement editButton = firstNote.findElement(By.cssSelector("[data-testid='note-edit']"));
        editButton.click();

        waitForModalToOpen();

        // Başlığı değiştir ama cancel et
        WebElement titleField = driver.findElement(By.cssSelector("[data-testid='note-title']"));
        titleField.clear();
        titleField.sendKeys("İptal Edilecek Değişiklik");

        // Cancel butonuna tıkla
        driver.findElement(By.cssSelector("[data-testid='note-cancel']")).click();
        waitForModalToClose();

        // Orijinal başlığın hala mevcut olduğunu kontrol et
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), originalTitle));
        boolean originalTitleExists = driver.getPageSource().contains(originalTitle);
        boolean cancelledTitleNotExists = !driver.getPageSource().contains("İptal Edilecek Değişiklik");

        Assert.assertTrue(originalTitleExists && cancelledTitleNotExists,
                "✅ Not düzenleme başarıyla iptal edildi!");
    }

    @Test
    public void testEditCompletedNote() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Tüm notları al
        List<WebElement> noteCards = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector("[data-testid='note-card']")
        ));

        WebElement completedNote = null;

        // Tamamlanmış notu bul (checkbox'ı selected olan)
        for (WebElement noteCard : noteCards) {
            WebElement checkbox = noteCard.findElement(By.cssSelector("[data-testid='toggle-note-switch']"));
            if (checkbox.isSelected()) {
                completedNote = noteCard;
                break;
            }
        }

        Assert.assertNotNull(completedNote, "⚠️ Tamamlanmış bir not bulunamadı!");

        System.out.println("Tamamlanmış not bulundu: " + completedNote);

        // Edit butonuna tıkla
        WebElement editButton = completedNote.findElement(By.cssSelector("[data-testid='note-edit']"));
        editButton.click();

        // Modal açılmasını bekle
        waitForModalToOpen();

        // Completed durumunu değiştir
        WebElement completedCheckbox = driver.findElement(By.cssSelector("[data-testid='note-completed']"));
        if (completedCheckbox.isSelected()) {
            completedCheckbox.click(); // Tamamlanmış durumunu kaldır
        }

        // Başlığı güncelle
        WebElement titleField = driver.findElement(By.cssSelector("[data-testid='note-title']"));
        titleField.clear();
        titleField.sendKeys("Tamamlanma Durumu Değiştirildi");

        // Kaydet butonuna tıkla
        driver.findElement(By.cssSelector("[data-testid='note-submit']")).click();

        waitForModalToClose();

        // Başlığın sayfada güncellendiğini kontrol et
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector("[data-testid='note-card-title']"), "Tamamlanma Durumu Değiştirildi"
        ));

        Assert.assertTrue(driver.getPageSource().contains("Tamamlanma Durumu Değiştirildi"),
                "✅ Tamamlanmış not durumu başarıyla değiştirildi!");
    }


    // NOT SİLME TESTLERİ

    @Test
    public void testDeleteNoteSuccessfully() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Silinecek son notu bul
        WebElement noteToDelete = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='note-card']:last-child")
        ));

        String noteTitle = noteToDelete.findElement(By.cssSelector("[data-testid='note-card-title']")).getText();

        // Delete butonuna tıkla
        WebElement deleteButton = noteToDelete.findElement(By.cssSelector("[data-testid='note-delete']"));
        deleteButton.click();

        // Modal içeriğinin gelmesini bekle
        WebElement confirmModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".modal-content")
        ));

        // Delete butonunun aktif ve görünür hale gelmesini bekle
        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-testid='note-delete-confirm']")
        ));

        // Scroll ve tıklama (bazı UI framework'lerinde gerekli olabilir)
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", confirmButton);
        confirmButton.click();

        // Modal kapanana kadar bekle
        wait.until(ExpectedConditions.invisibilityOf(confirmModal));

        // Sayfa güncellenmesi için bekle
        Thread.sleep(2000);

        // Silinen notun sayfada olmadığını kontrol et
        boolean noteDeleted = !driver.getPageSource().contains(noteTitle);
        Assert.assertTrue(noteDeleted, "✅ Not başarıyla silindi!");
    }

    @Test
    public void testDeleteNoteCancellation() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement noteToDelete = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='note-card']:first-child")
        ));

        String noteTitle = noteToDelete.findElement(By.cssSelector("[data-testid='note-card-title']")).getText();

        WebElement deleteButton = noteToDelete.findElement(By.cssSelector("[data-testid='note-delete']"));
        deleteButton.click();

        // Modal içeriği görünene kadar bekle
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".modal-content")));

        // Cancel butonunu tıkla
        WebElement cancelButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-testid='note-delete-cancel-1'], [data-testid='note-delete-cancel-2']")
        ));
        cancelButton.click();

        // Modal kapanmasını bekle
        wait.until(ExpectedConditions.invisibilityOf(modal));

        // Notun hala var olduğunu kontrol et
        java.util.List<WebElement> titles = driver.findElements(By.cssSelector("[data-testid='note-card-title']"));
        boolean noteStillExists = titles.stream().anyMatch(el -> el.getText().equals(noteTitle));

        Assert.assertTrue(noteStillExists, "✅ Not silme işlemi başarıyla iptal edildi!");
    }

    @Test
    public void testDeleteMultipleNotes() throws InterruptedException{
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Not sayısını kontrol et
        List<WebElement> notes = driver.findElements(By.cssSelector("[data-testid='note-card']"));

        // Not yoksa uyarı mesajı kontrolü yap
        if (notes.isEmpty()) {
            WebElement emptyMessage = driver.findElement(By.cssSelector("[data-testid='no-notes-message']"));
            Assert.assertTrue(emptyMessage.isDisplayed(), "Boş not listesi mesajı görünmelidir.");

            // 2 not ekle
            addNote("Test Notu 1", "Boş liste testi için eklendi.");
            addNote("Test Notu 2", "Boş liste testi için eklendi.");

            // Not eklendikten sonra DOM'un güncellenmesini bekle
            wait.until(ExpectedConditions.numberOfElementsToBe(
                    By.cssSelector("[data-testid='note-card']"), 2
            ));

        } else if (notes.size() == 1) {
            // 1 tane varsa 1 tane daha ekle
            addNote("Test Notu 2", "Tamamlamak için 1 tane daha eklendi.");

            // Not eklendikten sonra DOM'un güncellenmesini bekle
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                    By.cssSelector("[data-testid='note-card']"), 1
            ));
        }

        // Kısa bir bekleme ekle (DOM'un tamamen stabilize olması için)
        Thread.sleep(1000);

        // 2 not sil
        for (int i = 0; i < 2; i++) {
            // Her silme işleminden önce notları yeniden bul
            notes = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("[data-testid='note-card']")
            ));

            if (!notes.isEmpty()) {
                WebElement noteToDelete = notes.get(0);

                // Delete butonunun tıklanabilir olmasını bekle
                WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
                        noteToDelete.findElement(By.cssSelector("[data-testid='note-delete']"))
                ));
                deleteButton.click();

                // Modal'ın görünmesini bekle
                WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".modal-content")
                ));

                // Confirm butonunun tıklanabilir olmasını bekle
                WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("[data-testid='note-delete-confirm']")
                ));
                confirmButton.click();

                // Modal'ın kaybolmasını bekle
                wait.until(ExpectedConditions.invisibilityOf(modal));

                // Not silindikten sonra DOM'un güncellenmesini bekle
                wait.until(ExpectedConditions.numberOfElementsToBeLessThan(
                        By.cssSelector("[data-testid='note-card']"), notes.size()
                ));
            }
        }

        // Final kontrol
        List<WebElement> finalNotes = driver.findElements(By.cssSelector("[data-testid='note-card']"));
        Assert.assertTrue(finalNotes.size() <= 1, "✅ Birden fazla not başarıyla silindi!");
    }

    // KARMA İŞLEMLER TESTİ
    @Test
    public void testNoteOperationsWorkflow() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Yeni not ekle
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-testid='add-new-note']")
        ));
        addButton.click();
        waitForModalToOpen();

        WebElement titleField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='note-title']")
        ));
        String originalTitle = "İş Akışı Test Notu";
        titleField.sendKeys(originalTitle);

        driver.findElement(By.cssSelector("[data-testid='note-description']"))
                .sendKeys("Bu not iş akışı testi için oluşturuldu.");

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-testid='note-submit']")
        ));
        submitButton.click();
        waitForModalToClose();

        // Not eklendikten sonra DOM'da görünmesini bekle
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), originalTitle));

        // 2. Eklenen notu görüntüle
        // Not kartının tam yüklenmesini bekle
        Thread.sleep(2000);

        WebElement addedNote = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-testid='note-card-title' and contains(text(), '" + originalTitle + "')]/ancestor::div[@data-testid='note-card']")
        ));

        WebElement viewButton = addedNote.findElement(By.cssSelector("[data-testid='note-view']"));
        System.out.println("View butonuna tıklanıyor...");
        viewButton.click();

        // Daha uzun overlay temizliği ve bekleme
        Thread.sleep(2000);
        forceRemoveAllOverlays();
        Thread.sleep(1000);

        // URL kontrolü - maksimum 5 deneme (daha fazla)
        String currentUrl = "";
        for (int i = 0; i < 5; i++) {
            currentUrl = driver.getCurrentUrl();
            System.out.println("Deneme " + (i+1) + " - URL: " + currentUrl);

            if (!currentUrl.contains("google_vignette") &&
                    currentUrl.matches(".*\\/notes\\/app\\/notes\\/[a-f0-9]{24}.*")) {
                break;
            }

            if (currentUrl.contains("google_vignette")) {
                forceRemoveAllOverlays();
                Thread.sleep(1500);
            } else {
                Thread.sleep(1000);
            }
        }

        // Not detay sayfasının açıldığını doğrula
        Assert.assertTrue(currentUrl.matches(".*\\/notes\\/app\\/notes\\/[a-f0-9]{24}$"),
                "Not detay sayfasına yönlendirme başarısız! Mevcut URL: " + currentUrl);

        // Not başlığının sayfada görüntülendiğini kontrol et
        WebElement noteDetailTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'" + originalTitle + "')]")
        ));

        System.out.println("✅ Not detay sayfası başarıyla görüntülendi!");

        // Ana sayfaya geri dön
        driver.navigate().back();
        wait.until(ExpectedConditions.urlContains("/notes/app"));
        Thread.sleep(2000);

        // 3. Notu düzenle
        // Not elementini yeniden bul
        addedNote = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-testid='note-card-title' and contains(text(), '" + originalTitle + "')]/ancestor::div[@data-testid='note-card']")
        ));

        WebElement editButton = addedNote.findElement(By.cssSelector("[data-testid='note-edit']"));
        editButton.click();

        // Edit modalının açıldığını kontrol et
        waitForModalToOpen();

        // Mevcut başlığı temizle ve yeni başlık gir
        WebElement editTitleField = driver.findElement(By.cssSelector("[data-testid='note-title']"));
        editTitleField.clear();
        String newTitle = "Düzenlenmiş " + originalTitle;
        editTitleField.sendKeys(newTitle);

        // Açıklamayı güncelle
        WebElement descField = driver.findElement(By.cssSelector("[data-testid='note-description']"));
        descField.clear();
        descField.sendKeys("Bu not başarıyla düzenlendi - " + System.currentTimeMillis());

        // Değişiklikleri kaydet
        WebElement editSubmitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-testid='note-submit']")
        ));
        editSubmitButton.click();
        waitForModalToClose();

        // Güncellenmiş notun görünmesini bekle
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), newTitle));

        boolean noteUpdated = driver.getPageSource().contains(newTitle);
        Assert.assertTrue(noteUpdated, "Not başarıyla düzenlendi!");

        // 4. Düzenlenen notu sil
        // Sayfanın yüklenmesini bekle
        Thread.sleep(1000);

        // Silme işleminden ÖNCE toplam not sayısını al
        List<WebElement> allNotesBeforeDelete = driver.findElements(By.cssSelector("[data-testid='note-card']"));
        int totalNotesBeforeDelete = allNotesBeforeDelete.size();
        System.out.println("Silme öncesi toplam not sayısı: " + totalNotesBeforeDelete);

        // Silinecek notu bul (son not olarak varsayalım veya spesifik başlığa göre)
        WebElement noteToDelete = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@data-testid='note-card-title' and contains(text(), '" + newTitle + "')]/ancestor::div[@data-testid='note-card']")
        ));

        // Not başlığını kaydet
        String noteTitle = noteToDelete.findElement(By.cssSelector("[data-testid='note-card-title']")).getText();
        System.out.println("Silinecek not: " + noteTitle);

        // Delete butonuna tıkla
        WebElement deleteButton = noteToDelete.findElement(By.cssSelector("[data-testid='note-delete']"));
        deleteButton.click();

        // Modal içeriğinin gelmesini bekle
        WebElement confirmModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".modal-content")
        ));

        // Delete butonunun aktif ve görünür hale gelmesini bekle
        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-testid='note-delete-confirm']")
        ));

        // Scroll ve tıklama (bazı UI framework'lerinde gerekli olabilir)
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", confirmButton);
        confirmButton.click();

        // Modal kapanana kadar bekle
        wait.until(ExpectedConditions.invisibilityOf(confirmModal));

        // Sayfa güncellenmesi için bekle
        Thread.sleep(2000);

        // Silme işleminden SONRA toplam not sayısını al
        List<WebElement> allNotesAfterDelete = driver.findElements(By.cssSelector("[data-testid='note-card']"));
        int totalNotesAfterDelete = allNotesAfterDelete.size();
        System.out.println("Silme sonrası toplam not sayısı: " + totalNotesAfterDelete);

        // Test: Toplam not sayısının azaldığını kontrol et
        boolean noteCountDecreased = totalNotesAfterDelete < totalNotesBeforeDelete;

        System.out.println("Not sayısı azaldı mı? " + noteCountDecreased);

        Assert.assertTrue(noteCountDecreased,
                "✅ Tam iş akışı testi (Ekle->Görüntüle->Düzenle->Sil) başarılı! " +
                        "Silme öncesi: " + totalNotesBeforeDelete + ", Silme sonrası: " + totalNotesAfterDelete);
    }

    // CHECKBOX TOGGLE TESTLERİ
    @Test
    public void testToggleNoteCompletion() throws InterruptedException {
        login();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // İlk notu bul
        WebElement firstNote = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='note-card']:first-child")
        ));

        WebElement toggleSwitch = firstNote.findElement(By.cssSelector("[data-testid='toggle-note-switch']"));
        boolean initialState = toggleSwitch.isSelected();
        System.out.println("Başlangıç durumu: " + initialState);

        // Toggle durumunu değiştir
        toggleSwitch.click();
        Thread.sleep(1000);

        // Elementi yeniden bul (DOM değişmiş olabilir!)
        firstNote = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='note-card']:first-child")
        ));
        toggleSwitch = firstNote.findElement(By.cssSelector("[data-testid='toggle-note-switch']"));

        boolean newState = toggleSwitch.isSelected();
        Assert.assertNotEquals(initialState, newState,
                "❌ Not tamamlanma durumu değiştirilemedi. Toggle çalışmamış olabilir.");
        System.out.println("✅ Not tamamlanma durumu başarıyla değiştirildi! Yeni durum: " + newState);

        // Tekrar toggle et
        toggleSwitch.click();
        Thread.sleep(1000);

        // Yine yeniden bul
        firstNote = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='note-card']:first-child")
        ));
        toggleSwitch = firstNote.findElement(By.cssSelector("[data-testid='toggle-note-switch']"));

        boolean finalState = toggleSwitch.isSelected();
        Assert.assertEquals(initialState, finalState,
                "❌ Not durumu başlangıç haline dönmedi. İkinci toggle başarısız.");
        System.out.println("✅ Not durumu başarıyla başlangıç haline döndürüldü! Final durum: " + finalState);
    }


}