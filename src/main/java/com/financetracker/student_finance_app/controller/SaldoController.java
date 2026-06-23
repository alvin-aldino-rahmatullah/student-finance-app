package com.financetracker.student_finance_app.controller;

import com.financetracker.student_finance_app.model.Mahasiswa;
import com.financetracker.student_finance_app.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/saldo")
public class SaldoController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String lihatSaldo(HttpSession session, Model model) {
        Mahasiswa mhs = (Mahasiswa) session.getAttribute("userLogin");
        if (mhs == null) return "redirect:/auth/login";
        // Refresh dari DB supaya saldo selalu up-to-date
        mhs = (Mahasiswa) userRepository.findById(mhs.getId()).orElse(mhs);
        session.setAttribute("userLogin", mhs);
        model.addAttribute("nama", mhs.getNama());
        model.addAttribute("saldo", mhs.getSaldo());
        return "saldo/index";
    }

    @PostMapping("/tambah")
    public String tambahSaldo(HttpSession session,
                              @RequestParam double jumlah,
                              RedirectAttributes ra) {
        Mahasiswa mhs = (Mahasiswa) session.getAttribute("userLogin");
        if (mhs == null) return "redirect:/auth/login";
        if (jumlah <= 0) {
            ra.addFlashAttribute("error", "Jumlah harus lebih dari 0.");
            return "redirect:/saldo";
        }
        mhs = (Mahasiswa) userRepository.findById(mhs.getId()).orElse(mhs);
        mhs.tambahSaldo(jumlah);
        userRepository.save(mhs);
        session.setAttribute("userLogin", mhs);
        ra.addFlashAttribute("sukses", "Saldo berhasil ditambahkan sebesar Rp " +
                String.format("%,.0f", jumlah));
        return "redirect:/saldo";
    }
}
