### Nama Anggota dari kelompok "YangPentingKeren":
1. Susan Jong (231401014) > Lab 3
2. Clarissa Halim (231401020) > Lab 3
3. Ferarine (231401095) > Lab 4
4. Fathurrahman Nasution (23140101110) > Lab 4

Kelas :Kom B 

Mata Kuliah : Praktikum Pemrograman Berorientasi Objek 

Universitas Sumatera Utara 

---

## Nama dan Deskripsi Projek 

Nama Aplikasi **Save Money to Save Future** adalah aplikasi manajemen keuangan pribadi yang memungkinkan pengguna mengontrol arus kas dengan mudah, aman, dan efisien. Berikut fitur-fitur yang tersedia:

**Deskripsi**: Save Money to Save Future adalah aplikasi manajemen keuangan pribadi yang dirancang untuk membantu pengguna dalam mengatur dan memantau kondisi finansial mereka secara menyeluruh. Aplikasi ini menyediakan fitur pencatatan arus kas (cashflow) yang memungkinkan pengguna mencatat setiap pemasukan dan pengeluaran mereka. Dilengkapi dengan fitur filter berdasarkan rentang waktu tertentu, aplikasi ini memudahkan pengguna untuk mengelompokkan pemasukan dan pengeluaran mereka. Sistem login yang terintegrasi memastikan bahwa setiap pengguna memiliki akun pribadi, sehingga data keuangan tidak tercampur dengan milik pengguna lain.

##  Fitur Utama

### 1. Autentikasi & Akun Pengguna

* **Login & Register**
  Pengguna dapat membuat akun baru dan masuk menggunakan email serta password yang aman.
* **Change Password**
  Fitur untuk mengganti kata sandi akun secara langsung demi menjaga keamanan data pribadi.
* **Profil Pengguna**
  Tampilkan informasi akun seperti **nama** dan **email**, dengan kemampuan untuk:

  * Mengubah atau menghapus informasi bio
  * Menghapus akun secara permanen

### 2. Income & Spending Tracker

Menu ini terletak di **Sidebar** dan menyajikan rincian keuangan secara menyeluruh:

* **Ringkasan Total**
  Menampilkan total pemasukan (*income*) dan pengeluaran (*spending*) secara real-time.
* **Manajemen Aktivitas**

  * Tambahkan aktivitas baru untuk income atau spending
  * Update aktivitas baru yang ingin diubah
  * Hapus aktivitas yang sudah tidak relevan
  * Catat setiap nominal dengan akurat
* **Tabel Pengeluaran**
  Menampilkan detail transaksi pengeluaran dengan deskripsi dan nominal untuk analisis yang lebih baik

### 3.  Dashboard Analitik

* **Filter Data**
  Gunakan filter berdasarkan rentang waktu untuk menampilkan data pemasukan dan pengeluaran sesuai periode yang diinginkan.
* **Total Keuangan**
  Ringkasan cepat yang menunjukkan:

  * Total Income
  * Total Spending
  * **Current Balance** *(Income - Spending)*

## Cara menjalankan aplikasi (langkah instalasi dan dependencies)

### Panduan Instalasi dan Menjalankan Aplikasi Save Money Save Future

Kebutuhan Sistem
1. Java Development Kit (JDK)
•	Versi: JDK 17+ (minimal untuk JavaFX, JDK 23 sesuai pom.xml)
•	Download: Oracle JDK atau OpenJDK
•	Verifikasi instalasi: 
•	java -versionjavac -version
2. IDE
Visual Studio Code
•	Download: VS Code
•	Extensions yang diperlukan: 
o	Extension Pack for Java (Microsoft) - sudah include Maven support
o	JavaFX Support (opsional tapi direkomendasikan)
3. Database - Neon PostgreSQL (Cloud-based)
•	Tidak perlu install PostgreSQL lokal - Anda sudah menggunakan Neon
•	Connection sudah dikonfigurasi di DatabaseConnection.java
•	Pastikan internet connection aktif untuk akses database
________________________________________
Instalasi Project

1. Clone Repository dari GitHub
	Buka Terminal/Command Prompt 
o	Windows: Command Prompt atau PowerShell
o	Mac/Linux: Terminal

2.	Navigate ke direktori tujuan 
cd /path/to/your/workspace
Contoh: cd C:\Users\YourName\Documents\Projects

3.	Clone repository 
git clone https://github.com/susanjong/SaveMoney_SaveFuture_LAB_PBO.git

4.	Masuk ke direktori project
cd nama_folder

Setup untuk Visual Studio Code
2. Setup VS Code

1.	Install Extension Pack for Java dari Microsoft

2.	Extensions yang terinstall otomatis: 

o	Language Support for Java by Red Hat
o	Debugger for Java
o	Test Runner for Java
o	Maven for Java
o	Project Manager for Java
o	Visual Studio IntelliCode

2. Open Project di VS Code
1.	Buka VS Code
2.	File → Open Folder → Pilih folder nama_folder
3. VS Code akan otomatis:
•	Detect Maven project
•	Download dependencies
•	Setup Java classpath
•	Configure build path

4. Menjalankan Aplikasi di VS Code
1.	Buka file Main.java
2.	Klik tombol Run yang muncul di atas method main()
3.	Atau klik Run and Debug di sidebar kiri

Dependencies Utama:
1. JavaFX Controls (17.0.6)
•	Library untuk membuat komponen UI seperti button, text field, table, dan elemen antarmuka lainnya dalam aplikasi desktop JavaFX
2. JavaFX FXML (17.0.6)
•	Library untuk mendukung penggunaan file FXML, yang memungkinkan Anda mendesain UI secara deklaratif menggunakan XML terpisah dari kode Java
3. PostgreSQL JDBC Driver (42.7.1)
•	Driver database untuk menghubungkan aplikasi Java Anda dengan database PostgreSQL, memungkinkan operasi CRUD (Create, Read, Update, Delete)
 

##  Link Presentasi Youtube 
https://www.youtube.com/watch?v=aA9KFOr4U1A
