package vn.edu.fpt.SE2034_SWP391_G5.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
    void sendSimpleEmail(String toEmail, String subject, String content);
}
