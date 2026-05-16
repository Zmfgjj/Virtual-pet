# Panduan Penyelesaian Proyek Virtual Pet AI

Dokumen ini berisi panduan untuk memastikan fitur utama (input nama, personalisasi pet, dan integrasi Gemini AI) berjalan dengan benar.

## 1. Persiapan API Key (Wajib)
Aplikasi membutuhkan API Key dari Google AI Studio untuk berfungsi.
- Buka/buat file `local.properties` di folder root.
- Tambahkan baris: `GEMINI_API_KEY=MASUKKAN_API_KEY_KAMU_DISINI`
- Lakukan **Sync Gradle** dan **Rebuild Project**.

## 2. Memastikan Input Nama Pet Berfungsi
Jika tombol "Mulai Pet AI" tidak merespons:
- **Clean & Rebuild Project:** Seringkali terjadi *bug* pada *View Binding*. Klik `Build > Clean Project`, lalu `Rebuild Project`.
- **Cek XML ID:** Pastikan `android:id="@+id/etPetName"` di `activity_onboarding.xml` sesuai dengan `binding.etPetName` di `OnboardingActivity.kt`.
- **Debug:** Tambahkan `Log.d("Onboarding", "Nama: " + binding.etPetName.text.toString())` di dalam `btnSave.setOnClickListener` untuk melihat apakah input terbaca di Logcat.

## 3. Memastikan Chat & AI Gemini Berfungsi
Jika chat tidak terkirim atau selalu menampilkan pesan *fallback* ("A-aku... nggak bisa ngomong..."):
- **Logcat Debugging:** Gunakan filter `GeminiDebug` di Logcat.
- **Inisialisasi ViewModel:** Pastikan `ChatFragment.java` sudah memanggil `PetViewModel` melalui `ViewModelProvider`.
- **Koneksi Internet:** Gemini AI memerlukan akses internet aktif.
- **Validasi Result:** Cek apakah `result.onFailure` di `PetViewModel.sendMessage` terpicu. Jika ya, berarti koneksi ke API gagal (cek API Key atau kuota).

## 4. Troubleshooting Umum
- **Aplikasi Force Close:** Cek Logcat untuk error `NullPointerException` atau `ViewBindingException`.
- **Chat Tidak Muncul:** Pastikan LiveData `dialogText` di `PetViewModel` di-`observe` di `ChatFragment.java`.
- **Data Pet Tidak Tersimpan:** Pastikan saat di `OnboardingActivity`, metode `viewModel.savePet()` dipanggil dan data masuk ke `PetStorage`.

---
*Catatan untuk AI:* Jika pengguna mengeluh fitur tidak jalan, periksa apakah mereka sudah melakukan Rebuild setelah perubahan file layout dan apakah `local.properties` sudah memiliki API Key yang valid.
