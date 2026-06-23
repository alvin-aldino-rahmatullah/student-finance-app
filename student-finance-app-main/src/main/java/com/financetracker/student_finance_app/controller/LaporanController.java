package com.financetracker.student_finance_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financetracker.student_finance_app.model.Mahasiswa;
import com.financetracker.student_finance_app.service.LaporanService;

@RestController
@RequestMapping("/laporan")
public class LaporanController {

    @Autowired
    private LaporanService laporanService;

    @GetMapping(produces = "text/html")
    public String getLaporan() {

        Mahasiswa mahasiswa = new Mahasiswa(
                "Trisna Kusuma",
                "08123456789",
                "123456",
                "trisna@gmail.com"
        );

        mahasiswa.tambahSaldo(500000);

        return laporanService.generateLaporan(mahasiswa);
    }
}