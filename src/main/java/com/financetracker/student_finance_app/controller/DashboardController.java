package com.financetracker.student_finance_app.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.financetracker.student_finance_app.model.Mahasiswa;
import com.financetracker.student_finance_app.model.OrangTua;
import com.financetracker.student_finance_app.repository.UserRepository;
import com.financetracker.student_finance_app.service.TransaksiService;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    @Autowired
    private TransaksiService transaksiService;

    @Autowired
    private UserRepository userRepository; // ✅ FIX: tambah untuk refresh session

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Object userLogin = session.getAttribute("userLogin");
        if (userLogin == null) return "redirect:/auth/login";

        // ✅ FIX: Arahkan orang tua ke dashboard khusus (sebelumnya bisa NullPointerException)
        if (userLogin instanceof OrangTua) {
            return "redirect:/orangtua/dashboard";
        }

        Mahasiswa mahasiswa = (Mahasiswa) userLogin;

        // ✅ FIX: Refresh dari DB supaya saldo yang tampil selalu terbaru
        mahasiswa = (Mahasiswa) userRepository.findById(mahasiswa.getId()).orElse(mahasiswa);
        session.setAttribute("userLogin", mahasiswa);

        Map<String, Object> statistik = transaksiService.getStatistikBulanan(mahasiswa.getId());

        model.addAttribute("nama", mahasiswa.getNama());
        model.addAttribute("saldo", mahasiswa.getSaldo());
        model.addAttribute("totalPemasukan",  statistik.get("totalPemasukan"));
        model.addAttribute("totalPengeluaran", statistik.get("totalPengeluaran"));

        var semua = transaksiService.getRiwayat(mahasiswa.getId());
        model.addAttribute("transaksiTerakhir",
                semua.size() > 5 ? semua.subList(0, 5) : semua);

        return "dashboard";
    }
}