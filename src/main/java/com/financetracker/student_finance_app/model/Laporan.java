package com.financetracker.student_finance_app.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Laporan {

    private String periode;
    private Mahasiswa mahasiswa;
    private List<Transaksi> transaksiList;
    private double totalPemasukan;
    private double totalPengeluaran;
    private double saldo;

    public Laporan(String periode, Mahasiswa mahasiswa, List<Transaksi> transaksiList,
                   double totalPemasukan, double totalPengeluaran) {
        this.periode = periode;
        this.mahasiswa = mahasiswa;
        this.transaksiList = transaksiList;
        this.totalPemasukan = totalPemasukan;
        this.totalPengeluaran = totalPengeluaran;
        this.saldo = mahasiswa.getSaldo();
    }

    public String getPeriode()             { return periode; }
    public Mahasiswa getMahasiswa()        { return mahasiswa; }
    public List<Transaksi> getTransaksiList() { return transaksiList; }
    public double getTotalPemasukan()      { return totalPemasukan; }
    public double getTotalPengeluaran()    { return totalPengeluaran; }
    public double getSaldo()               { return saldo; }
    public double getSelisih()             { return totalPemasukan - totalPengeluaran; }
    public String getTanggalGenerate()     { return LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")); }

    public void generatePDF()   { /* implementasi export PDF di LaporanController */ }
    public void kirimLaporan()  { /* implementasi kirim ke OrangTua */ }
}
