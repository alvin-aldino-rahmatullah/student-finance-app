package com.financetracker.student_finance_app.repository;

import com.financetracker.student_finance_app.model.JenisTransaksi;
import com.financetracker.student_finance_app.model.Transaksi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransaksiRepository extends JpaRepository<Transaksi, Long> {

    List<Transaksi> findByMahasiswaIdOrderByTanggalDesc(Long mahasiswaId);

    List<Transaksi> findByMahasiswaIdAndTanggalBetweenOrderByTanggalDesc(
            Long mahasiswaId, LocalDate mulai, LocalDate selesai);

    List<Transaksi> findByMahasiswaIdAndJenisOrderByTanggalDesc(
            Long mahasiswaId, JenisTransaksi jenis);

    @Query("SELECT SUM(t.jumlah) FROM Transaksi t WHERE t.mahasiswa.id = :id AND t.jenis = :jenis AND t.tanggal BETWEEN :mulai AND :selesai")
    Double sumByMahasiswaIdAndJenisAndTanggalBetween(
            @Param("id") Long mahasiswaId,
            @Param("jenis") JenisTransaksi jenis,
            @Param("mulai") LocalDate mulai,
            @Param("selesai") LocalDate selesai);

    @Query("SELECT t.kategori, SUM(t.jumlah) FROM Transaksi t WHERE t.mahasiswa.id = :id AND t.jenis = :jenis AND t.tanggal BETWEEN :mulai AND :selesai GROUP BY t.kategori")
    List<Object[]> sumByKategoriAndJenis(
            @Param("id") Long mahasiswaId,
            @Param("jenis") JenisTransaksi jenis,
            @Param("mulai") LocalDate mulai,
            @Param("selesai") LocalDate selesai);

    @Query("SELECT FUNCTION('DATE_FORMAT', t.tanggal, '%Y-%m'), SUM(CASE WHEN t.jenis = 'PEMASUKAN' THEN t.jumlah ELSE 0 END), SUM(CASE WHEN t.jenis = 'PENGELUARAN' THEN t.jumlah ELSE 0 END) FROM Transaksi t WHERE t.mahasiswa.id = :id GROUP BY FUNCTION('DATE_FORMAT', t.tanggal, '%Y-%m') ORDER BY FUNCTION('DATE_FORMAT', t.tanggal, '%Y-%m')")
    List<Object[]> monthlyTrendByMahasiswaId(@Param("id") Long mahasiswaId);
}
