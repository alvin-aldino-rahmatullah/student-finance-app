package com.financetracker.student_finance_app.service;

import com.financetracker.student_finance_app.model.Mahasiswa;
import com.financetracker.student_finance_app.model.OrangTua;
import com.financetracker.student_finance_app.model.User;
import com.financetracker.student_finance_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Mahasiswa daftarMahasiswa(String nama, String noHP, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            return null;
        }
        String hashedPassword = passwordEncoder.encode(password);
        Mahasiswa mahasiswa = new Mahasiswa(nama, noHP, hashedPassword, email);
        return userRepository.save(mahasiswa);
    }

    public OrangTua daftarOrangTua(String nama, String noHP, String password) {
        if (userRepository.existsByNoHP(noHP)) {
            return null;
        }
        String hashedPassword = passwordEncoder.encode(password);
        OrangTua orangTua = new OrangTua(nama, noHP, hashedPassword);
        return userRepository.save(orangTua);
    }

    public Mahasiswa loginMahasiswa(String email, String inputPassword) {
        Optional<Mahasiswa> optional = userRepository.findByEmail(email);
        if (optional.isEmpty()) {
            return null;
        }
        Mahasiswa mahasiswa = optional.get();
        if (!passwordEncoder.matches(inputPassword, mahasiswa.getPassword())) {
            return null;
        }
        return mahasiswa;
    }

    public OrangTua loginOrangTua(String noHP, String inputPassword) {
        Optional<OrangTua> optional = userRepository.findByNoHP(noHP);
        if (optional.isEmpty()) {
            return null;
        }
        OrangTua orangTua = optional.get();
        if (!passwordEncoder.matches(inputPassword, orangTua.getPassword())) {
            return null;
        }
        return orangTua;
    }

    public List<User> semuaUser() {
        return userRepository.findAll();
    }

    public Optional<User> cariUserById(Long id) {
        return userRepository.findById(id);
    }

    public void hapusUser(Long id) {
        userRepository.deleteById(id);
    }
}