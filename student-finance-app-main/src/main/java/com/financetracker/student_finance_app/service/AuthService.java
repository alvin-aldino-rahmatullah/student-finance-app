package com.financetracker.student_finance_app.service;

import com.financetracker.student_finance_app.model.Mahasiswa;
import com.financetracker.student_finance_app.model.OrangTua;
import com.financetracker.student_finance_app.model.User;
import com.financetracker.student_finance_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // REGISTRASI

    public Mahasiswa daftarMahasiswa(String nama, String noHP, String email, String password) {
        if (userRepository.existsByEmail(email)) return null;
        Mahasiswa mahasiswa = new Mahasiswa(nama, noHP, passwordEncoder.encode(password), email);
        return userRepository.save(mahasiswa);
    }

    public OrangTua daftarOrangTua(String nama, String noHP, String password) {
        if (userRepository.existsByNoHP(noHP)) return null;
        OrangTua orangTua = new OrangTua(nama, noHP, passwordEncoder.encode(password));
        return userRepository.save(orangTua);
    }

    // LOGIN

    public Mahasiswa loginMahasiswa(String email, String inputPassword) {
        Optional<Mahasiswa> optional = userRepository.findByEmail(email);
        if (optional.isEmpty()) return null;
        Mahasiswa mahasiswa = optional.get();
        if (!passwordEncoder.matches(inputPassword, mahasiswa.getPassword())) return null;
        return mahasiswa;
    }

    public OrangTua loginOrangTua(String noHP, String inputPassword) {
        Optional<OrangTua> optional = userRepository.findByNoHP(noHP);
        if (optional.isEmpty()) return null;
        OrangTua orangTua = optional.get();
        if (!passwordEncoder.matches(inputPassword, orangTua.getPassword())) return null;
        return orangTua;
    }

    // MANAJEMEN USER (kelola akun sendiri)

    public Optional<User> cariUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Edit profil user -- update nama, noHP, dan email (khusus Mahasiswa).
     * Mengembalikan user yang sudah diupdate, atau null kalau tidak ditemukan.
     */
    public User editProfil(Long id, String nama, String noHP, String email) {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isEmpty()) return null;

        User user = optional.get();
        user.setNama(nama);
        user.setNoHP(noHP);

        // Email hanya ada di Mahasiswa
        if (user instanceof Mahasiswa mahasiswa) {
            mahasiswa.setEmail(email);
        }

        return userRepository.save(user);
    }

    /**
     * Ganti password -- verifikasi password lama dulu sebelum ganti.
     * Mengembalikan true kalau berhasil, false kalau password lama salah.
     */
    public boolean gantiPassword(Long id, String passwordLama, String passwordBaru) {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isEmpty()) return false;

        User user = optional.get();

        // Verifikasi password lama dulu
        if (!passwordEncoder.matches(passwordLama, user.getPassword())) return false;

        // Set password baru yang sudah di-hash
        user.setPassword(passwordEncoder.encode(passwordBaru));
        userRepository.save(user);
        return true;
    }

    /**
     * Hapus akun sendiri -- user menghapus akunnya sendiri.
     */
    public void hapusAkun(Long id) {
        userRepository.deleteById(id);
    }
}