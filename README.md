# Laporan Commit & Riwayat Perubahan - Week 9
**Proyek:** SoundVault (`Proj_SoundVault`)  
**Pengembang:** Fasya Tri.N (`fasyanovember@gmail.com`)  
**Tanggal Ringkasan:** 9 November 2026  

---

## 📌 Ringkasan Eksekutif Week 9

Pada Week 9, pengerjaan proyek **SoundVault** berfokus pada:
1. Inisialisasi dasar proyek Android dengan Jetpack Compose (`Initial commit`).
2. Konfigurasi dependensi awal (Navigation, Retrofit, Gson Converter, Lifecycle ViewModel).
3. Penerapan Arsitektur **MVVM (Model-View-ViewModel)** & **Repository Pattern**:
   - Model Data (`AudioBrand`).
   - Mock Data & Remote API (`MockSessionManager`, `ApiService`, `RetrofitClient`).
   - Data Repository (`BrandRepository`).
   - UI State Management (`BrandViewModel`).
   - User Interface (`BrandCatalogScreen` dengan Jetpack Compose).
4. Penyesuaian `MainActivity` untuk menampilkan halaman katalog brand.
5. Pembaruan `compileSdk` ke versi 37.

---

## 📜 Daftar Riwayat Commit

| No | Hash Commit (Short) | Hash Commit (Full) | Pesan Commit | Tanggal & Waktu |
|---|---|---|---|---|
| 1 | `d23ed2f` | `d23ed2fdabadcbc78fae4c387d0ca2674630bf60` | Memperbarui `compileSdk` ke versi 37 | Mon Nov 9 15:17:37 2026 +0700 |
| 2 | `d22c856` | `d22c856dc568370054ac695c5624ba62a2b3861e` | Mengganti UI Greeting default dengan BrandCatalogScreen pada MainActivity | Mon Nov 9 15:16:08 2026 +0700 |
| 3 | `4bf59a6` | `4bf59a67f158e8e3af3f2ce63254b0084fad1505` | Menambahkan UI katalog brand beserta ViewModel dan Repository | Mon Nov 9 15:15:23 2026 +0700 |
| 4 | `b835b12` | `b835b128ffae918f75b705816317243dc4655c34` | Menambahkan `RetrofitClient` untuk inisialisasi `ApiService` | Mon Nov 9 15:12:37 2026 +0700 |
| 5 | `d6a06cc` | `d6a06cc92a1db325127a6c0ea15953d026783bbc` | Menambahkan interface untuk ApiService dan melengkapi model AudioBrand | Mon Nov 9 15:11:13 2026 +0700 |
| 6 | `dc60236` | `dc602361fe6b45bd18a94b235368adb4003ae296` | menambah MockSessionManager dan AudioBrand model | Mon Nov 9 15:01:01 2026 +0700 |
| 7 | `aa1198e` | `aa1198ee64abec9041e214307af5f149f3f8ac3f` | Menambahkan dependensi Navigation dan Retrofit serta file navigasi awal | Mon Nov 9 14:51:01 2026 +0700 |
| 8 | `5955072` | `59550728963a17dd525b8b692e6ea44b2c58b828` | Initial commit | Mon Nov 9 14:43:24 2026 +0700 |

---

## 🔍 Detail Perubahan Per Commit

### 1. Commit `d23ed2fdabadcbc78fae4c387d0ca2674630bf60`
* **Pesan Commit:** `Memperbarui compileSdk ke versi 37`
* **Deskripsi Perubahan:** Memperbarui konfigurasi `compileSdk` dari versi 35/terdahulu menjadi 37 pada konfigurasi Gradle aplikasi untuk menyelaraskan dengan SDK Android terbaru.
* **File Diubah:**
  - `app/build.gradle.kts` (+1, -1)

---

### 2. Commit `d22c856dc568370054ac695c5624ba62a2b3861e`
* **Pesan Commit:** `Mengganti UI Greeting default dengan BrandCatalogScreen pada MainActivity`
* **Deskripsi Perubahan:** Menghapus composable `Greeting` standar dari wizard bawaan Android Studio dan menggantikannya dengan penanganan UI utama menggunakan `BrandCatalogScreen()`.
* **File Diubah:**
  - `app/src/main/java/com/example/proj_soundvault/MainActivity.kt` (+5, -30)

