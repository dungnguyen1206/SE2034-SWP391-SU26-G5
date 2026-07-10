package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vn.edu.fpt.SE2034_SWP391_G5.service.SmsService;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Value("${smsgateway.api-url}")
    private String apiUrl;

    @Value("${smsgateway.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public SmsServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void sendOtpSms(String phoneNumber, String otp) {
        // sms-gateway.app expects standard 0352365217 or E.164, we will pass it directly
        // because their app uses the Android dialer, local formats are fine.
        
        String message = "Ma OTP cua ban tai HAMS la: " + otp + ". Ma co hieu luc trong 5 phut.";

        org.springframework.web.util.UriComponentsBuilder builder = org.springframework.web.util.UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("key", apiKey)
                .queryParam("number", phoneNumber)
                .queryParam("message", message)
                .queryParam("devices", "1")
                .queryParam("type", "sms");

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
            log.debug("SMS Gateway response: {}", response.getBody());
            
            // Basic error checking based on their API response
            if (response.getBody() != null && response.getBody().contains("\"success\":false")) {
                 log.error("SMS Gateway error: {}", response.getBody());
                 throw new RuntimeException("SMS Gateway từ chối tin nhắn, có thể điện thoại chưa kết nối: " + response.getBody());
            }
        } catch (Exception e) {
            log.error("Failed to send SMS via Gateway", e);
            throw new RuntimeException("Lỗi gửi SMS qua Android Gateway: " + e.getMessage());
        }
    }
}
