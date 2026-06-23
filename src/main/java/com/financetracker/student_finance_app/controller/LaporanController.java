package com.financetracker.student_finance_app.controller;

import com.financetracker.student_finance_app.model.Laporan;
import com.financetracker.student_finance_app.model.Mahasiswa;
import com.financetracker.student_finance_app.service.LaporanService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/laporan")
public class LaporanController {

    @Autowired
    private LaporanService laporanService;

    @GetMapping
    public String index(HttpSession session, Model model,
                        @RequestParam(defaultValue = "bulanan") String periode) {
        Mahasiswa mhs = (Mahasiswa) session.getAttribute("userLogin");
        if (mhs == null) return "redirect:/auth/login";

        Laporan laporan = laporanService.buatLaporan(mhs.getId(), periode);
        model.addAttribute("laporan", laporan);
        model.addAttribute("periode", periode);
        model.addAttribute("nama", mhs.getNama());
        model.addAttribute("noHPMahasiswa", mhs.getNoHP());
        return "laporan/index";
    }

    // Kirim ke orang tua yang sudah terdaftar (auto)
    @PostMapping("/kirim")
    public String kirimOtomatis(HttpSession session,
                                @RequestParam(defaultValue = "bulanan") String periode,
                                RedirectAttributes ra) {
        Mahasiswa mhs = (Mahasiswa) session.getAttribute("userLogin");
        if (mhs == null) return "redirect:/auth/login";

        boolean berhasil = laporanService.kirimKeOrangTuaWA(mhs.getId(), periode);
        if (berhasil) {
            ra.addFlashAttribute("sukses", "✅ Laporan berhasil dikirim ke WhatsApp orang tua!");
        } else {
            ra.addFlashAttribute("error",
                "❌ Gagal kirim otomatis. Pastikan orang tua sudah daftar dengan No HP mahasiswa yang benar, atau kirim ke nomor manual.");
        }
        return "redirect:/laporan?periode=" + periode;
    }

    // Kirim ke nomor WA tertentu (manual)
    @PostMapping("/kirim-manual")
    public String kirimManual(HttpSession session,
                              @RequestParam(defaultValue = "bulanan") String periode,
                              @RequestParam String noHP,
                              RedirectAttributes ra) {
        Mahasiswa mhs = (Mahasiswa) session.getAttribute("userLogin");
        if (mhs == null) return "redirect:/auth/login";

        if (noHP == null || noHP.isBlank()) {
            ra.addFlashAttribute("error", "Nomor HP tidak boleh kosong.");
            return "redirect:/laporan?periode=" + periode;
        }

        boolean berhasil = laporanService.kirimKeNomorWA(mhs.getId(), periode, noHP);
        if (berhasil) {
            ra.addFlashAttribute("sukses", "✅ Laporan berhasil dikirim ke " + noHP + "!");
        } else {
            ra.addFlashAttribute("error", "❌ Gagal mengirim laporan. Cek token Fonnte & nomor HP.");
        }
        return "redirect:/laporan?periode=" + periode;
    }
}
