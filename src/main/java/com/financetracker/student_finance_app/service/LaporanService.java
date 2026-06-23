package com.financetracker.student_finance_app.service;

import com.financetracker.student_finance_app.model.*;
import com.financetracker.student_finance_app.repository.TransaksiRepository;
import com.financetracker.student_finance_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class LaporanService {

    @Autowired
    private TransaksiRepository transaksiRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WhatsAppService whatsAppService;

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
     * Kirim laporan ke WA orang tua
     * noHPOrangTua diambil dari data OrangTua yang terdaftar
     */
    public boolean kirimKeOrangTuaWA(Long mahasiswaId, String periode) {
        Laporan laporan = buatLaporan(mahasiswaId, periode);
        Mahasiswa mhs = laporan.getMahasiswa();

        // Cari OrangTua yang terhubung dengan noHP mahasiswa ini
        var orangTuaOpt = userRepository.findOrangTuaByNoHP(mhs.getNoHP());

        // Ambil noHP orang tua - dari relasi atau dari noHPMahasiswa
        String noHPTarget = null;

        // Cari semua OrangTua yang punya noHPMahasiswa = noHP mahasiswa ini
        // Gunakan noHP mahasiswa untuk dicari di field noHPMahasiswa orang tua
        var semuaUser = userRepository.findAll();
        for (var user : semuaUser) {
            if (user instanceof OrangTua ot && mhs.getNoHP().equals(ot.getNoHPMahasiswa())) {
                noHPTarget = ot.getNoHP();
                break;
            }
        }

        if (noHPTarget == null) {
            System.out.println("[Laporan] Tidak ada orang tua terdaftar untuk mahasiswa: " + mhs.getNama());
            return false;
        }

        String pesan = formatPesanWA(laporan);
        return whatsAppService.kirimPesan(noHPTarget, pesan);
    }

    /**
     * Kirim laporan ke nomor WA tertentu (dipilih manual)
     */
    public boolean kirimKeNomorWA(Long mahasiswaId, String periode, String noHP) {
        Laporan laporan = buatLaporan(mahasiswaId, periode);
        String pesan = formatPesanWA(laporan);
        return whatsAppService.kirimPesan(noHP, pesan);
    }

    private String formatPesanWA(Laporan laporan) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));
        return String.format(
            "📊 *LAPORAN KEUANGAN MAHASISWA*\n" +
            "━━━━━━━━━━━━━━━━━━━\n" +
            "👤 Nama     : %s\n" +
            "📅 Periode  : %s\n" +
            "🗓️ Tanggal  : %s\n" +
            "━━━━━━━━━━━━━━━━━━━\n" +
            "💰 Total Pemasukan  : Rp %,.0f\n" +
            "💸 Total Pengeluaran: Rp %,.0f\n" +
            "📈 Selisih          : Rp %,.0f\n" +
            "🏦 Saldo Saat Ini   : Rp %,.0f\n" +
            "━━━━━━━━━━━━━━━━━━━\n" +
            "Dikirim otomatis via Aplikasi Keuangan Mahasiswa 🎓",
            laporan.getMahasiswa().getNama(),
            laporan.getPeriode(),
            LocalDate.now().format(fmt),
            laporan.getTotalPemasukan(),
            laporan.getTotalPengeluaran(),
            laporan.getSelisih(),
            laporan.getSaldo()
        );
    }
}
