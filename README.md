# Laporan Riwayat Perubahan
**Proyek:** SoundVault

**Navigasi:** [SRS Revision History](#-srs-revision-history) | [Week 11](#-week-11) | [Arsip Mingguan (Week 10 & Week 9)](#-arsip-mingguan-sebelumnya)

---

## 📄 SRS Revision History

Riwayat revisi dokumen **System Requirement Specification (SRS)** proyek SoundVault:

| Date | Version | Description | Author(s) |
|---|---|---|---|
| 13/11/2026 | 0.1 | Draft Version Inisialisasi Dokumen SRS | Fasya Tri Nugroho |
| 14/11/2026 | 0.2 | Penambahan Bab 2 (Overall Description), Entity Relationship Diagram (ERD), dan Relasi Antar Tabel (RAT) | Fasya Tri Nugroho |
| 15/11/2026 | 0.3 | Penambahan Use Case Diagram, tabel deskripsi Use Case, dan Flow Diagram untuk setiap modul fitur utama pada Bab 3 | Fasya Tri Nugroho |
| 16/11/2026 | 0.4 | Penyesuaian urutan penomoran Bab 3 serta penambahan spesifikasi lengkap Subbab 3.2 (Autentikasi & Manajemen Sesi), Flow Diagram, dan 4 Activity Diagram 3 Swimlane | Fasya Tri Nugroho |
| 17/11/2026 | 0.5 | Penambahan spesifikasi lengkap Subbab 3.3 (Manajemen Katalog Merek Audio / AudioBrand), Flow Diagram, dan 4 Activity Diagram 3 Swimlane | Fasya Tri Nugroho |
| 18/11/2026 | 0.6 | Penambahan spesifikasi lengkap Subbab 3.4 (Manajemen Listing Audio & Pencarian Multi-Filter / ListingItem), Flow Diagram, dan 4 Activity Diagram 3 Swimlane | Fasya Tri Nugroho |
| 19/11/2026 | 0.7 | Penambahan spesifikasi lengkap Subbab 3.5 (Sistem Penawaran & Negosiasi Harga / TransactionOffer), Flow Diagram, dan 4 Activity Diagram 3 Swimlane | Fasya Tri Nugroho |
| 20/11/2026 | 0.8 | Penambahan spesifikasi lengkap Subbab 3.6 (Manajemen Wishlist & Ekspor Kwitansi Lokal), Flow Diagram, dan 4 Activity Diagram 3 Swimlane | Fasya Tri Nugroho |
| 21/11/2026 | 0.9 | Penambahan spesifikasi lengkap Bab 4 (External Interface Requirements: User, Hardware, Software, dan Communications Interfaces) | Fasya Tri Nugroho |
| 22/11/2026 | 1.0 | Penambahan Bab 5 (Other Nonfunctional Requirements), Bab 6 (Other Requirements), dan Appendix A–B (Glossary & Traceability Matrix) - Draft Lengkap SRS | Fasya Tri Nugroho |

---

## 📌 Week 11

### Ringkasan Eksekutif Week 11

Pada Week 11, pengerjaan proyek **SoundVault** berfokus pada pengembangan modul **Manajemen Listing Audio & Pencarian Multi-Filter (`ListingItem`)** pada sisi backend (`soundvault-api`) serta finalisasi riwayat revisi SRS hingga versi `1.0`:
1. **Finalisasi Tabel Revisi SRS (`README.md`)**: Pembaruan riwayat dokumen SRS hingga versi `1.0` (Subbab 3.6, Bab 4, Bab 5, Bab 6, serta Appendix A–B).
2. **Pembangunan `listingController.js` & Pencarian Multi-Filter**:
   - Penambahan konstanta validasi untuk kategori (`IEM`, `DAC_AMP`, `DAP`, `HEADPHONE`, `TWS`, `CABLE_ACCESSORY`), kondisi barang, dan status ketersediaan.
   - Pembuatan query SQL listing barang dengan `JOIN` ke tabel `audio_brands` dan `users`.
   - Implementasi fitur pencarian dan *multi-filter* pada fungsi `getAllListings` serta pengambilan detail produk via `getListingById`.
   - Implementasi fungsi CRUD (`createListing`, `updateListing`, `deleteListing`) dengan validasi input dan kontrol kepemilikan (*ownership control*).
3. **Konfigurasi Rute & Integrasi Endpoint Listing (`listingRoutes.js`, `server.js`)**:
   - Pembuatan endpoint REST API listing dengan proteksi middleware verifikasi token (`verifyToken`) dan pendaftaran rute pada `server.js`.
4. **Data Awal (*Seed*) Listing Item (`schema.sql`)**:
   - Penambahan data awal produk *listing item* ke dalam skema database `soundvault-api/src/schema.sql`.

<details>
<summary><b>📜 Daftar Riwayat Commit — Week 11 (9 Commit)</b></summary>

| No | Hash Commit | Pesan Commit | Tanggal & Waktu |
|---|---|---|---|
| 1 | `0518614` | menambah data awal untuk listing item | Mon Nov 23 22:04:46 2026 +0700 |
| 2 | `7016b02` | Add listingRoutes ke server untuk integrasi endpoint API | Mon Nov 23 22:03:51 2026 +0700 |
| 3 | `d421d6e` | Add listing routes dengan CRUD operations dan token verification | Mon Nov 23 22:02:55 2026 +0700 |
| 4 | `b311219` | Add createListing, updateListing, dan deleteListing functions dengan validation dan ownership control | Mon Nov 23 22:00:09 2026 +0700 |
| 5 | `977aec2` | Add getListingById function untuk mendapatkan detail audio product | Mon Nov 23 21:52:09 2026 +0700 |
| 6 | `0f682b1` | implementasi fungsi multi-filter untuk list audio dalam getAllListings | Mon Nov 23 21:47:57 2026 +0700 |
| 7 | `396f379` | Add SQL query untuk listing barang dengan joins dari audio_brands dan users | Mon Nov 23 21:42:04 2026 +0700 |
| 8 | `7bda50d` | Add listingController dengan validasi constant untuk kategori, kondisi, dan status | Mon Nov 23 21:40:49 2026 +0700 |
| 9 | `8d353cf` | Update version history in README.md | Mon Nov 23 08:19:24 2026 +0700 |

</details>

<details>
<summary><b>🔍 Detail Perubahan Per Commit — Week 11</b></summary>

#### 9. Commit `0518614`
* **Pesan Commit:** `menambah data awal untuk listing item`
* **Deskripsi Perubahan:** Menambahkan data awal (*seed*) untuk tabel listing item audio di dalam skema SQL database.
* **File Diubah:**
  - `soundvault-api/src/schema.sql` (+62, -1)

---

#### 8. Commit `7016b02`
* **Pesan Commit:** `Add listingRoutes ke server untuk integrasi endpoint API`
* **Deskripsi Perubahan:** Mendaftarkan rute `listingRoutes` ke dalam `server.js` agar endpoint API listing dapat diakses.
* **File Diubah:**
  - `soundvault-api/server.js` (+2)

---

#### 7. Commit `d421d6e`
* **Pesan Commit:** `Add listing routes dengan CRUD operations dan token verification`
* **Deskripsi Perubahan:** Menambahkan definisi rute endpoint CRUD untuk modul listing produk audio yang dilengkapi middleware verifikasi token.
* **File Diubah:**
  - `soundvault-api/routes/listingRoutes.js` (+20)

---

#### 6. Commit `b311219`
* **Pesan Commit:** `Add createListing, updateListing, dan deleteListing functions dengan validation dan ownership control`
* **Deskripsi Perubahan:** Mengimplementasikan fungsi pembuatan, pembaruan, dan penghapusan listing produk audio beserta validasi input dan pengecekan kepemilikan barang (*ownership control*).
* **File Diubah:**
  - `soundvault-api/controllers/listingController.js` (+370)

---

#### 5. Commit `977aec2`
* **Pesan Commit:** `Add getListingById function untuk mendapatkan detail audio product`
* **Deskripsi Perubahan:** Menambahkan handler `getListingById` pada `listingController.js` untuk mengambil informasi detail satu produk audio berdasarkan ID.
* **File Diubah:**
  - `soundvault-api/controllers/listingController.js` (+37)

---

#### 4. Commit `0f682b1`
* **Pesan Commit:** `implementasi fungsi multi-filter untuk list audio dalam getAllListings`
* **Deskripsi Perubahan:** Mengimplementasikan dukungan pencarian dan *multi-filter* (berdasarkan kata kunci, merek, kategori, kondisi, rentang harga, dan status) pada fungsi `getAllListings`.
* **File Diubah:**
  - `soundvault-api/controllers/listingController.js` (+94, -1)

---

#### 3. Commit `396f379`
* **Pesan Commit:** `Add SQL query untuk listing barang dengan joins dari audio_brands dan users`
* **Deskripsi Perubahan:** Menambahkan query SQL dasar untuk mengambil daftar listing barang beserta relasi `JOIN` dari tabel `audio_brands` dan `users`.
* **File Diubah:**
  - `soundvault-api/controllers/listingController.js` (+25, -1)

---

#### 2. Commit `7bda50d`
* **Pesan Commit:** `Add listingController dengan validasi constant untuk kategori, kondisi, dan status`
* **Deskripsi Perubahan:** Membuat kerangka awal `listingController.js` beserta konstanta validasi untuk kategori perangkat audio, kondisi fisik, dan status listing.
* **File Diubah:**
  - `soundvault-api/controllers/listingController.js` (+5)

---

#### 1. Commit `8d353cf`
* **Pesan Commit:** `Update version history in README.md`
* **Deskripsi Perubahan:** Memperbarui tabel riwayat revisi dokumen SRS pada `README.md` hingga versi `1.0`.
* **File Diubah:**
  - `README.md` (+4, -2)

</details>

---

## 📁 Arsip Mingguan Sebelumnya

<details>
<summary><b>📌 Week 10 — Backend REST API, Autentikasi JWT & Integrasi Katalog Android (18 Commit)</b></summary>

### Ringkasan Eksekutif Week 10

Pada Week 10, pengerjaan proyek **SoundVault** berfokus pada pembangunan backend REST API (`soundvault-api`) serta integrasi autentikasi dan katalog merek audio pada aplikasi Android:
1. **Inisialisasi Backend (`soundvault-api`)**: Setup proyek Node.js beserta dependensi esensial, konfigurasi koneksi database dengan `dotenv` (`db.js`), serta skema database dan skrip *seed* data awal (`schema.sql`, `seed.js`).
2. **Sistem Autentikasi & Otorisasi Backend**: Implementasi JWT (`generateTokens`), middleware `verifyToken` dan `requireAdmin`, serta endpoint register, login, refresh token, dan logout (`authController.js`, `authRoutes.js`).
3. **API CRUD Katalog Merek Audio**: Implementasi logika CRUD merek audio dengan validasi dan penanganan kesalahan (`brandController.js`, `brandRoutes.js`, `server.js`).
4. **Integrasi Autentikasi Android**: Penambahan izin Internet & cleartext traffic (`AndroidManifest.xml`), model autentikasi (`AuthModels.kt`), manajemen sesi lokal (`SessionManager.kt`), konfigurasi `AuthInterceptor` & `TokenAuthenticator` di `RetrofitClient.kt`, serta `AuthRepository` dan `AuthViewModel`.
5. **UI Autentikasi, Navigasi & Katalog Terintegrasi**: Pembuatan `LoginScreen.kt` dan `RegisterScreen.kt`, pengaturan `SoundVaultNavGraph` (`NavGraph.kt`, `Screen.kt`), serta integrasi `BrandCatalogScreen` dengan backend API (pencarian, pembaruan data, dan dialog konfirmasi).

<details>
<summary><b>📜 Daftar Riwayat Commit — Week 10 (18 Commit)</b></summary>

| No | Hash Commit | Pesan Commit | Tanggal & Waktu |
|---|---|---|---|
| 1 | `3c4692a` | Menghubungkan fitur katalog merek audio dengan API backend, serta menambahkan fitur pencarian, pembaruan data, dan dialog konfirmasi | Tue Nov 17 14:56:18 2026 +0700 |
| 2 | `1c215d9` | Menambahkan UI Login dan Register serta mengonfigurasi navigasi otentikasi | Tue Nov 17 14:40:03 2026 +0700 |
| 3 | `37b2c19` | Menambahkan `AuthRepository` dan `AuthViewModel` untuk alur autentikasi | Tue Nov 17 14:38:09 2026 +0700 |
| 4 | `b239e2d` | Menambahkan endpoint autentikasi serta konfigurasi AuthInterceptor dan TokenAuthenticator di RetrofitClient | Tue Nov 17 14:37:12 2026 +0700 |
| 5 | `0ccefdd` | Menambahkan `SessionManager` dan model `UserSession` untuk manajemen sesi lokal | Tue Nov 17 14:35:32 2026 +0700 |
| 6 | `3fe7934` | Menambahkan data model untuk otentikasi (`AuthModels`) | Tue Nov 17 14:34:10 2026 +0700 |
| 7 | `86f8aff` | Menambahkan izin Internet dan mengaktifkan cleartext traffic di AndroidManifest | Tue Nov 17 14:32:55 2026 +0700 |
| 8 | `7479c97` | Menambahkan skrip seed untuk mengisi data awal pengguna dan merek audio ke dalam database | Tue Nov 17 14:03:13 2026 +0700 |
| 9 | `9aefafa` | Menambahkan rute dasar untuk autentikasi dan merek dengan middleware verifikasi token dan admin | Tue Nov 17 13:50:24 2026 +0700 |
| 10 | `f2cca6c` | Menambahkan logika CRUD untuk merek audio dengan validasi dan penanganan kesalahan | Tue Nov 17 13:47:59 2026 +0700 |
| 11 | `00efb31` | Menambahkan logika autentikasi pengguna dengan fungsi login, refresh token, dan logout | Tue Nov 17 13:25:26 2026 +0700 |
| 12 | `fff977d` | Menambahkan logika registrasi pengguna dengan validasi input dan penyimpanan ke database | Tue Nov 17 13:11:23 2026 +0700 |
| 13 | `1b68557` | Menambahkan fungsi generateTokens untuk menghasilkan access dan refresh token | Tue Nov 17 13:09:18 2026 +0700 |
| 14 | `20d4446` | Menambahkan middleware requireAdmin untuk membatasi akses hanya kepada pengguna dengan peran ADMIN | Tue Nov 17 13:08:51 2026 +0700 |
| 15 | `272ae51` | Menambahkan logika verifikasi token autentikasi pada middleware | Tue Nov 17 13:08:38 2026 +0700 |
| 16 | `d2a2ce4` | Menambahkan middleware untuk verifikasi token autentikasi | Tue Nov 17 13:08:26 2026 +0700 |
| 17 | `7eedc11` | menambah konfigurasi koneksi database dengan dotenv | Tue Nov 17 13:07:40 2026 +0700 |
| 18 | `abcba31` | instalasi soundvault-api dengan essential serta dependencies | Tue Nov 17 13:05:48 2026 +0700 |

</details>

<details>
<summary><b>🔍 Detail Perubahan Per Commit — Week 10</b></summary>

#### 18. Commit `3c4692a`
* **Pesan Commit:** `Menghubungkan fitur katalog merek audio dengan API backend, serta menambahkan fitur pencarian, pembaruan data, dan dialog konfirmasi`
* **Deskripsi Perubahan:** Menghubungkan `BrandCatalogScreen`, `BrandViewModel`, dan `BrandRepository` dengan API backend beserta fitur pencarian, pembaruan data, dan dialog konfirmasi, serta penyesuaian pada `AudioBrand`, `RetrofitClient`, dan `MainActivity`.
* **File Diubah:**
  - `app/src/main/java/com/example/proj_soundvault/MainActivity.kt`
  - `app/src/main/java/com/example/proj_soundvault/data/model/AudioBrand.kt`
  - `app/src/main/java/com/example/proj_soundvault/data/remote/RetrofitClient.kt`
  - `app/src/main/java/com/example/proj_soundvault/data/repository/BrandRepository.kt`
  - `app/src/main/java/com/example/proj_soundvault/ui/screen/brand/BrandCatalogScreen.kt`
  - `app/src/main/java/com/example/proj_soundvault/ui/viewmodel/BrandViewModel.kt`

---

#### 17. Commit `1c215d9`
* **Pesan Commit:** `Menambahkan UI Login dan Register serta mengonfigurasi navigasi otentikasi` *(judul asli di git diawali baris usulan pesan commit)*
* **Deskripsi Perubahan:** Menambahkan `LoginScreen` dan `RegisterScreen` yang terintegrasi dengan `AuthViewModel`, menambahkan rute `Login`, `Register`, dan `BrandCatalog` pada `Screen.kt`, serta mengimplementasikan `SoundVaultNavGraph` pada `NavGraph.kt`.
* **File Diubah:**
  - `app/src/main/java/com/example/proj_soundvault/navigation/NavGraph.kt` (+65)
  - `app/src/main/java/com/example/proj_soundvault/navigation/Screen.kt` (+5)
  - `app/src/main/java/com/example/proj_soundvault/ui/screen/auth/LoginScreen.kt` (+118)
  - `app/src/main/java/com/example/proj_soundvault/ui/screen/auth/RegisterScreen.kt` (+130)

---

#### 16. Commit `37b2c19`
* **Pesan Commit:** `Menambahkan AuthRepository dan AuthViewModel untuk alur autentikasi`
* **Deskripsi Perubahan:** Mengimplementasikan `AuthRepository` untuk komunikasi API autentikasi dan `AuthViewModel` untuk mengelola state UI autentikasi.
* **File Diubah:**
  - `app/src/main/java/com/example/proj_soundvault/data/repository/AuthRepository.kt` (+74)
  - `app/src/main/java/com/example/proj_soundvault/ui/AuthViewModel.kt` (+88)

---

#### 15. Commit `b239e2d`
* **Pesan Commit:** `Menambahkan endpoint autentikasi serta konfigurasi AuthInterceptor dan TokenAuthenticator di RetrofitClient`
* **Deskripsi Perubahan:** Menambahkan definisi endpoint autentikasi di `ApiService` serta konfigurasi `AuthInterceptor` dan `TokenAuthenticator` pada `RetrofitClient`.
* **File Diubah:**
  - `app/src/main/java/com/example/proj_soundvault/data/remote/ApiService.kt` (+31)
  - `app/src/main/java/com/example/proj_soundvault/data/remote/RetrofitClient.kt` (+125)

---

#### 14. Commit `0ccefdd`
* **Pesan Commit:** `Menambahkan SessionManager dan model UserSession untuk manajemen sesi lokal`
* **Deskripsi Perubahan:** Menambahkan `SessionManager` untuk menyimpan dan mengelola sesi pengguna lokal serta model `UserSession`.
* **File Diubah:**
  - `app/src/main/java/com/example/proj_soundvault/data/local/SessionManager.kt` (+81)

---

#### 13. Commit `3fe7934`
* **Pesan Commit:** `Menambahkan data model untuk otentikasi (AuthModels)`
* **Deskripsi Perubahan:** Menambahkan struktur data model untuk request dan response autentikasi.
* **File Diubah:**
  - `app/src/main/java/com/example/proj_soundvault/data/model/AuthModels.kt` (+43)

---

#### 12. Commit `86f8aff`
* **Pesan Commit:** `Menambahkan izin Internet dan mengaktifkan cleartext traffic di AndroidManifest`
* **Deskripsi Perubahan:** Menambahkan permission `INTERNET` dan `usesCleartextTraffic` pada manifest aplikasi Android.
* **File Diubah:**
  - `app/src/main/AndroidManifest.xml` (+6)

---

#### 11. Commit `7479c97`
* **Pesan Commit:** `Menambahkan skrip seed untuk mengisi data awal pengguna dan merek audio ke dalam database`
* **Deskripsi Perubahan:** Menambahkan skema SQL (`schema.sql`) dan skrip `seed.js` untuk inisialisasi data pengguna dan merek audio di database.
* **File Diubah:**
  - `soundvault-api/seed.js` (+48)
  - `soundvault-api/src/schema.sql` (+105)

---

#### 10. Commit `9aefafa`
* **Pesan Commit:** `Menambahkan rute dasar untuk autentikasi dan merek dengan middleware verifikasi token dan admin`
* **Deskripsi Perubahan:** Mengonfigurasi entry point `server.js` serta rute API untuk autentikasi (`authRoutes.js`) dan merek audio (`brandRoutes.js`).
* **File Diubah:**
  - `soundvault-api/server.js` (+22)
  - `soundvault-api/src/routes/authRoutes.js` (+11)
  - `soundvault-api/src/routes/brandRoutes.js` (+12)

---

#### 9. Commit `f2cca6c`
* **Pesan Commit:** `Menambahkan logika CRUD untuk merek audio dengan validasi dan penanganan kesalahan`
* **Deskripsi Perubahan:** Mengimplementasikan controller CRUD untuk entitas merek audio beserta validasi input dan error handling.
* **File Diubah:**
  - `soundvault-api/src/controllers/brandController.js` (+164)

---

#### 8. Commit `00efb31`
* **Pesan Commit:** `Menambahkan logika autentikasi pengguna dengan fungsi login, refresh token, dan logout`
* **Deskripsi Perubahan:** Menambahkan handler login, refresh token, dan logout pada `authController.js`.
* **File Diubah:**
  - `soundvault-api/src/controllers/authController.js` (+124)

---

#### 7. Commit `fff977d`
* **Pesan Commit:** `Menambahkan logika registrasi pengguna dengan validasi input dan penyimpanan ke database`
* **Deskripsi Perubahan:** Menambahkan handler registrasi pengguna pada `authController.js`.
* **File Diubah:**
  - `soundvault-api/src/controllers/authController.js` (+51)

---

#### 6. Commit `1b68557`
* **Pesan Commit:** `Menambahkan fungsi generateTokens untuk menghasilkan access dan refresh token`
* **Deskripsi Perubahan:** Menambahkan helper `generateTokens` pada `authController.js`.
* **File Diubah:**
  - `soundvault-api/src/controllers/authController.js` (+21)

---

#### 5. Commit `20d4446`
* **Pesan Commit:** `Menambahkan middleware requireAdmin untuk membatasi akses hanya kepada pengguna dengan peran ADMIN`
* **Deskripsi Perubahan:** Menambahkan middleware otorisasi peran `ADMIN` pada `authMiddleware.js`.
* **File Diubah:**
  - `soundvault-api/src/middleware/authMiddleware.js` (+13)

---

#### 4. Commit `272ae51`
* **Pesan Commit:** `Menambahkan logika verifikasi token autentikasi pada middleware`
* **Deskripsi Perubahan:** Melengkapi implementasi verifikasi JWT pada `authMiddleware.js`.
* **File Diubah:**
  - `soundvault-api/src/middleware/authMiddleware.js` (+14)

---

#### 3. Commit `d2a2ce4`
* **Pesan Commit:** `Menambahkan middleware untuk verifikasi token autentikasi`
* **Deskripsi Perubahan:** Membuat kerangka awal middleware verifikasi token autentikasi.
* **File Diubah:**
  - `soundvault-api/src/middleware/authMiddleware.js` (+11)

---

#### 2. Commit `7eedc11`
* **Pesan Commit:** `menambah konfigurasi koneksi database dengan dotenv`
* **Deskripsi Perubahan:** Menambahkan konfigurasi koneksi database menggunakan environment variable via `dotenv`.
* **File Diubah:**
  - `soundvault-api/src/config/db.js` (+12)

---

#### 1. Commit `abcba31`
* **Pesan Commit:** `instalasi soundvault-api dengan essential serta dependencies`
* **Deskripsi Perubahan:** Inisialisasi direktori `soundvault-api`, instalasi dependensi awal (`package.json`, `package-lock.json`), dan pembuatan struktur file backend awal.
* **File Diubah:**
  - `.gitignore` (+2)
  - `.idea/deviceManager.xml` (+13), `.idea/gradle.xml` (+1)
  - `soundvault-api/package.json` (+21), `soundvault-api/package-lock.json` (+1549)
  - File kerangka awal di `soundvault-api/` (`seed.js`, `server.js`, `src/config/db.js`, `src/controllers/*`, `src/middleware/*`, `src/routes/*`)

</details>

</details>

<details>
<summary><b>📌 Week 9 — Inisialisasi Proyek Android, Arsitektur MVVM & UI Katalog Awal (8 Commit)</b></summary>

### Ringkasan Eksekutif Week 9

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

<details>
<summary><b>📜 Daftar Riwayat Commit — Week 9 (8 Commit)</b></summary>

| No | Hash Commit | Pesan Commit | Tanggal & Waktu |
|---|---|---|---|
| 1 | `d23ed2f` | Memperbarui `compileSdk` ke versi 37 | Mon Nov 9 15:17:37 2026 +0700 |
| 2 | `d22c856` | Mengganti UI Greeting default dengan BrandCatalogScreen pada MainActivity | Mon Nov 9 15:16:08 2026 +0700 |
| 3 | `4bf59a6` | Menambahkan UI katalog brand beserta ViewModel dan Repository | Mon Nov 9 15:15:23 2026 +0700 |
| 4 | `b835b12` | Menambahkan `RetrofitClient` untuk inisialisasi `ApiService` | Mon Nov 9 15:12:37 2026 +0700 |
| 5 | `d6a06cc` | Menambahkan interface untuk ApiService dan melengkapi model AudioBrand | Mon Nov 9 15:11:13 2026 +0700 |
| 6 | `dc60236` | menambah MockSessionManager dan AudioBrand model | Mon Nov 9 15:01:01 2026 +0700 |
| 7 | `aa1198e` | Menambahkan dependensi Navigation dan Retrofit serta file navigasi awal | Mon Nov 9 14:51:01 2026 +0700 |
| 8 | `5955072` | Initial commit | Mon Nov 9 14:43:24 2026 +0700 |

</details>

<details>
<summary><b>🔍 Detail Perubahan Per Commit — Week 9</b></summary>

#### 8. Commit `d23ed2f`
* **Pesan Commit:** `Memperbarui compileSdk ke versi 37`
* **Deskripsi Perubahan:** Memperbarui konfigurasi `compileSdk` dari versi terdahulu menjadi 37 pada konfigurasi Gradle aplikasi untuk menyelaraskan dengan SDK Android terbaru.
* **File Diubah:**
  - `app/build.gradle.kts` (+1, -1)

---

#### 7. Commit `d22c856`
* **Pesan Commit:** `Mengganti UI Greeting default dengan BrandCatalogScreen pada MainActivity`
* **Deskripsi Perubahan:** Menghapus composable `Greeting` standar dari wizard bawaan Android Studio dan menggantikannya dengan penanganan UI utama menggunakan `BrandCatalogScreen()`.
* **File Diubah:**
  - `app/src/main/java/com/example/proj_soundvault/MainActivity.kt` (+5, -30)

---

#### 6. Commit `4bf59a6`
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

#### 5. Commit `b835b12`
* **Pesan Commit:** `Menambahkan RetrofitClient untuk inisialisasi ApiService`
* **Deskripsi Perubahan:** Membuat singleton `RetrofitClient` untuk mengonfigurasi instance Retrofit dengan Base URL dan Converter Factory.
* **File Diubah:**
  - `app/src/main/java/com/example/proj_soundvault/data/remote/RetrofitClient.kt` (+17)

---

#### 4. Commit `d6a06cc`
* **Pesan Commit:** `Menambahkan interface untuk ApiService dan melengkapi model AudioBrand`
* **Deskripsi Perubahan:**
  - Mendefinisikan interface `ApiService` dengan endpoint REST API yang dibutuhkan.
  - Melengkapi properti pada data class `AudioBrand`.
* **File Diubah:**
  - `app/src/main/java/com/example/proj_soundvault/data/model/AudioBrand.kt` (+6)
  - `app/src/main/java/com/example/proj_soundvault/data/remote/ApiService.kt` (+15)

---

#### 3. Commit `dc60236`
* **Pesan Commit:** `menambah MockSessionManager dan AudioBrand model`
* **Deskripsi Perubahan:** Menambahkan model dasar `AudioBrand` dan kelas helper `MockSessionManager` untuk simulasi sesi pengguna.
* **File Diubah:**
  - `app/src/main/java/com/example/proj_soundvault/data/model/AudioBrand.kt` (+2)
  - `app/src/main/java/com/example/proj_soundvault/data/remote/MockSessionManager.kt` (+16)

---

#### 2. Commit `aa1198e`
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

#### 1. Commit `5955072`
* **Pesan Commit:** `Initial commit`
* **Deskripsi Perubahan:** Inisialisasi awal proyek Android Studio (SoundVault) dengan struktur Jetpack Compose bawaan.
* **File Diubah:**
  - 48 file awal (skrip Gradle, AndroidManifest, MainActivity awal, Theme, Drawable, Mipmap resources, dll).

</details>

</details>
