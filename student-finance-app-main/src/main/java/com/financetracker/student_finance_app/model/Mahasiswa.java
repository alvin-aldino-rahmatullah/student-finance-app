package com.financetracker.student_finance_app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("MAHASISWA")
@Getter
@Setter
@NoArgsConstructor
public class Mahasiswa extends User {

    @Column(nullable = false)
    private double saldo;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    public Mahasiswa(String nama, String noHP, String password, String email) {
        super(nama, noHP, password);
        this.email = email;
        this.saldo = 0.0;
    }

    @Override
    public boolean login(String inputPassword) {
        return this.getPassword().equals(inputPassword);
    }

    public void tambahSaldo(double jumlah) {
        if (jumlah > 0) {
            this.saldo += jumlah;
        }
    }

    public double lihatSaldo() {
        return this.saldo;
    }

    public void updateSaldo(double jumlah, JenisTransaksi jenis) {
        if (jenis == JenisTransaksi.PEMASUKAN) {
            this.saldo += jumlah;
        } else if (jenis == JenisTransaksi.PENGELUARAN) {
            this.saldo -= jumlah;
        }
    }
}