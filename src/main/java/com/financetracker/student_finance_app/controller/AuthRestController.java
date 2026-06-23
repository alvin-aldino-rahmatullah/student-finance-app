package com.financetracker.student_finance_app.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financetracker.student_finance_app.model.Mahasiswa;
import com.financetracker.student_finance_app.model.OrangTua;
import com.financetracker.student_finance_app.model.User;
import com.financetracker.student_finance_app.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private AuthService authService;

    // ===================================================
    // REGISTRASI
    // ===================================================

    @PostMapping("/daftar/mahasiswa")
    public ResponseEntity<Map<String, Object>> daftarMahasiswa(
            @RequestBody Map<String, String> body) {

        Mahasiswa mahasiswa = authService.daftarMahasiswa(
            body.get("nama"), body.get("noHP"),
            body.get("email"), body.get("password")
        );

        Map<String, Object> response = new HashMap<>();
        if (mahasiswa == null) {
            response.put("pesan", "Email sudah terdaftar.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        response.put("pesan", "Registrasi mahasiswa berhasil");
        response.put("id", mahasiswa.getId());
        response.put("nama", mahasiswa.getNama());
        response.put("email", mahasiswa.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/daftar/orangtua")
    public ResponseEntity<Map<String, Object>> daftarOrangTua(
            @RequestBody Map<String, String> body) {

        OrangTua orangTua = authService.daftarOrangTua(
    body.get("nama"),
    body.get("noHP"),
    body.get("password"),
    body.get("noHPMahasiswa")
);

        Map<String, Object> response = new HashMap<>();
        if (orangTua == null) {
            response.put("pesan", "Nomor HP sudah terdaftar.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        response.put("pesan", "Registrasi orang tua berhasil");
        response.put("id", orangTua.getId());
        response.put("nama", orangTua.getNama());
        response.put("noHP", orangTua.getNoHP());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ===================================================
    // LOGIN
    // ===================================================

    @PostMapping("/login/mahasiswa")
    public ResponseEntity<Map<String, Object>> loginMahasiswa(
            @RequestBody Map<String, String> body) {

        Mahasiswa mahasiswa = authService.loginMahasiswa(
            body.get("email"), body.get("password")
        );

        Map<String, Object> response = new HashMap<>();
        if (mahasiswa == null) {
            response.put("pesan", "Email atau password salah.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        response.put("pesan", "Login berhasil");
        response.put("id", mahasiswa.getId());
        response.put("nama", mahasiswa.getNama());
        response.put("email", mahasiswa.getEmail());
        response.put("saldo", mahasiswa.getSaldo());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/orangtua")
    public ResponseEntity<Map<String, Object>> loginOrangTua(
            @RequestBody Map<String, String> body) {

        OrangTua orangTua = authService.loginOrangTua(
            body.get("noHP"), body.get("password")
        );

        Map<String, Object> response = new HashMap<>();
        if (orangTua == null) {
            response.put("pesan", "Nomor HP atau password salah.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        response.put("pesan", "Login berhasil");
        response.put("id", orangTua.getId());
        response.put("nama", orangTua.getNama());
        response.put("noHP", orangTua.getNoHP());
        return ResponseEntity.ok(response);
    }

    // ===================================================
    // MANAJEMEN USER (kelola akun sendiri)
    // ===================================================

    @GetMapping("/profil/{id}")
    public ResponseEntity<Map<String, Object>> lihatProfil(@PathVariable Long id) {
        Optional<User> optional = authService.cariUserById(id);
        Map<String, Object> response = new HashMap<>();
        if (optional.isEmpty()) {
            response.put("pesan", "User tidak ditemukan.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("data", optional.get());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profil/{id}/edit")
    public ResponseEntity<Map<String, Object>> editProfil(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        User updated = authService.editProfil(
            id, body.get("nama"), body.get("noHP"), body.get("email")
        );

        Map<String, Object> response = new HashMap<>();
        if (updated == null) {
            response.put("pesan", "User tidak ditemukan.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("pesan", "Profil berhasil diupdate.");
        response.put("data", updated);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profil/{id}/ganti-password")
    public ResponseEntity<Map<String, Object>> gantiPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        boolean berhasil = authService.gantiPassword(
            id, body.get("passwordLama"), body.get("passwordBaru")
        );

        Map<String, Object> response = new HashMap<>();
        if (!berhasil) {
            response.put("pesan", "Password lama salah.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        response.put("pesan", "Password berhasil diubah.");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/profil/{id}/hapus-akun")
    public ResponseEntity<Map<String, Object>> hapusAkun(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        if (authService.cariUserById(id).isEmpty()) {
            response.put("pesan", "User tidak ditemukan.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        authService.hapusAkun(id);
        response.put("pesan", "Akun berhasil dihapus.");
        return ResponseEntity.ok(response);
    }
}