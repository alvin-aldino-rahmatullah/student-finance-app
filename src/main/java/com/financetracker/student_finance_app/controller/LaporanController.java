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
        return "laporan/index";
    }

    @PostMapping("/kirim")
    public String kirimLaporan(HttpSession session,
                               @RequestParam(defaultValue = "bulanan") String periode,
                               RedirectAttributes ra) {
        Mahasiswa mhs = (Mahasiswa) session.getAttribute("userLogin");
        if (mhs == null) return "redirect:/auth/login";

        try {
            laporanService.kirimKeOrangTua(mhs.getId(), periode);
            ra.addFlashAttribute("sukses",
                    "Laporan berhasil dikirim ke orang tua! (No HP: " + mhs.getNoHP() + ")");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Gagal mengirim laporan: " + e.getMessage());
        }
        return "redirect:/laporan?periode=" + periode;
    }
}
