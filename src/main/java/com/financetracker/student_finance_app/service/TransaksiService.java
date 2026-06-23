package com.financetracker.student_finance_app.service;

import com.financetracker.student_finance_app.model.JenisTransaksi;
import com.financetracker.student_finance_app.model.Mahasiswa;
import com.financetracker.student_finance_app.model.Transaksi;
import com.financetracker.student_finance_app.repository.TransaksiRepository;
import com.financetracker.student_finance_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class TransaksiService {

    @Autowired
    private TransaksiRepository transaksiRepository;

    @Autowired
    private UserRepository userRepository;

    // ========== CRUD ==========

    @Transactional
    public Transaksi tambah(Long mahasiswaId, JenisTransaksi jenis, double jumlah,
                             LocalDate tanggal, String catatan, String kategori) {
        Mahasiswa mhs = (Mahasiswa) userRepository.findById(mahasiswaId)
                .orElseThrow(() -> new RuntimeException("Mahasiswa tidak ditemukan"));
        Transaksi t = new Transaksi(jenis, jumlah, tanggal, catatan, kategori, mhs);
        if (!t.validasi()) throw new IllegalArgumentException("Data transaksi tidak valid");
        mhs.updateSaldo(jumlah, jenis);
        userRepository.save(mhs);
        return transaksiRepository.save(t);
    }

    @Transactional
    public Transaksi update(Long transaksiId, JenisTransaksi jenis, double jumlah,
                             LocalDate tanggal, String catatan, String kategori) {
        Transaksi t = transaksiRepository.findById(transaksiId)
                .orElseThrow(() -> new RuntimeException("Transaksi tidak ditemukan"));
        Mahasiswa mhs = t.getMahasiswa();
        // Rollback saldo lama
        if (t.getJenis() == JenisTransaksi.PEMASUKAN) mhs.updateSaldo(t.getJumlah(), JenisTransaksi.PENGELUARAN);
        else mhs.updateSaldo(t.getJumlah(), JenisTransaksi.PEMASUKAN);
        // Update
        t.setJenis(jenis); t.setJumlah(jumlah); t.setTanggal(tanggal);
        t.setCatatan(catatan); t.setKategori(kategori);
        mhs.updateSaldo(jumlah, jenis);
        userRepository.save(mhs);
        return transaksiRepository.save(t);
    }

    @Transactional
    public void hapus(Long transaksiId) {
        Transaksi t = transaksiRepository.findById(transaksiId)
                .orElseThrow(() -> new RuntimeException("Transaksi tidak ditemukan"));
        Mahasiswa mhs = t.getMahasiswa();
        if (t.getJenis() == JenisTransaksi.PEMASUKAN) mhs.updateSaldo(t.getJumlah(), JenisTransaksi.PENGELUARAN);
        else mhs.updateSaldo(t.getJumlah(), JenisTransaksi.PEMASUKAN);
        userRepository.save(mhs);
        transaksiRepository.delete(t);
    }

    // ========== RIWAYAT ==========

    public List<Transaksi> getRiwayat(Long mahasiswaId) {
        return transaksiRepository.findByMahasiswaIdOrderByTanggalDesc(mahasiswaId);
    }

    public List<Transaksi> getRiwayatMingguan(Long mahasiswaId) {
        LocalDate selesai = LocalDate.now();
        LocalDate mulai = selesai.minusDays(6);
        return transaksiRepository.findByMahasiswaIdAndTanggalBetweenOrderByTanggalDesc(mahasiswaId, mulai, selesai);
    }

    public List<Transaksi> getRiwayatBulanan(Long mahasiswaId) {
        LocalDate selesai = LocalDate.now();
        LocalDate mulai = selesai.withDayOfMonth(1);
        return transaksiRepository.findByMahasiswaIdAndTanggalBetweenOrderByTanggalDesc(mahasiswaId, mulai, selesai);
    }

    // ========== STATISTIK & GRAFIK ==========

    public Map<String, Object> getStatistikBulanan(Long mahasiswaId) {
        LocalDate selesai = LocalDate.now();
        LocalDate mulai = selesai.withDayOfMonth(1);

        Double pemasukan = transaksiRepository.sumByMahasiswaIdAndJenisAndTanggalBetween(
                mahasiswaId, JenisTransaksi.PEMASUKAN, mulai, selesai);
        Double pengeluaran = transaksiRepository.sumByMahasiswaIdAndJenisAndTanggalBetween(
                mahasiswaId, JenisTransaksi.PENGELUARAN, mulai, selesai);

        double totalPemasukan = pemasukan != null ? pemasukan : 0;
        double totalPengeluaran = pengeluaran != null ? pengeluaran : 0;

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalPemasukan", totalPemasukan);
        stats.put("totalPengeluaran", totalPengeluaran);
        stats.put("selisih", totalPemasukan - totalPengeluaran);
        stats.put("periodeLabel", mulai + " s/d " + selesai);
        return stats;
    }

    public Map<String, Double> getKategoriPengeluaran(Long mahasiswaId) {
        LocalDate selesai = LocalDate.now();
        LocalDate mulai = selesai.withDayOfMonth(1);
        List<Object[]> rows = transaksiRepository.sumByKategoriAndJenis(
                mahasiswaId, JenisTransaksi.PENGELUARAN, mulai, selesai);
        Map<String, Double> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String kategori = row[0] != null ? row[0].toString() : "Lainnya";
            double total = row[1] != null ? ((Number) row[1]).doubleValue() : 0;
            result.put(kategori, total);
        }
        return result;
    }

    public Map<String, Object> getTrendBulanan(Long mahasiswaId) {
        List<Object[]> rows = transaksiRepository.monthlyTrendByMahasiswaId(mahasiswaId);
        List<String> labels = new ArrayList<>();
        List<Double> pemasukan = new ArrayList<>();
        List<Double> pengeluaran = new ArrayList<>();
        for (Object[] row : rows) {
            labels.add(row[0].toString());
            pemasukan.add(row[1] != null ? ((Number) row[1]).doubleValue() : 0);
            pengeluaran.add(row[2] != null ? ((Number) row[2]).doubleValue() : 0);
        }
        Map<String, Object> trend = new LinkedHashMap<>();
        trend.put("labels", labels);
        trend.put("pemasukan", pemasukan);
        trend.put("pengeluaran", pengeluaran);
        return trend;
    }
}
