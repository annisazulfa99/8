package com.inventaris.controller;

import com.inventaris.Main;
import com.inventaris.dao.UserDAO;
import com.inventaris.model.User;
import com.inventaris.util.AlertUtil;
import com.inventaris.util.LogActivityUtil;
import com.inventaris.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Optional;

/**
 * UserController - Admin Management Panel
 * Manage users with CRUD operations
 */
public class UserController implements Initializable {
    
    // ============================================================
    // FXML COMPONENTS
    // ============================================================
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> colUserId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colNama;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colStatus;
    @FXML private TableColumn<User, Void> colUserAction;
    
    @FXML private TextField searchUserField;
    
    // DAOs
    private final UserDAO userDAO = new UserDAO();
    private final SessionManager sessionManager = SessionManager.getInstance();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Check admin access
        if (!sessionManager.isAdmin()) {
            AlertUtil.showError("Access Denied", "Hanya admin yang dapat mengakses halaman ini!");
            return;
        }
        
        // Setup tables
        setupUserTable();
        
        // Load data
        loadAllUsers();
        
        System.out.println("✅ User Management initialized");
    }
    
    // ============================================================
    // TABLE SETUP
    // ============================================================
    
    private void setupUserTable() {
        colUserId.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Warna Status
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null); 
                    setStyle("");
                } else {
                    setText(status);
                    if ("aktif".equals(status)) {
                        setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        // Tombol Aksi
        colUserAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnToggle = new Button();
            private final Button btnReset = new Button("🔑 Reset");
            private final HBox buttons = new HBox(5, btnToggle, btnReset);
            
            {
                btnToggle.getStyleClass().add("btn-warning");
                btnReset.getStyleClass().add("btn-secondary");
                
                btnToggle.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    toggleUserStatus(user);
                });
                
                btnReset.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    resetPassword(user);
                });
            }
            
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    btnToggle.setText("aktif".equals(user.getStatus()) ? "❌ Nonaktif" : "✅ Aktifkan");
                    
                    // Styling dinamis tombol
                    if ("aktif".equals(user.getStatus())) {
                        btnToggle.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black;");
                    } else {
                        btnToggle.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
                    }
                    
                    setGraphic(buttons);
                }
            }
        });
    }
    
    // ============================================================
    // LOAD DATA
    // ============================================================
    
    private void loadAllUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            
            // Filter berdasarkan search
            String keyword = "";
            if (searchUserField != null) {
                keyword = searchUserField.getText().toLowerCase().trim();
            }
            
            if (!keyword.isEmpty()) {
                String finalKeyword = keyword;
                users.removeIf(u -> 
                    !u.getUsername().toLowerCase().contains(finalKeyword) && 
                    !u.getNama().toLowerCase().contains(finalKeyword)
                );
            }
            
            ObservableList<User> observableList = FXCollections.observableArrayList(users);
            userTable.setItems(observableList);
            
        } catch (Exception e) {
            AlertUtil.showError("Error", "Gagal memuat data user!");
            e.printStackTrace();
        }
    }
    
    // ============================================================
    // CRUD OPERATIONS
    // ============================================================
    
    /**
     * ✨ FITUR: Tambah User Baru
     */
   /**
 * ✨ FITUR: Tambah User Baru (FIXED - Support Instansi)
 */
/**
 * ✨ FITUR: Tambah User Baru (FIXED - Validasi Diperbaiki)
 */
/**
 * ✨ FITUR: Tambah User Baru (FIXED - Dengan Tombol Simpan)
 */
/**
 * ✨ FITUR: Tambah User Baru (FIXED - Dengan Kategori Instansi)
 */
