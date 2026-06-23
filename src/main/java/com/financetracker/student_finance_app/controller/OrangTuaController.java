package com.financetracker.student_finance_app.controller;

import com.financetracker.student_finance_app.model.Laporan;
import com.financetracker.student_finance_app.model.Mahasiswa;
import com.financetracker.student_finance_app.model.OrangTua;
import com.financetracker.student_finance_app.repository.UserRepository;
import com.financetracker.student_finance_app.service.LaporanService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/orangtua")
public class OrangTuaController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LaporanService laporanService;

    // Dashboard orang tua
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model,
                            @RequestParam(defaultValue = "bulanan") String periode) {
        OrangTua ot = getOrangTua(session);
        if (ot == null) return "redirect:/auth/login";

        model.addAttribute("nama", ot.getNama());
        model.addAttribute("periode", periode);

        // Cari mahasiswa yang terhubung berdasarkan noHPMahasiswa
        if (ot.getNoHPMahasiswa() != null && !ot.getNoHPMahasiswa().isBlank()) {
            Optional<Mahasiswa> mhsOpt = userRepository.findMahasiswaByNoHP(ot.getNoHPMahasiswa());
            if (mhsOpt.isPresent()) {
                Mahasiswa mhs = mhsOpt.get();
                Laporan laporan = laporanService.buatLaporan(mhs.getId(), periode);
                model.addAttribute("laporan", laporan);
                model.addAttribute("mahasiswa", mhs);
            } else {
                model.addAttribute("peringatan",
                    "Nomor HP mahasiswa (" + ot.getNoHPMahasiswa() + ") belum terdaftar di sistem.");
            }
        } else {
            model.addAttribute("peringatan",
                "Belum ada No HP mahasiswa yang ditautkan. Silakan update profil.");
        }

        return "orangtua/dashboard";
    }

    // Halaman profil orang tua (update noHPMahasiswa)
    @GetMapping("/profil")
    public String profil(HttpSession session, Model model) {
        OrangTua ot = getOrangTua(session);
        if (ot == null) return "redirect:/auth/login";
        model.addAttribute("orangTua", ot);
        return "orangtua/profil";
    }

    @PostMapping("/profil/update")
    public String updateProfil(HttpSession session,
                               @RequestParam String noHPMahasiswa,
                               org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        OrangTua ot = getOrangTua(session);
        if (ot == null) return "redirect:/auth/login";

        ot.setNoHPMahasiswa(noHPMahasiswa);
        userRepository.save(ot);
        session.setAttribute("userLogin", ot);

        ra.addFlashAttribute("sukses", "No HP mahasiswa berhasil disimpan!");
        return "redirect:/orangtua/dashboard";
    }

    private OrangTua getOrangTua(HttpSession session) {
        Object user = session.getAttribute("userLogin");
        if (user instanceof OrangTua ot) return ot;
        return null;
    }
}
