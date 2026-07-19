package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        if (toEmail == null || toEmail.endsWith("@walkin.local")) return;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Mã xác thực OTP đăng ký tài khoản HAMS");
        message.setText("Chào bạn,\n\n"
                + "Cảm ơn bạn đã đăng ký tài khoản tại hệ thống HAMS.\n"
                + "Mã xác thực OTP của bạn là: " + otp + "\n"
                + "Mã này có hiệu lực trong vòng 5 phút. Vui lòng không chia sẻ mã này cho bất kỳ ai.\n\n"
                + "Trân trọng,\n"
                + "Hệ thống quản lý HAMS");
        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Lỗi gửi email: " + e.getMessage());
        }
    }

    @Override
    public void sendSimpleEmail(String toEmail, String subject, String content) {
        if (toEmail == null || toEmail.endsWith("@walkin.local")) return;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(content);
        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Lỗi gửi email: " + e.getMessage());
        }
    }
}
