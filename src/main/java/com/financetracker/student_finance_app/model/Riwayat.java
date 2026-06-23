package com.financetracker.student_finance_app.model;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class Riwayat {

    private String periode;
    private List<Transaksi> semuaTransaksi;

    public Riwayat(String periode, List<Transaksi> semuaTransaksi) {
        this.periode = periode;
        this.semuaTransaksi = semuaTransaksi;
    }

    public String getPeriode() { return periode; }
    public List<Transaksi> getSemuaTransaksi() { return semuaTransaksi; }

    public List<Transaksi> filterMingguan() {
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        return semuaTransaksi.stream()
                .filter(t -> !t.getTanggal().isBefore(sevenDaysAgo))
                .collect(Collectors.toList());
    }

    public List<Transaksi> filterBulanan() {
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        return semuaTransaksi.stream()
                .filter(t -> !t.getTanggal().isBefore(firstDayOfMonth))
                .collect(Collectors.toList());
    }

    public double hitungPemasukan() {
        return semuaTransaksi.stream()
                .filter(t -> t.getJenis() == JenisTransaksi.PEMASUKAN)
                .mapToDouble(Transaksi::getJumlah)
                .sum();
    }

    public double hitungPengeluaran() {
        return semuaTransaksi.stream()
                .filter(t -> t.getJenis() == JenisTransaksi.PENGELUARAN)
                .mapToDouble(Transaksi::getJumlah)
                .sum();
    }
}
