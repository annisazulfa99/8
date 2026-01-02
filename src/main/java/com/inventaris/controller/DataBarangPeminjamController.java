package com.inventaris.controller;

import com.inventaris.Main;
import com.inventaris.dao.BarangDAO;
import com.inventaris.dao.InstansiDAO;
import com.inventaris.model.Barang;
import com.inventaris.model.CartItem;
import com.inventaris.util.AlertUtil;
import com.inventaris.util.CartManager;
import com.inventaris.util.SessionManager;
import java.io.IOException;
import java.io.InputStream;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * DataBarangPeminjamController - Halaman Katalog Barang
 * UPDATED: Instansi tanpa keranjang + fitur tambah barang baru
 */
public class DataBarangPeminjamController implements Initializable {
    
    // --- UI Component ---
    @FXML private Label cartBadge;
    @FXML private Label lblTotalBarang;
    @FXML private Label lblResultCount;
    @FXML private ComboBox<String> filterLembaga;
    @FXML private ComboBox<String> filterBEM;
    @FXML private ComboBox<String> filterHimpunan;
    @FXML private ComboBox<String> filterUKM;
    @FXML private ComboBox<String> sortCombo;
    @FXML private FlowPane catalogGrid;
    @FXML private VBox emptyState;
    @FXML private StackPane contentArea;
    @FXML private ScrollPane filterSidebar;
    @FXML private HBox cartContainer; // ✅ NEW: Container untuk keranjang
    @FXML private HBox actionButtonContainer; // ✅ NEW: Container untuk tombol aksi
    
    private Parent currentContent;
    
    // --- Data & Tools ---
    private final BarangDAO barangDAO = new BarangDAO();
    private final SessionManager sessionManager = SessionManager.getInstance();
    private List<Barang> allBarang = new ArrayList<>();
    private List<Barang> filteredBarang = new ArrayList<>();
    
    // Keyword pencarian dari LayoutController
    private String currentSearchKeyword = ""; 
    
    // 🛡️ FLAG PENGAMAN: Mencegah error looping saat reset otomatis
    private boolean isUpdatingFilter = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("🔄 DataBarangPeminjam initializing...");
        System.out.println("👤 Current Role: " + sessionManager.getCurrentRole());
        System.out.println("🆔 Role ID: " + sessionManager.getCurrentRoleId());
        
        // ✅ 1. Sembunyikan Filter Sidebar untuk Instansi
        hideFilterForInstansi();
        
        // ✅ 2. Configure UI untuk Instansi (hapus keranjang, tambah button)
        configureUIForRole();
        
        // ✅ 3. Ambil Data dari Database (DENGAN FILTER ROLE!)
        loadAllBarang();

        // ✅ 4. Isi Pilihan Dropdown (SESUAI ROLE!)
        loadFilters();
        
        // 5. Pasang Logic "Saling Reset"
        setupListeners();
        
        // 6. Pastikan tampilan awal bersih
        handleResetFilter(); 
        
        // 7. Cek Keranjang (hanya untuk peminjam)
        if (!sessionManager.isInstansi()) {
            updateCartBadge();
        }
        
