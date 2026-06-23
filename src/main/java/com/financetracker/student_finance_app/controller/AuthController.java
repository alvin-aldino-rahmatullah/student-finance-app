package com.financetracker.student_finance_app.controller;

import com.financetracker.student_finance_app.model.Mahasiswa;
import com.financetracker.student_finance_app.model.OrangTua;
import com.financetracker.student_finance_app.model.User;
import com.financetracker.student_finance_app.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String halamanLogin() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String prosesLogin(
            @RequestParam String tipeUser,
            @RequestParam String identifier,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (tipeUser.equals("mahasiswa")) {
            Mahasiswa mahasiswa = authService.loginMahasiswa(identifier, password);
            if (mahasiswa == null) {
                redirectAttributes.addFlashAttribute("error", "Email atau password salah.");
                return "redirect:/auth/login";
            }
            session.setAttribute("userLogin", mahasiswa);
            session.setAttribute("tipeUser", "mahasiswa");
            return "redirect:/dashboard";

        } else if (tipeUser.equals("orangtua")) {
            OrangTua orangTua = authService.loginOrangTua(identifier, password);
            if (orangTua == null) {
                redirectAttributes.addFlashAttribute("error", "Nomor HP atau password salah.");
                return "redirect:/auth/login";
            }
            session.setAttribute("userLogin", orangTua);
            session.setAttribute("tipeUser", "orangtua");
            // Orang tua diarahkan ke dashboard khusus orang tua
            return "redirect:/orangtua/dashboard";

        } else {
            redirectAttributes.addFlashAttribute("error", "Tipe pengguna tidak valid.");
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/daftar")
    public String halamanDaftar() {
        return "auth/daftar";
    }

    @PostMapping("/daftar/mahasiswa")
    public String daftarMahasiswa(
            @RequestParam String nama,
            @RequestParam String noHP,
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {

        Mahasiswa mahasiswa = authService.daftarMahasiswa(nama, noHP, email, password);
        if (mahasiswa == null) {
            redirectAttributes.addFlashAttribute("error", "Email sudah terdaftar.");
            return "redirect:/auth/daftar";
        }
        redirectAttributes.addFlashAttribute("sukses", "Registrasi berhasil! Silakan login.");
        return "redirect:/auth/login";
    }

    @PostMapping("/daftar/orangtua")
    public String daftarOrangTua(
            @RequestParam String nama,
            @RequestParam String noHP,
            @RequestParam String password,
            @RequestParam(required = false) String noHPMahasiswa,
            RedirectAttributes redirectAttributes) {

        OrangTua orangTua = authService.daftarOrangTua(nama, noHP, password, noHPMahasiswa);
        if (orangTua == null) {
            redirectAttributes.addFlashAttribute("error", "Nomor HP sudah terdaftar.");
            return "redirect:/auth/daftar";
        }
        redirectAttributes.addFlashAttribute("sukses", "Registrasi berhasil! Silakan login.");
        return "redirect:/auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }

    @GetMapping("/profil")
    public String halamanProfil(HttpSession session, Model model) {
        if (session.getAttribute("userLogin") == null) return "redirect:/auth/login";
        model.addAttribute("user", session.getAttribute("userLogin"));
        return "auth/profil";
    }

    @PostMapping("/profil/edit")
    public String editProfil(
            @RequestParam String nama,
            @RequestParam String noHP,
            @RequestParam(required = false) String email,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("userLogin");
        User updated = authService.editProfil(user.getId(), nama, noHP, email);
        if (updated == null) {
            redirectAttributes.addFlashAttribute("error", "Gagal update profil.");
            return "redirect:/auth/profil";
        }
        session.setAttribute("userLogin", updated);
        redirectAttributes.addFlashAttribute("sukses", "Profil berhasil diupdate.");
        return "redirect:/auth/profil";
    }

    @PostMapping("/profil/ganti-password")
    public String gantiPassword(
            @RequestParam String passwordLama,
            @RequestParam String passwordBaru,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("userLogin");
        boolean berhasil = authService.gantiPassword(user.getId(), passwordLama, passwordBaru);
        if (!berhasil) {
            redirectAttributes.addFlashAttribute("errorPassword", "Password lama salah.");
            return "redirect:/auth/profil";
        }
        redirectAttributes.addFlashAttribute("suksesPassword", "Password berhasil diubah.");
        return "redirect:/auth/profil";
    }

    @PostMapping("/profil/hapus-akun")
    public String hapusAkun(HttpSession session) {
        User user = (User) session.getAttribute("userLogin");
        authService.hapusAkun(user.getId());
        session.invalidate();
        return "redirect:/auth/login";
    }
}
