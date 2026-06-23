package com.financetracker.student_finance_app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "transaksi")
@Getter
@Setter
@NoArgsConstructor
public class Transaksi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JenisTransaksi jenis;

    @Column(nullable = false)
    private double jumlah;

    @Column(nullable = false)
    private LocalDate tanggal;

    @Column(length = 255)
    private String catatan;

    @Column(length = 100)
    private String kategori;

    @ManyToOne
    @JoinColumn(name = "mahasiswa_id", nullable = false)
    private Mahasiswa mahasiswa;

    public Transaksi(JenisTransaksi jenis, double jumlah, LocalDate tanggal,
                     String catatan, String kategori, Mahasiswa mahasiswa) {
        this.jenis = jenis;
        this.jumlah = jumlah;
        this.tanggal = tanggal;
        this.catatan = catatan;
        this.kategori = kategori;
        this.mahasiswa = mahasiswa;
    }

    public boolean validasi() {
        return jumlah > 0 && tanggal != null && mahasiswa != null;
    }

    public void tambah() { /* handled via repository */ }
    public void update() { /* handled via repository */ }
    public void hapus()  { /* handled via repository */ }
}