@FXML
private void handleAddUser() {
    // Buat dialog custom
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Tambah User Baru");
    dialog.setHeaderText("Masukkan data user baru");
    
    // Tombol OK dan Cancel
    ButtonType btnSave = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
    ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    dialog.getDialogPane().getButtonTypes().addAll(btnSave, btnCancel);
    
    // Form input
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 150, 10, 10));
    
    TextField txtUsername = new TextField();
    txtUsername.setPromptText("Username");
    
    PasswordField txtPassword = new PasswordField();
    txtPassword.setPromptText("Password");
    
    TextField txtNama = new TextField();
    txtNama.setPromptText("Nama Lengkap");
    
    TextField txtNoTelepon = new TextField();
    txtNoTelepon.setPromptText("No. Telepon");
    
    ComboBox<String> cmbRole = new ComboBox<>();
    cmbRole.getItems().addAll("admin", "peminjam", "instansi");
    cmbRole.setPromptText("Pilih Role");
    cmbRole.setValue("peminjam"); // Default
    
    // Field untuk nama instansi (awalnya hidden)
    TextField txtNamaInstansi = new TextField();
    txtNamaInstansi.setPromptText("Contoh: HIMATRONIKA-AI");
    Label lblNamaInstansi = new Label("Nama Instansi:");
    
    // Field untuk kategori instansi (awalnya hidden)
    ComboBox<String> cmbKategori = new ComboBox<>();
    cmbKategori.getItems().addAll("Himpunan", "UKM");
    cmbKategori.setPromptText("Pilih Kategori");
    Label lblKategori = new Label("Kategori:");
    
    // Susun form dasar
    grid.add(new Label("Username:"), 0, 0);
    grid.add(txtUsername, 1, 0);
    grid.add(new Label("Password:"), 0, 1);
    grid.add(txtPassword, 1, 1);
    grid.add(new Label("Nama Lengkap:"), 0, 2);
    grid.add(txtNama, 1, 2);
    grid.add(new Label("No. Telepon:"), 0, 3);
    grid.add(txtNoTelepon, 1, 3);
    grid.add(new Label("Role:"), 0, 4);
    grid.add(cmbRole, 1, 4);
    
    // Event listener untuk role ComboBox - tampilkan field instansi jika diperlukan
    cmbRole.valueProperty().addListener((obs, oldVal, newVal) -> {
        // Hapus field instansi terlebih dahulu
        grid.getChildren().removeAll(lblNamaInstansi, txtNamaInstansi, lblKategori, cmbKategori);
        
        // Tambahkan kembali jika role adalah instansi
        if ("instansi".equals(newVal)) {
            grid.add(lblNamaInstansi, 0, 5);
            grid.add(txtNamaInstansi, 1, 5);
            grid.add(lblKategori, 0, 6);
            grid.add(cmbKategori, 1, 6);
            txtNoTelepon.setPromptText("No. Telepon Instansi");
        } else if ("peminjam".equals(newVal)) {
            txtNoTelepon.setPromptText("No. Telepon Peminjam");
        } else {
            txtNoTelepon.setPromptText("No. Telepon");
        }
        
        // Force dialog to resize
        dialog.getDialogPane().getScene().getWindow().sizeToScene();
    });
    
    // Set content ke dialog
    dialog.getDialogPane().setContent(grid);
    
    // Disable tombol Simpan jika field kosong
    javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(btnSave);
    saveButton.setDisable(false); // Aktifkan tombol
    
    // Tampilkan dialog
    Optional<ButtonType> result = dialog.showAndWait();
    
    // Proses jika user klik Simpan
    if (result.isPresent() && result.get() == btnSave) {
        // Ambil nilai dari form
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String nama = txtNama.getText().trim();
        String noTelepon = txtNoTelepon.getText().trim();
        String role = cmbRole.getValue();
        String namaInstansi = txtNamaInstansi.getText().trim();
        String kategori = cmbKategori.getValue();
        
        // Validasi input
        if (username.isEmpty()) {
            AlertUtil.showError("Validasi", "Username tidak boleh kosong!");
            handleAddUser();
            return;
        }
        
        if (password.isEmpty()) {
            AlertUtil.showError("Validasi", "Password tidak boleh kosong!");
            handleAddUser();
            return;
        }
        
        if (password.length() < 6) {
            AlertUtil.showError("Validasi", "Password minimal 6 karakter!");
            handleAddUser();
            return;
        }
        
        if (nama.isEmpty()) {
            AlertUtil.showError("Validasi", "Nama tidak boleh kosong!");
            handleAddUser();
            return;
        }
        
        if (noTelepon.isEmpty()) {
            AlertUtil.showError("Validasi", "No. Telepon tidak boleh kosong!");
            handleAddUser();
            return;
        }
        
        if (role == null) {
            AlertUtil.showError("Validasi", "Role harus dipilih!");
            handleAddUser();
            return;
        }
        
        // Validasi khusus untuk role instansi
        if ("instansi".equals(role)) {
            if (namaInstansi.isEmpty()) {
                AlertUtil.showError("Validasi", "Nama Instansi tidak boleh kosong untuk role instansi!");
                handleAddUser();
                return;
            }
            
            if (kategori == null || kategori.isEmpty()) {
                AlertUtil.showError("Validasi", "Kategori Instansi harus dipilih (Himpunan/UKM)!");
                handleAddUser();
                return;
            }
        }
        
        // Cek username sudah ada atau belum
        if (userDAO.usernameExists(username)) {
            AlertUtil.showError("Validasi", "Username sudah digunakan!");
            handleAddUser();
            return;
        }
        
        // Buat user baru
        User newUser = new User(username, password, nama, role);
        newUser.setStatus("aktif");
        
        // Tentukan namaInstansi dan kategori berdasarkan role
        String finalNamaInstansi = "instansi".equals(role) ? namaInstansi : null;
        String finalKategori = "instansi".equals(role) ? kategori : null;
        
        // Simpan ke database (dengan parameter kategori tambahan)
        if (userDAO.registerWithKategori(newUser, noTelepon, finalNamaInstansi, finalKategori)) {
            AlertUtil.showSuccess("Berhasil", 
                "User berhasil ditambahkan!" + 
                ("instansi".equals(role) ? "\nKategori: " + kategori : ""));
            LogActivityUtil.log(
                sessionManager.getCurrentUsername(), 
                "Tambah user: " + username + " (role: " + role + 
                ("instansi".equals(role) ? ", kategori: " + kategori : "") + ")", 
                "CREATE_USER", 
                sessionManager.getCurrentRole()
            );
            loadAllUsers(); // Refresh table
        } else {
            AlertUtil.showError("Gagal", "Gagal menambahkan user ke database!");
        }
    }
}
    
    /**
     * Toggle status user (aktif/nonaktif)
     */
    private void toggleUserStatus(User user) {
        String newStatus = "aktif".equals(user.getStatus()) ? "nonaktif" : "aktif";
        String action = "aktif".equals(newStatus) ? "mengaktifkan" : "menonaktifkan";
        
        if (!AlertUtil.showConfirmation("Konfirmasi", "Yakin " + action + " user " + user.getUsername() + "?")) {
            return;
        }
        
        user.setStatus(newStatus);
        if (userDAO.updateUser(user)) {
            AlertUtil.showSuccess("Berhasil", "Status user berhasil diubah!");
            LogActivityUtil.log(
                sessionManager.getCurrentUsername(), 
                action + " user: " + user.getUsername(), 
                "UPDATE_USER_STATUS", 
                sessionManager.getCurrentRole()
            );
            loadAllUsers();
        } else {
            AlertUtil.showError("Gagal", "Gagal mengubah status user!");
        }
    }
    
    /**
     * Reset password user
     */
    private void resetPassword(User user) {
        String newPassword = AlertUtil.showInputDialog(
            "Reset Password", 
            "Reset password untuk: " + user.getUsername(), 
            "Masukkan password baru:"
        );
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return;
        }
        
        if (newPassword.length() < 6) {
            AlertUtil.showError("Validasi", "Password minimal 6 karakter!");
            return;
        }
        
        if (userDAO.changePassword(user.getIdUser(), newPassword)) {
            AlertUtil.showSuccess("Berhasil", "Password berhasil direset!");
            LogActivityUtil.log(
                sessionManager.getCurrentUsername(), 
                "Reset password user: " + user.getUsername(), 
                "RESET_PASSWORD", 
                sessionManager.getCurrentRole()
            );
        } else {
            AlertUtil.showError("Gagal", "Gagal reset password!");
        }
    }
    
    // ============================================================
    // SEARCH & FILTER
    // ============================================================
    
    @FXML
    private void handleSearchUser() {
        loadAllUsers();
    }
}