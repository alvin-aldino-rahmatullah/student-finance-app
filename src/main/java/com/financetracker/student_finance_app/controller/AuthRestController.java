package com.financetracker.student_finance_app.controller;

import com.financetracker.student_finance_app.model.Mahasiswa;
import com.financetracker.student_finance_app.model.OrangTua;
import com.financetracker.student_finance_app.model.User;
import com.financetracker.student_finance_app.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private AuthService authService;

    @PostMapping("/daftar/mahasiswa")
    public ResponseEntity<Map<String, Object>> daftarMahasiswa(
            @RequestBody Map<String, String> body) {

        Mahasiswa mahasiswa = authService.daftarMahasiswa(
            body.get("nama"),
            body.get("noHP"),
            body.get("email"),
            body.get("password")
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
            body.get("password")
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

    @PostMapping("/login/mahasiswa")
    public ResponseEntity<Map<String, Object>> loginMahasiswa(
            @RequestBody Map<String, String> body) {

        Mahasiswa mahasiswa = authService.loginMahasiswa(
            body.get("email"),
            body.get("password")
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
            body.get("noHP"),
            body.get("password")
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

    @GetMapping("/users")
    public ResponseEntity<List<User>> semuaUser() {
        return ResponseEntity.ok(authService.semuaUser());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> userById(@PathVariable Long id) {
        Optional<User> optional = authService.cariUserById(id);
        Map<String, Object> response = new HashMap<>();
        if (optional.isEmpty()) {
            response.put("pesan", "User dengan ID " + id + " tidak ditemukan.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("data", optional.get());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> hapusUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        if (authService.cariUserById(id).isEmpty()) {
            response.put("pesan", "User dengan ID " + id + " tidak ditemukan.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        authService.hapusUser(id);
        response.put("pesan", "User berhasil dihapus.");
        return ResponseEntity.ok(response);
    }
}