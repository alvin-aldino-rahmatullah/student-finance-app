package com.financetracker.student_finance_app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nama;

    @Column(name = "no_hp", nullable = false, length = 20)
    private String noHP;

    @Column(nullable = false)
    private String password;

    public User(String nama, String noHP, String password) {
        this.nama = nama;
        this.noHP = noHP;
        this.password = password;
    }

    public abstract boolean login(String inputPassword);
}