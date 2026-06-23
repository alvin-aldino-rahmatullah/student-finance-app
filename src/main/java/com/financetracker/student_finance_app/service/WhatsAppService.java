package com.financetracker.student_finance_app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class WhatsAppService {

    @Value("${fonnte.token}")
    private String fonnteToken;

    @Value("${fonnte.api.url}")
    private String fonnteUrl;

    /**
     * Kirim pesan WhatsApp via Fonnte API
     * @param noHP    nomor tujuan (format: 628xxxx atau 08xxxx)
     * @param pesan   isi pesan yang akan dikirim
     * @return true kalau berhasil, false kalau gagal
     */
    public boolean kirimPesan(String noHP, String pesan) {
        try {
            // Normalisasi nomor HP (0xxx -> 62xxx)
            String nomorTujuan = noHP.startsWith("0")
                    ? "62" + noHP.substring(1)
                    : noHP;

            // Build form data
            String formData = "target=" + URLEncoder.encode(nomorTujuan, StandardCharsets.UTF_8)
                    + "&message=" + URLEncoder.encode(pesan, StandardCharsets.UTF_8);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fonnteUrl))
                    .header("Authorization", fonnteToken)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            System.out.println("[WhatsApp] Response: " + response.body());
            return response.statusCode() == 200;

        } catch (Exception e) {
            System.err.println("[WhatsApp] Gagal kirim: " + e.getMessage());
            return false;
        }
    }
}
