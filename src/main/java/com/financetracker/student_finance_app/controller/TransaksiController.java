package com.financetracker.student_finance_app.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.financetracker.student_finance_app.model.JenisTransaksi;
import com.financetracker.student_finance_app.model.Mahasiswa;
import com.financetracker.student_finance_app.repository.UserRepository;
import com.financetracker.student_finance_app.service.TransaksiService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/transaksi")
public class TransaksiController {

    @Autowired
    private TransaksiService transaksiService;

    @Autowired
    private UserRepository userRepository; // ✅ FIX: tambah untuk refresh session

    private Mahasiswa getMahasiswa(HttpSession session) {
        return (Mahasiswa) session.getAttribute("userLogin");
    }

    /**
     * ✅ FIX: Refresh objek Mahasiswa dari DB lalu simpan ulang ke session.
     * Dipanggil setelah setiap operasi yang mengubah saldo.
     */
    private Mahasiswa refreshSession(HttpSession session, Long mahasiswaId) {
        Mahasiswa fresh = (Mahasiswa) userRepository.findById(mahasiswaId).orElse(null);
        if (fresh != null) {
            session.setAttribute("userLogin", fresh);
        }
        return fresh;
    }

    // ========== KELOLA TRANSAKSI ==========

    @GetMapping
    public String listTransaksi(HttpSession session, Model model) {
        Mahasiswa mhs = getMahasiswa(session);
        if (mhs == null) return "redirect:/auth/login";

        // ✅ FIX: Selalu ambil saldo terbaru dari DB saat buka halaman transaksi
        mhs = refreshSession(session, mhs.getId());
        if (mhs == null) return "redirect:/auth/login";

        model.addAttribute("transaksiList", transaksiService.getRiwayat(mhs.getId()));
        model.addAttribute("nama", mhs.getNama());
        model.addAttribute("saldo", mhs.getSaldo());
        return "transaksi/kelola";
    }

    @PostMapping("/tambah")
    public String tambah(HttpSession session,
                         @RequestParam JenisTransaksi jenis,
                         @RequestParam double jumlah,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal,
                         @RequestParam(required = false) String catatan,
                         @RequestParam(required = false) String kategori,
                         RedirectAttributes ra) {
        Mahasiswa mhs = getMahasiswa(session);
        if (mhs == null) return "redirect:/auth/login";
        try {
            transaksiService.tambah(mhs.getId(), jenis, jumlah, tanggal, catatan, kategori);

            // ✅ FIX: Refresh saldo di session setelah transaksi berhasil disimpan ke DB
            refreshSession(session, mhs.getId());

            ra.addFlashAttribute("sukses", "Transaksi berhasil ditambahkan!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Gagal: " + e.getMessage());
        }
        return "redirect:/transaksi";
    }

    @PostMapping("/hapus/{id}")
    public String hapus(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        Mahasiswa mhs = getMahasiswa(session);
        if (mhs == null) return "redirect:/auth/login";
        try {
            transaksiService.hapus(id);

            // ✅ FIX: Refresh saldo di session setelah transaksi dihapus
            refreshSession(session, mhs.getId());

            ra.addFlashAttribute("sukses", "Transaksi berhasil dihapus.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Gagal: " + e.getMessage());
        }
        return "redirect:/transaksi";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, HttpSession session,
                         @RequestParam JenisTransaksi jenis,
                         @RequestParam double jumlah,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal,
                         @RequestParam(required = false) String catatan,
                         @RequestParam(required = false) String kategori,
                         RedirectAttributes ra) {
        Mahasiswa mhs = getMahasiswa(session);
        if (mhs == null) return "redirect:/auth/login";
        try {
            transaksiService.update(id, jenis, jumlah, tanggal, catatan, kategori);

            // ✅ FIX: Refresh saldo di session setelah transaksi diupdate
            refreshSession(session, mhs.getId());

            ra.addFlashAttribute("sukses", "Transaksi berhasil diperbarui.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Gagal: " + e.getMessage());
        }
        return "redirect:/transaksi";
    }

    // ========== RIWAYAT ==========

    @GetMapping("/riwayat")
    public String riwayat(HttpSession session, Model model,
                          @RequestParam(defaultValue = "semua") String filter) {
        Mahasiswa mhs = getMahasiswa(session);
        if (mhs == null) return "redirect:/auth/login";
        var list = switch (filter) {
            case "mingguan" -> transaksiService.getRiwayatMingguan(mhs.getId());
            case "bulanan"  -> transaksiService.getRiwayatBulanan(mhs.getId());
            default         -> transaksiService.getRiwayat(mhs.getId());
        };
        model.addAttribute("transaksiList", list);
        model.addAttribute("filter", filter);
        model.addAttribute("nama", mhs.getNama());
        return "transaksi/riwayat";
    }

    // ========== STATISTIK & GRAFIK ==========

    @GetMapping("/statistik")
    public String statistik(HttpSession session, Model model) {
        Mahasiswa mhs = getMahasiswa(session);
        if (mhs == null) return "redirect:/auth/login";

        // ✅ FIX: Ambil saldo terbaru dari DB untuk halaman statistik
        mhs = refreshSession(session, mhs.getId());
        if (mhs == null) return "redirect:/auth/login";

        model.addAttribute("nama", mhs.getNama());
        model.addAttribute("saldo", mhs.getSaldo());
        model.addAttribute("statistik", transaksiService.getStatistikBulanan(mhs.getId()));
        model.addAttribute("kategoriPengeluaran", transaksiService.getKategoriPengeluaran(mhs.getId()));
        model.addAttribute("trendBulanan", transaksiService.getTrendBulanan(mhs.getId()));
        return "transaksi/statistik";
    }
}