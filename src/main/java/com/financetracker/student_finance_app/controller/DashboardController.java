package com.financetracker.student_finance_app.controller;

import com.financetracker.student_finance_app.model.Mahasiswa;
import com.financetracker.student_finance_app.service.TransaksiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private TransaksiService transaksiService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("userLogin") == null) {
            return "redirect:/auth/login";
        }

        Mahasiswa mahasiswa = (Mahasiswa) session.getAttribute("userLogin");
        Map<String, Object> statistik = transaksiService.getStatistikBulanan(mahasiswa.getId());

        model.addAttribute("nama", mahasiswa.getNama());
        model.addAttribute("saldo", mahasiswa.getSaldo());
        model.addAttribute("totalPemasukan",  statistik.get("totalPemasukan"));
        model.addAttribute("totalPengeluaran", statistik.get("totalPengeluaran"));

        // 5 transaksi terakhir (real dari DB)
        var semua = transaksiService.getRiwayat(mahasiswa.getId());
        model.addAttribute("transaksiTerakhir",
                semua.size() > 5 ? semua.subList(0, 5) : semua);

        return "dashboard";
    }
}
