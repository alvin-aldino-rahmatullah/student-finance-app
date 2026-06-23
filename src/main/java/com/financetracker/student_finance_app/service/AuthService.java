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

    public Mahasiswa daftarMahasiswa(String nama, String noHP, String email, String password) {
        if (userRepository.existsByEmail(email)) return null;
        Mahasiswa mahasiswa = new Mahasiswa(nama, noHP, passwordEncoder.encode(password), email);
        return userRepository.save(mahasiswa);
    }

    public OrangTua daftarOrangTua(String nama, String noHP, String password, String noHPMahasiswa) {
        if (userRepository.existsByNoHP(noHP)) return null;
        OrangTua orangTua = new OrangTua(nama, noHP, passwordEncoder.encode(password), noHPMahasiswa);
        return userRepository.save(orangTua);
    }

    public Mahasiswa loginMahasiswa(String email, String inputPassword) {
        Optional<Mahasiswa> optional = userRepository.findByEmail(email);
        if (optional.isEmpty()) return null;
        Mahasiswa mahasiswa = optional.get();
        if (!passwordEncoder.matches(inputPassword, mahasiswa.getPassword())) return null;
        return mahasiswa;
    }

    public OrangTua loginOrangTua(String noHP, String inputPassword) {
        Optional<OrangTua> optional = userRepository.findOrangTuaByNoHP(noHP);
        if (optional.isEmpty()) return null;
        OrangTua orangTua = optional.get();
        if (!passwordEncoder.matches(inputPassword, orangTua.getPassword())) return null;
        return orangTua;
    }

    public Optional<User> cariUserById(Long id) {
        return userRepository.findById(id);
    }

    public User editProfil(Long id, String nama, String noHP, String email) {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isEmpty()) return null;
        User user = optional.get();
        user.setNama(nama);
        user.setNoHP(noHP);
        if (user instanceof Mahasiswa mahasiswa) mahasiswa.setEmail(email);
        return userRepository.save(user);
    }

    public boolean gantiPassword(Long id, String passwordLama, String passwordBaru) {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isEmpty()) return false;
        User user = optional.get();
        if (!passwordEncoder.matches(passwordLama, user.getPassword())) return false;
        user.setPassword(passwordEncoder.encode(passwordBaru));
        userRepository.save(user);
        return true;
    }

    public void hapusAkun(Long id) {
        userRepository.deleteById(id);
    }
}
