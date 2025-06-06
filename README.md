# Yazılım Kalite Güvence Projesi: Web UI Test Otomasyonu

Bu proje, **ExpandTesting** adlı demo web platformu üzerinde gerçekleştirilen **UI otomasyon testlerini** içermektedir. Proje, Marmara Üniversitesi Teknoloji Fakültesi Bilgisayar Mühendisliği Bölümü'nde **Yazılım Kalite ve Güvence Temelleri** dersi kapsamında hazırlanmıştır.

Testler, gerçek dünya senaryolarına benzer koşullar altında gerçekleştirilmiştir. Bu kapsamda, **reklamlar, pop-up’lar, overlay katmanlar** gibi dışsal etkenlerin test sonuçlarını etkilememesi için özel bir **reklam engelleme altyapısı** geliştirilmiştir. Bu yapı, `BaseTest` sınıfı içerisinde JavaScript ve CSS temelli temizleme işlemleriyle uygulanmış, testlerin daha **kararlı, güvenilir ve tekrarlanabilir** hale gelmesi sağlanmıştır.

## 🎯 Amaç

Bu çalışmanın temel amacı, gerçek dünyadaki web uygulamalarını simüle eden bir platformda **fonksiyonel test otomasyonu** uygulamaktır. Hedeflenen kazanımlar:

- UI test senaryolarının hazırlanması ve yürütülmesi  
- Selenium + TestNG gibi araçlarla otomasyon framework'ü kurulumu  
- Pozitif ve negatif test senaryolarının uygulanması  
- Yazılım güvenliği ve kullanıcı deneyimi üzerine analizlerin yapılması  
- Temel test stratejilerinin uygulanması ve sonuçlarının değerlendirilmesi

## 🛠 Kullanılan Teknolojiler

- **Selenium WebDriver** – UI test otomasyonu için
- **Java** – Test senaryolarının yazımı
- **TestNG** – Test yönetimi, grup yapısı ve paralel test çalıştırma
- **WebDriverWait** – Dinamik bekleme işlemleri
- **JavaScriptExecutor** – Özel scroll ve JS komutları için
- **Maven** – Bağımlılık yönetimi (opsiyonel)

## 🧪 Test Edilen Modüller

ExpandTesting platformundaki şu bölümler test edilmiştir:

1. **Giriş (Login) Ekranı**
   - Geçerli ve geçersiz giriş denemeleri
   - Form validasyonları
   - Google ve LinkedIn OAuth kontrolleri
2. **Kayıt (Register) Ekranı**
   - Başarılı kayıt işlemleri
   - Script injection, boş alan, format kontrolü gibi negatif testler
3. **Not Uygulaması (Note App)**
   - Not ekleme, düzenleme, silme işlemleri
   - Arama, filtreleme ve kategori bazlı işlemler
   - Not tamamlanma toggle, uzun içerik, boşluk validasyonu
   - Tam yaşam döngüsü testi (Ekle-Görüntüle-Düzenle-Sil)

## 🔧 Projeyi Çalıştırma

1. Java ve Maven yüklü olmalıdır.
2. Proje klasöründe aşağıdaki komutu çalıştırarak testleri başlatabilirsiniz:

```bash
mvn clean test
