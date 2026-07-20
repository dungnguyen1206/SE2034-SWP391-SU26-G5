package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vn.edu.fpt.SE2034_SWP391_G5.service.SmsService;



import java.util.HashMap;
import java.util.Map;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Value("${traccar.api-url}")
    private String apiUrl;

    @Value("${traccar.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public SmsServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendOtpSms(String phoneNumber, String otp) {
        // Format phone number to standard E.164 if it starts with 0
        String formattedPhone = phoneNumber;
        if (phoneNumber.startsWith("0")) {
            formattedPhone = "+84" + phoneNumber.substring(1);
        } else if (!phoneNumber.startsWith("+")) {
            formattedPhone = "+" + phoneNumber;
        }

        String message = "Ma xac thuc OTP cua ban tai HAMS la: " + otp + ". Ma co hieu luc trong 5 phut.";

        // Traccar SMS Gateway requires a JSON body: {"to": "+84...", "message": "..."}
        Map<String, String> body = new HashMap<>();
        body.put("to", formattedPhone);
        body.put("message", message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Traccar uses the raw token in the Authorization header
        headers.set("Authorization", apiKey);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            // POST request to Traccar SMS Gateway URL (e.g. http://192.168.x.x:8082)
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
            log.debug("Traccar SMS Gateway response: {}", response.getBody());
        } catch (Exception e) {
            log.error("Failed to send SMS via Traccar Gateway", e);
            throw new RuntimeException("Lỗi gửi SMS qua Traccar Gateway nội bộ: " + e.getMessage());
        }
    }

    @Override
    public void sendWalkInAccountSms(String phoneNumber, String password) {
        String formattedPhone = phoneNumber;
        if (phoneNumber.startsWith("0")) {
            formattedPhone = "+84" + phoneNumber.substring(1);
        } else if (!phoneNumber.startsWith("+")) {
            formattedPhone = "+" + phoneNumber;
        }

        String message = "HAMS: Ma truy cap cua ban la " + password + ". Vui long vao he thong de doi ma.";

        Map<String, String> body = new HashMap<>();
        body.put("to", formattedPhone);
        body.put("message", message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
            log.debug("Traccar SMS Gateway response: {}", response.getBody());
        } catch (Exception e) {
            log.error("Failed to send SMS via Traccar Gateway", e);
            // Optionally throw or just log since this is just an info SMS and shouldn't block booking
            log.error("Lỗi gửi SMS mật khẩu qua Traccar Gateway nội bộ: " + e.getMessage());
        }
    }
}
