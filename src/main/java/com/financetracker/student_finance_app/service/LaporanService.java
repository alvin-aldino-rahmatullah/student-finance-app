package com.financetracker.student_finance_app.service;

import com.financetracker.student_finance_app.model.*;
import com.financetracker.student_finance_app.repository.TransaksiRepository;
import com.financetracker.student_finance_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class LaporanService {

    @Autowired
    private TransaksiRepository transaksiRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Buat laporan untuk periode tertentu.
     * periode: "bulanan" (bulan ini) atau "mingguan" (7 hari terakhir)
     */
    public Laporan buatLaporan(Long mahasiswaId, String periode) {
        Mahasiswa mhs = (Mahasiswa) userRepository.findById(mahasiswaId)
                .orElseThrow(() -> new RuntimeException("Mahasiswa tidak ditemukan"));

        LocalDate selesai = LocalDate.now();
        LocalDate mulai = periode.equals("mingguan")
                ? selesai.minusDays(6)
                : selesai.withDayOfMonth(1);

        List<Transaksi> list = transaksiRepository
                .findByMahasiswaIdAndTanggalBetweenOrderByTanggalDesc(mahasiswaId, mulai, selesai);

        Double rawMasuk = transaksiRepository.sumByMahasiswaIdAndJenisAndTanggalBetween(
                mahasiswaId, JenisTransaksi.PEMASUKAN, mulai, selesai);
        Double rawKeluar = transaksiRepository.sumByMahasiswaIdAndJenisAndTanggalBetween(
                mahasiswaId, JenisTransaksi.PENGELUARAN, mulai, selesai);

        double totalMasuk  = rawMasuk  != null ? rawMasuk  : 0;
        double totalKeluar = rawKeluar != null ? rawKeluar : 0;

        String periodeLabel = mulai.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                + " s/d " + selesai.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

        return new Laporan(periodeLabel, mhs, list, totalMasuk, totalKeluar);
    }

    /**
     * Kirim laporan ke OrangTua lewat nomor HP.
     * Saat ini mencetak ke log; bisa dikembangkan ke WhatsApp API / email.
     */
    public String kirimKeOrangTua(Long mahasiswaId, String periode) {
        Laporan laporan = buatLaporan(mahasiswaId, periode);
        String pesan = String.format(
                "[LAPORAN KEUANGAN - %s]\n" +
                "Nama Mahasiswa : %s\n" +
                "Periode        : %s\n" +
                "Total Masuk    : Rp %,.0f\n" +
                "Total Keluar   : Rp %,.0f\n" +
                "Selisih        : Rp %,.0f\n" +
                "Saldo Saat Ini : Rp %,.0f",
                laporan.getTanggalGenerate(),
                laporan.getMahasiswa().getNama(),
                laporan.getPeriode(),
                laporan.getTotalPemasukan(),
                laporan.getTotalPengeluaran(),
                laporan.getSelisih(),
                laporan.getSaldo()
        );
        // TODO: integrasikan ke WhatsApp API / email
        System.out.println("=== KIRIM KE ORANG TUA ===");
        System.out.println(pesan);
        return pesan;
    }
}
