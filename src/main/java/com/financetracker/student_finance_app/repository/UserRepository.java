package com.financetracker.student_finance_app.repository;

import com.financetracker.student_finance_app.model.Mahasiswa;
import com.financetracker.student_finance_app.model.OrangTua;
import com.financetracker.student_finance_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT m FROM Mahasiswa m WHERE m.email = :email")
    Optional<Mahasiswa> findByEmail(String email);

    @Query("SELECT o FROM OrangTua o WHERE o.noHP = :noHP")
    Optional<OrangTua> findOrangTuaByNoHP(String noHP);

    @Query("SELECT m FROM Mahasiswa m WHERE m.noHP = :noHP")
    Optional<Mahasiswa> findMahasiswaByNoHP(String noHP);

    @Query("SELECT COUNT(m) > 0 FROM Mahasiswa m WHERE m.email = :email")
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(o) > 0 FROM OrangTua o WHERE o.noHP = :noHP")
    boolean existsByNoHP(String noHP);
}