---

### 3. Commit `4bf59a67f158e8e3af3f2ce63254b0084fad1505`
* **Pesan Commit:** `Menambahkan UI katalog brand beserta ViewModel dan Repository`
* **Deskripsi Perubahan:** Implementasi komponen utama arsitektur MVVM:
  - `BrandRepository`: Mengelola pemanggilan API / pencarian data brand audio.
  - `BrandViewModel`: Mengelola UI state (loading, sukses, error) dan aksi pencarian.
  - `BrandCatalogScreen`: Layar antarmuka katalog brand berbasis Jetpack Compose yang menampilkan daftar brand audio beserta fitur pencarian.
* **File Diubah:**
  - `app/src/main/java/com/example/proj_soundvault/data/repository/BrandRepository.kt` (+26)
  - `app/src/main/java/com/example/proj_soundvault/ui/screen/brand/BrandCatalogScreen.kt` (+148)
  - `app/src/main/java/com/example/proj_soundvault/ui/viewmodel/BrandViewModel.kt` (+54)

---

### 4. Commit `b835b128ffae918f75b705816317243dc4655c34`
* **Pesan Commit:** `Menambahkan RetrofitClient untuk inisialisasi ApiService`
* **Deskripsi Perubahan:** Membuat singleton `RetrofitClient` untuk mengonfigurasi instance Retrofit dengan Base URL dan Converter Factory.
* **File Diubah:**
  - `app/src/main/java/com/example/proj_soundvault/data/remote/RetrofitClient.kt` (+17)

---

### 5. Commit `d6a06cc92a1db325127a6c0ea15953d026783bbc`
* **Pesan Commit:** `Menambahkan interface untuk ApiService dan melengkapi model AudioBrand`
* **Deskripsi Perubahan:** 
  - Mendefinisikan interface `ApiService` dengan endpoint REST API yang dibutuhkan.
  - Melengkapi properti pada data class `AudioBrand`.
* **File Diubah:**
  - `app/src/main/java/com/example/proj_soundvault/data/model/AudioBrand.kt` (+6)
  - `app/src/main/java/com/example/proj_soundvault/data/remote/ApiService.kt` (+15)

---

### 6. Commit `dc602361fe6b45bd18a94b235368adb4003ae296`
* **Pesan Commit:** `menambah MockSessionManager dan AudioBrand model`
* **Deskripsi Perubahan:** Menambahkan model dasar `AudioBrand` dan kelas helper `MockSessionManager` untuk simulasi sesi pengguna.
* **File Diubah:**
  - `app/src/main/java/com/example/proj_soundvault/data/model/AudioBrand.kt` (+2)
  - `app/src/main/java/com/example/proj_soundvault/data/remote/MockSessionManager.kt` (+16)

---

### 7. Commit `aa1198ee64abec9041e214307af5f149f3f8ac3f`
* **Pesan Commit:** `Menambahkan dependensi Navigation dan Retrofit serta file navigasi awal`
* **Deskripsi Perubahan:**
  - Menambahkan dependensi Gradle untuk Navigation Compose, Retrofit 2, Gson Converter, Lifecycle ViewModel, dan Material Icons Extended.
  - Membuat *stub* awal `NavGraph.kt` dan `Screen.kt`.
  - Penyesuaian nama proyek di file `.idea/.name`.
* **File Diubah:**
  - `app/build.gradle.kts` (+11)
  - `app/src/main/java/com/example/proj_soundvault/navigation/NavGraph.kt` (+2)
  - `app/src/main/java/com/example/proj_soundvault/navigation/Screen.kt` (+2)
  - `.idea/.name` (+1)
  - `.idea/misc.xml` (+1, -1)

---

### 8. Commit `59550728963a17dd525b8b692e6ea44b2c58b828`
* **Pesan Commit:** `Initial commit`
* **Deskripsi Perubahan:** Inisialisasi awal proyek Android Studio (SoundVault) dengan struktur Jetpack Compose bawaan.
* **File Diubah:**
  - 48 file awal (skrip Gradle, AndroidManifest, MainActivity awal, Theme, Drawable, Mipmap resources, dll).

---
