package com.financetracker.student_finance_app.service;

import org.springframework.stereotype.Service;
import com.financetracker.student_finance_app.model.Mahasiswa;

@Service
public class LaporanService {

    public String generateLaporan(Mahasiswa mahasiswa) {

        return """
        <html>
        <head>
            <title>Laporan Bulanan</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    margin: 40px;
                }

                .laporan {
                    border: 1px solid #ccc;
                    padding: 20px;
                    width: 500px;
                    border-radius: 10px;
                }

                h2 {
                    color: #2c3e50;
                }
            </style>
        </head>
        <body>

            <div class="laporan">
                <h2>LAPORAN BULANAN MAHASISWA</h2>

                <p><b>Nama Mahasiswa:</b> %s</p>
                <p><b>Email:</b> %s</p>
                <p><b>Saldo Saat Ini:</b> Rp %.2f</p>

                <hr>

                <p><b>Status:</b></p>
                <p>Laporan berhasil dibuat dan siap dikirim ke orang tua.</p>
            </div>

        </body>
        </html>
        """
                .formatted(
                        mahasiswa.getNama(),
                        mahasiswa.getEmail(),
                        mahasiswa.getSaldo()
                );
    }
}