package com.financetracker.student_finance_app.controller;

import com.financetracker.student_finance_app.model.Mahasiswa;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Cek apakah user sudah login
        if (session.getAttribute("userLogin") == null) {
            return "redirect:/auth/login";
        }

        // Ambil data mahasiswa dari session
        Mahasiswa mahasiswa = (Mahasiswa) session.getAttribute("userLogin");

        // Kirim data ke View lewat Model
        // Saldo dari object Mahasiswa (real data)
        model.addAttribute("nama", mahasiswa.getNama());
        model.addAttribute("saldo", mahasiswa.getSaldo());

        // Data dummy -- nanti disambung ke modul teman
        model.addAttribute("totalPemasukan", 1500000);
        model.addAttribute("totalPengeluaran", 750000);

        // Dummy transaksi terakhir
        model.addAttribute("transaksiTerakhir", List.of(
            Map.of("tanggal", "2026-06-20", "keterangan", "Uang kiriman orang tua", "jenis", "PEMASUKAN", "jumlah", 1500000),
            Map.of("tanggal", "2026-06-19", "keterangan", "Beli makan siang", "jenis", "PENGELUARAN", "jumlah", 25000),
            Map.of("tanggal", "2026-06-18", "keterangan", "Bayar kos", "jenis", "PENGELUARAN", "jumlah", 500000),
            Map.of("tanggal", "2026-06-17", "keterangan", "Beli buku", "jenis", "PENGELUARAN", "jumlah", 85000),
            Map.of("tanggal", "2026-06-16", "keterangan", "Beasiswa", "jenis", "PEMASUKAN", "jumlah", 750000)
        ));

        return "dashboard";
    }
}