        System.out.println("✅ DataBarang Initialized. Total barang: " + allBarang.size());
    }
    
    // ============================================================
    // ✅ NEW: CONFIGURE UI UNTUK ROLE
    // ============================================================
    
    /**
     * ✅ Configure UI: Hapus keranjang & tambah button untuk Instansi
     */
    private void configureUIForRole() {
        if (sessionManager.isInstansi()) {
            System.out.println("🔧 Configuring UI for INSTANSI");
            
            // ❌ Sembunyikan keranjang
            if (cartContainer != null) {
                cartContainer.setVisible(false);
                cartContainer.setManaged(false);
                System.out.println("   ❌ Cart - HIDDEN");
            }
            
            // ✅ Tambahkan button "Tambah Barang"
            if (actionButtonContainer != null) {
                Button btnTambah = new Button("+ Tambah Barang Baru");
                btnTambah.setStyle(
                    "-fx-background-color: #6A5436; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 20; " +
                    "-fx-padding: 10 20; " +
                    "-fx-cursor: hand; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 14px;"
                );
                btnTambah.setOnAction(e -> handleTambahBarang());
                
                actionButtonContainer.getChildren().clear();
                actionButtonContainer.getChildren().add(btnTambah);
                System.out.println("   ✅ Tambah Barang Button - ADDED");
            }
        } else {
            System.out.println("👁️ Normal mode (Peminjam/Admin) - Cart visible");
        }
    }
    
    // ============================================================
    // ✅ NEW: HIDE FILTER SIDEBAR UNTUK INSTANSI
    // ============================================================
    
    /**
     * ✅ Sembunyikan sidebar filter jika yang login adalah Instansi
     */
    private void hideFilterForInstansi() {
        if (sessionManager.isInstansi() && filterSidebar != null) {
            System.out.println("🚫 Hiding filter sidebar for Instansi");
            filterSidebar.setVisible(false);
            filterSidebar.setManaged(false);
        }
    }
    
    // ============================================================
    // ✅ PERBAIKAN UTAMA: LOAD DATA SESUAI ROLE
    // ============================================================
    
    /**
     * ✅ FIXED: Load barang sesuai role yang login
     */
    private void loadAllBarang() {
        try {
            if (sessionManager.isInstansi()) {
                // ✅ INSTANSI: Hanya barang miliknya sendiri
                Integer instansiId = sessionManager.getCurrentRoleId();
                
                if (instansiId == null) {
                    System.err.println("❌ ERROR: instansiId is NULL!");
                    AlertUtil.showError("Error", "ID Instansi tidak ditemukan!");
                    allBarang = new ArrayList<>();
                    return;
                }
                
                System.out.println("🔒 INSTANSI MODE - Loading barang untuk ID: " + instansiId);
                
                // Ambil SEMUA barang instansi ini (termasuk yang stok 0)
                allBarang = barangDAO.getByInstansi(instansiId);
                
                System.out.println("📦 Barang instansi ditemukan: " + allBarang.size() + " items");
                for (Barang b : allBarang) {
                    System.out.println("   - " + b.getKodeBarang() + " | " + b.getNamaBarang() + " | Stok: " + b.getJumlahTersedia());
                }
                
            } else {
                // ✅ ADMIN & PEMINJAM: Lihat semua barang available
                System.out.println("👁️ Loading ALL available barang (Admin/Peminjam mode)");
                allBarang = barangDAO.getAvailable();
            }
            
            lblTotalBarang.setText(allBarang.size() + " items");
            
        } catch (Exception e) {
            System.err.println("❌ Error loading barang: " + e.getMessage());
            e.printStackTrace();
            allBarang = new ArrayList<>();
        }
    }

    /**
     * ✅ FIXED: Load filter dropdown sesuai role
     */
    private void loadFilters() {
        InstansiDAO dao = new InstansiDAO();
        
        if (sessionManager.isInstansi()) {
            // ✅ INSTANSI: Skip loading filter (sidebar sudah disembunyikan)
            System.out.println("🔒 Instansi mode - Skipping filter setup");
            
        } else {
            // ✅ ADMIN & PEMINJAM: Enable filter normal
            System.out.println("👁️ Admin/Peminjam mode - Loading all filters");
            
            fillCombo(filterLembaga, dao.getByKategori("LEMBAGA"));
            fillCombo(filterBEM, dao.getByKategori("BEM"));
            fillCombo(filterHimpunan, dao.getByKategori("HIMPUNAN"));
            fillCombo(filterUKM, dao.getByKategori("UKM"));
        }

        // Sort combo tetap aktif untuk semua role
        sortCombo.getItems().setAll("Terbaru", "Nama A-Z", "Nama Z-A", "Stok Terbanyak");
        sortCombo.setValue("Terbaru");
    }
    
    // ============================================================
    // 1. LOGIC FILTER INTERACTION
    // ============================================================
    
    private void setupListeners() {
        // Skip setup listeners untuk instansi (karena filter disembunyikan)
        if (sessionManager.isInstansi()) {
            sortCombo.setOnAction(e -> applyFiltersAndDisplay());
            return;
        }
        
        // Pasang listener khusus ke setiap ComboBox (untuk Admin & Peminjam)
        filterLembaga.setOnAction(e -> handleSingleFilterSelection(filterLembaga));
        filterBEM.setOnAction(e -> handleSingleFilterSelection(filterBEM));
        filterHimpunan.setOnAction(e -> handleSingleFilterSelection(filterHimpunan));
        filterUKM.setOnAction(e -> handleSingleFilterSelection(filterUKM));
        
        // Listener Sort beda sendiri (tidak mereset filter lain)
        sortCombo.setOnAction(e -> applyFiltersAndDisplay());
    }

    /**
     * Logic Pintar: Saat satu dipilih, yang lain otomatis jadi "Semua"
     */
    private void handleSingleFilterSelection(ComboBox<String> sourceCombo) {
        // Jika sedang proses reset, jangan jalankan logic ini
        if (isUpdatingFilter) return; 
        
        // Jika instansi yang login, skip filter logic
        if (sessionManager.isInstansi()) {
            applyFiltersAndDisplay();
            return;
        }

        String selectedValue = sourceCombo.getValue();

        // Jika user memilih sesuatu yang BUKAN "Semua"
        if (selectedValue != null && !"Semua".equals(selectedValue)) {
            
            // 🔒 Kunci pintu dulu
            isUpdatingFilter = true; 
            
            try {
                // Reset ComboBox lain selain yang sedang dipilih
                if (sourceCombo != filterLembaga) filterLembaga.setValue("Semua");
                if (sourceCombo != filterBEM) filterBEM.setValue("Semua");
                if (sourceCombo != filterHimpunan) filterHimpunan.setValue("Semua");
                if (sourceCombo != filterUKM) filterUKM.setValue("Semua");
            } finally {
                // 🔓 Buka kunci pintu
                isUpdatingFilter = false; 
            }
        }
        
        // Terapkan filter ke layar
        applyFiltersAndDisplay();
    }

    @FXML
    private void handleResetFilter() {
        // Skip untuk instansi (tidak ada filter)
        if (sessionManager.isInstansi()) {
            this.currentSearchKeyword = ""; 
            sortCombo.setValue("Terbaru");
            applyFiltersAndDisplay();
            return;
        }
        
        // 🔒 Kunci pintu agar listener di atas tidak 'kaget'
        isUpdatingFilter = true;
        
        try {
            filterLembaga.setValue("Semua");
            filterBEM.setValue("Semua");
            filterHimpunan.setValue("Semua");
            filterUKM.setValue("Semua");
            
            this.currentSearchKeyword = ""; 
            sortCombo.setValue("Terbaru");
            
        } finally {
            // 🔓 Buka kunci
            isUpdatingFilter = false;
        }
        
        // Tampilkan ulang semua data
        applyFiltersAndDisplay();
    }

    // ============================================================
    // 2. DATA PROCESSING & DISPLAY
    // ============================================================

    private void applyFiltersAndDisplay() {
        filteredBarang = new ArrayList<>(allBarang);
        
        // ✅ Jika instansi, skip filter instansi (karena sudah difilter di loadAllBarang)
        if (!sessionManager.isInstansi()) {
            // --- FILTER 1: INSTANSI (Hanya untuk Admin & Peminjam) ---
            InstansiDAO dao = new InstansiDAO();
            String selectedInstansi = null;

            if (!"Semua".equals(filterLembaga.getValue())) selectedInstansi = filterLembaga.getValue();
            else if (!"Semua".equals(filterBEM.getValue())) selectedInstansi = filterBEM.getValue();
            else if (!"Semua".equals(filterHimpunan.getValue())) selectedInstansi = filterHimpunan.getValue();
            else if (!"Semua".equals(filterUKM.getValue())) selectedInstansi = filterUKM.getValue();

            if (selectedInstansi != null) {
                int id = dao.getIdByNama(selectedInstansi);
                filteredBarang = filteredBarang.stream()
                    .filter(b -> b.getIdInstansi() != null && b.getIdInstansi() == id)
                    .collect(Collectors.toList());
            }
        }

        // --- FILTER 2: SEARCH KEYWORD (Dari LayoutController) ---
        if (!currentSearchKeyword.isEmpty()) {
            String lowerKey = currentSearchKeyword.toLowerCase();
            filteredBarang = filteredBarang.stream()
                .filter(b -> b.getNamaBarang().toLowerCase().contains(lowerKey))
                .collect(Collectors.toList());
        }

        // --- FILTER 3: SORTING ---
        String sort = sortCombo.getValue();
        if (sort != null) {
            switch (sort) {
                case "Nama A-Z": 
                    filteredBarang.sort(Comparator.comparing(Barang::getNamaBarang)); 
                    break;
                case "Nama Z-A": 
                    filteredBarang.sort(Comparator.comparing(Barang::getNamaBarang).reversed()); 
                    break;
                case "Stok Terbanyak": 
                    filteredBarang.sort(Comparator.comparingInt(Barang::getJumlahTersedia).reversed()); 
                    break;
            }
        }

        displayCatalog();
    }

    private void displayCatalog() {
        catalogGrid.getChildren().clear();
        boolean isEmpty = filteredBarang.isEmpty();
        
        emptyState.setVisible(isEmpty);
        emptyState.setManaged(isEmpty);
        
        lblResultCount.setText("Menampilkan " + filteredBarang.size() + " barang");

        if (!isEmpty) {
            for (Barang barang : filteredBarang) {
                catalogGrid.getChildren().add(createBarangCard(barang));
            }
        }
        
        System.out.println("📊 Displayed " + filteredBarang.size() + " items");
    }

    // ============================================================
    // 3. UTILITIES & INITIAL DATA SETUP
    // ============================================================

    private void fillCombo(ComboBox<String> combo, List<String> items) {
        combo.getItems().clear();
        combo.getItems().add("Semua");
        if (items != null) combo.getItems().addAll(items);
        combo.setValue("Semua");
    }

    public void searchBarang(String keyword) {
        this.currentSearchKeyword = (keyword == null) ? "" : keyword.trim();
        applyFiltersAndDisplay();
    }

    // ============================================================
    // 4. UI COMPONENTS (CARD & CART)
    // ============================================================

    private VBox createBarangCard(Barang barang) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefSize(220, 320);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        // Image Handling
        ImageView imageView = new ImageView();
        imageView.setFitWidth(180); 
        imageView.setFitHeight(140); 
        imageView.setPreserveRatio(true);
        
        try {
            String path = barang.getFotoUrl();
            if (path != null && !path.isBlank()) {
                if (path.startsWith("http")) {
                    imageView.setImage(new Image(path, true));
                } else {
                    InputStream is = getClass().getResourceAsStream(path);
                    if (is != null) imageView.setImage(new Image(is));
                }
            }
        } catch (Exception ignored) {}
        
        if (imageView.getImage() == null) {
            InputStream ph = getClass().getResourceAsStream("/images/barang/placeholder.png");
            if (ph != null) imageView.setImage(new Image(ph));
        }

        // Labels
        Label nameLbl = new Label(barang.getNamaBarang());
        nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        nameLbl.setWrapText(true); 
        nameLbl.setAlignment(Pos.CENTER);

        Label stokLbl = new Label("Stok: " + barang.getJumlahTersedia());
        stokLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        Label ownerLbl = new Label(barang.getNamaPemilik() != null ? barang.getNamaPemilik() : "Umum");
        ownerLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #6A5436;");

        // ✅ Button berbeda untuk Instansi vs Peminjam
        Button btnAdd = new Button();
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        
        if (sessionManager.isInstansi()) {
            // ✅ INSTANSI: Tombol "Edit Barang"
            btnAdd.setText("✏️ Edit Barang");
            btnAdd.setStyle("-fx-background-color: #8B6F47; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;");
            btnAdd.setOnAction(e -> handleEditBarang(barang));
        } else {
            // ✅ PEMINJAM & ADMIN: Tombol "+ Keranjang"
            btnAdd.setText("+ Keranjang");
            btnAdd.setStyle("-fx-background-color: #6A5436; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;");
            btnAdd.setOnAction(e -> showAddToCartDialog(barang));
        }

        card.getChildren().addAll(imageView, nameLbl, stokLbl, ownerLbl, btnAdd);
        return card;
    }

    // ============================================================
    // ✅ NEW: HANDLER TAMBAH BARANG BARU (INSTANSI)
    // ============================================================
    
    /**
     * ✅ Dialog untuk tambah barang baru (khusus instansi)
     */
    private void handleTambahBarang() {
        Dialog<Barang> dialog = new Dialog<>();
        dialog.setTitle("Tambah Barang Baru");
        dialog.setHeaderText("Tambah Barang untuk " + sessionManager.getCurrentUser().getNama());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(400);

        // Kode Barang
        HBox boxKode = new HBox(10);
        boxKode.setAlignment(Pos.CENTER_LEFT);
        Label lblKode = new Label("Kode Barang:");
        lblKode.setPrefWidth(120);
        TextField tfKode = new TextField();
        tfKode.setPromptText("Contoh: PSTI-004");
        tfKode.setPrefWidth(200);
        boxKode.getChildren().addAll(lblKode, tfKode);

        // Nama Barang
        HBox boxNama = new HBox(10);
        boxNama.setAlignment(Pos.CENTER_LEFT);
        Label lblNama = new Label("Nama Barang:");
        lblNama.setPrefWidth(120);
        TextField tfNama = new TextField();
        tfNama.setPromptText("Contoh: Raspberry Pi");
        tfNama.setPrefWidth(200);
        boxNama.getChildren().addAll(lblNama, tfNama);

        // Lokasi
        HBox boxLokasi = new HBox(10);
        boxLokasi.setAlignment(Pos.CENTER_LEFT);
        Label lblLokasi = new Label("Lokasi:");
        lblLokasi.setPrefWidth(120);
        TextField tfLokasi = new TextField();
        tfLokasi.setPromptText("Contoh: Lab PSTI");
        tfLokasi.setPrefWidth(200);
        boxLokasi.getChildren().addAll(lblLokasi, tfLokasi);

        // Jumlah Total
        HBox boxTotal = new HBox(10);
        boxTotal.setAlignment(Pos.CENTER_LEFT);
        Label lblTotal = new Label("Jumlah Total:");
        lblTotal.setPrefWidth(120);
        Spinner<Integer> spinnerTotal = new Spinner<>(1, 9999, 1);
        spinnerTotal.setEditable(true);
        spinnerTotal.setPrefWidth(200);
        boxTotal.getChildren().addAll(lblTotal, spinnerTotal);

        // Deskripsi
        Label lblDeskripsi = new Label("Deskripsi:");
        TextArea taDeskripsi = new TextArea();
        taDeskripsi.setPromptText("Deskripsi barang...");
        taDeskripsi.setPrefRowCount(3);

        content.getChildren().addAll(boxKode, boxNama, boxLokasi, boxTotal, lblDeskripsi, taDeskripsi);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                // Validasi
                if (tfKode.getText().trim().isEmpty()) {
                    AlertUtil.showWarning("Validasi", "Kode barang tidak boleh kosong!");
                    return null;
                }
                if (tfNama.getText().trim().isEmpty()) {
                    AlertUtil.showWarning("Validasi", "Nama barang tidak boleh kosong!");
                    return null;
                }
                
                // Create barang baru
                Barang barang = new Barang();
                barang.setKodeBarang(tfKode.getText().trim().toUpperCase());
                barang.setNamaBarang(tfNama.getText().trim());
                barang.setLokasiBarang(tfLokasi.getText().trim());
                barang.setJumlahTotal(spinnerTotal.getValue());
                barang.setJumlahTersedia(spinnerTotal.getValue()); // Tersedia = Total saat baru
                barang.setDeskripsi(taDeskripsi.getText().trim());
                barang.setKondisiBarang("baik");
                barang.setStatus("tersedia");
                barang.setIdInstansi(sessionManager.getCurrentRoleId());
                
                return barang;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newBarang -> {
            // Check kode barang duplikat
            if (barangDAO.kodeExists(newBarang.getKodeBarang())) {
                AlertUtil.showError("Error", "Kode barang sudah digunakan!");
                return;
            }
            
            // Insert ke database
            if (barangDAO.create(newBarang)) {
                AlertUtil.showSuccess("Berhasil", "Barang berhasil ditambahkan!");
                
                // Reload data
                loadAllBarang();
                applyFiltersAndDisplay();
            } else {
                AlertUtil.showError("Gagal", "Gagal menambahkan barang!");
            }
        });
    }

    /**
     * ✅ Handler untuk edit barang (khusus instansi)
     */
   /**
     * ✅ Handler untuk edit barang (khusus instansi) - WITH DELETE OPTION
     */
    private void handleEditBarang(Barang barang) {
        Dialog<Barang> dialog = new Dialog<>();
        dialog.setTitle("Edit Barang");
        dialog.setHeaderText(barang.getNamaBarang() + " (" + barang.getKodeBarang() + ")");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Field Jumlah Total
        HBox boxTotal = new HBox(10);
        boxTotal.setAlignment(Pos.CENTER_LEFT);
        Label lblTotal = new Label("Jumlah Total:");
        lblTotal.setPrefWidth(120);
        Spinner<Integer> spinnerTotal = new Spinner<>(0, 9999, barang.getJumlahTotal());
        spinnerTotal.setEditable(true);
        spinnerTotal.setPrefWidth(150);
        boxTotal.getChildren().addAll(lblTotal, spinnerTotal);

        // Field Jumlah Tersedia
        HBox boxTersedia = new HBox(10);
        boxTersedia.setAlignment(Pos.CENTER_LEFT);
        Label lblTersedia = new Label("Jumlah Tersedia:");
        lblTersedia.setPrefWidth(120);
        Spinner<Integer> spinnerTersedia = new Spinner<>(0, 9999, barang.getJumlahTersedia());
        spinnerTersedia.setEditable(true);
        spinnerTersedia.setPrefWidth(150);
        boxTersedia.getChildren().addAll(lblTersedia, spinnerTersedia);

        // Field Kondisi
        HBox boxKondisi = new HBox(10);
        boxKondisi.setAlignment(Pos.CENTER_LEFT);
        Label lblKondisi = new Label("Kondisi:");
        lblKondisi.setPrefWidth(120);
        ComboBox<String> cbKondisi = new ComboBox<>();
        cbKondisi.getItems().addAll("baik", "rusak ringan", "rusak berat");
        cbKondisi.setValue(barang.getKondisiBarang());
        cbKondisi.setPrefWidth(150);
        boxKondisi.getChildren().addAll(lblKondisi, cbKondisi);

        // Field Status
        HBox boxStatus = new HBox(10);
        boxStatus.setAlignment(Pos.CENTER_LEFT);
        Label lblStatus = new Label("Status:");
        lblStatus.setPrefWidth(120);
        ComboBox<String> cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll("tersedia", "dipinjam", "rusak", "hilang");
        cbStatus.setValue(barang.getStatus());
        cbStatus.setPrefWidth(150);
        boxStatus.getChildren().addAll(lblStatus, cbStatus);

        content.getChildren().addAll(boxTotal, boxTersedia, boxKondisi, boxStatus);
        dialog.getDialogPane().setContent(content);
        
        // ✅ TAMBAH TOMBOL DELETE
        ButtonType btnDelete = new ButtonType("🗑️ Hapus Barang", ButtonBar.ButtonData.LEFT);
        dialog.getDialogPane().getButtonTypes().addAll(btnDelete, ButtonType.OK, ButtonType.CANCEL);

        // ✅ Styling tombol delete jadi merah
        Button deleteButton = (Button) dialog.getDialogPane().lookupButton(btnDelete);
        if (deleteButton != null) {
            deleteButton.setStyle(
                "-fx-background-color: #D32F2F; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-cursor: hand;"
            );
        }

        dialog.setResultConverter(btn -> {
            if (btn == btnDelete) {
                // ✅ HANDLE DELETE
                handleDeleteBarangFromDialog(barang);
                return null; // Return null agar dialog tutup
                
            } else if (btn == ButtonType.OK) {
                // Validasi
                int total = spinnerTotal.getValue();
                int tersedia = spinnerTersedia.getValue();
                
                if (tersedia > total) {
                    AlertUtil.showWarning("Validasi", "Jumlah tersedia tidak boleh lebih dari jumlah total!");
                    return null;
                }
                
                barang.setJumlahTotal(total);
                barang.setJumlahTersedia(tersedia);
                barang.setKondisiBarang(cbKondisi.getValue());
                barang.setStatus(cbStatus.getValue());
                
                return barang;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedBarang -> {
            if (barangDAO.update(updatedBarang)) {
                AlertUtil.showSuccess("Berhasil", "Barang berhasil diupdate!");
                loadAllBarang();
                applyFiltersAndDisplay();
            } else {
                AlertUtil.showError("Gagal", "Gagal mengupdate barang!");
            }
        });
    }

    /**
     * ✅ NEW: Handler untuk hapus barang dari dialog edit
     */
    private void handleDeleteBarangFromDialog(Barang barang) {
        // Konfirmasi hapus
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Hapus");
        confirmAlert.setHeaderText("Hapus Barang: " + barang.getNamaBarang());
        confirmAlert.setContentText(
            "Apakah Anda yakin ingin menghapus barang ini?\n\n" +
            "Kode: " + barang.getKodeBarang() + "\n" +
            "Nama: " + barang.getNamaBarang() + "\n\n" +
            "⚠️ Tindakan ini tidak dapat dibatalkan!"
        );
        
        // Custom button
        ButtonType btnYes = new ButtonType("Ya, Hapus", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNo = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmAlert.getButtonTypes().setAll(btnYes, btnNo);
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == btnYes) {
                // Cek apakah barang sedang dipinjam
                if (isBarangDipinjam(barang.getKodeBarang())) {
                    AlertUtil.showError("Tidak Dapat Dihapus", 
                        "Barang sedang dipinjam!\n" +
                        "Tidak dapat menghapus barang yang masih dalam peminjaman.");
                    return;
                }
                
                // Hapus dari database
                if (barangDAO.delete(barang.getKodeBarang())) {
                    AlertUtil.showSuccess("Berhasil", 
                        "Barang \"" + barang.getNamaBarang() + "\" berhasil dihapus!");
                    
                    // Reload data
                    loadAllBarang();
                    applyFiltersAndDisplay();
                } else {
                    AlertUtil.showError("Gagal", "Gagal menghapus barang!");
                }
            }
        });
    }

    /**
     * ✅ NEW: Cek apakah barang sedang dipinjam
     */
    private boolean isBarangDipinjam(String kodeBarang) {
        try {
            java.sql.Connection conn = com.inventaris.config.DatabaseConfig.getInstance().getConnection();
            String sql = "SELECT COUNT(*) FROM borrow " +
                         "WHERE kode_barang = ? AND status_barang = 'dipinjam'";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, kodeBarang);
            java.sql.ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                rs.close();
                stmt.close();
                conn.close();
                return count > 0;
            }
            
            rs.close();
            stmt.close();
            conn.close();
            return false;
            
        } catch (Exception e) {
            System.err.println("Error checking barang status: " + e.getMessage());
            e.printStackTrace();
            return true; // Anggap dipinjam jika error (untuk safety)
        }
    }

   private void showAddToCartDialog(Barang barang) {
        Dialog<CartItem> dialog = new Dialog<>();
        dialog.setTitle("Tambah Keranjang");
        dialog.setHeaderText(barang.getNamaBarang());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        HBox boxJumlah = new HBox(10);
        boxJumlah.getChildren().add(new Label("Jumlah:"));
        Spinner<Integer> spinner = new Spinner<>(1, barang.getJumlahTersedia(), 1);
        spinner.setEditable(true);
        boxJumlah.getChildren().add(spinner);
        boxJumlah.setAlignment(Pos.CENTER_LEFT);

        DatePicker dpPinjam = new DatePicker(LocalDate.now());
        DatePicker dpKembali = new DatePicker(LocalDate.now().plusDays(7));
        
        HBox boxPinjam = new HBox(10);
        boxPinjam.getChildren().addAll(new Label("Tgl Pinjam: "), dpPinjam);
        
        HBox boxKembali = new HBox(10);
        boxKembali.getChildren().addAll(new Label("Tgl Kembali:"), dpKembali);
        
        VBox dateBox = new VBox(10);
        dateBox.getChildren().addAll(boxPinjam, boxKembali);

        content.getChildren().addAll(boxJumlah, dateBox);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                CartItem item = new CartItem();
                item.setBarang(barang);
                item.setJumlahPinjam(spinner.getValue());
                item.setTglPinjam(dpPinjam.getValue());
                item.setTglKembali(dpKembali.getValue());
                return item;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(this::addToCart);
    }

    private void addToCart(CartItem item) {
        if (!item.isValid()) {
            AlertUtil.showWarning("Validasi", item.getValidationError());
            return;
        }
        if (CartManager.getInstance().hasBarang(item.getBarang().getIdBarang())) {
            AlertUtil.showWarning("Info", "Barang sudah ada di keranjang.");
            return;
        }
        CartManager.getInstance().addItem(item);
        updateCartBadge();
        AlertUtil.showSuccess("Sukses", "Masuk keranjang!");
    }

    @FXML
    public void handlePeminjaman() {
        if (LayoutController.getInstance() != null) {
            LayoutController.getInstance().handlePeminjaman();
        }
    }
    
    private void updateCartBadge() {
        if (cartBadge != null) {
            int count = CartManager.getInstance().getCart().size();
            cartBadge.setText(String.valueOf(count));
            cartBadge.setVisible(count > 0);
        }
    }

    private void loadPage(String fxmlPath) {
        try {
            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            currentContent = loader.load();
            contentArea.getChildren().add(currentContent);
        } catch (IOException e) {
            System.err.println("❌ Gagal memuat halaman: " + fxmlPath);
            e.printStackTrace();
        }
    }
    
    @FXML 
    private void handleApplyFilter() { 
        applyFiltersAndDisplay(); 
    }
}