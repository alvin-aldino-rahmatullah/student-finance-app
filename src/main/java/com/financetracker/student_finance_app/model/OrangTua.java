package com.financetracker.student_finance_app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("ORANG_TUA")
@Getter
@Setter
@NoArgsConstructor
public class OrangTua extends User {

    // No HP mahasiswa yang terdaftar (untuk link ke akun mahasiswa)
    @Column(name = "no_hp_mahasiswa", length = 20)
    private String noHPMahasiswa;

    public OrangTua(String nama, String noHP, String password) {
        super(nama, noHP, password);
    }

    public OrangTua(String nama, String noHP, String password, String noHPMahasiswa) {
        super(nama, noHP, password);
        this.noHPMahasiswa = noHPMahasiswa;
    }

    @Override
    public boolean login(String inputPassword) {
        return this.getPassword().equals(inputPassword);
    }

    public void terimaLaporan(Object laporan) {
        System.out.println(this.getNama() + " menerima laporan keuangan.");
    }
}
