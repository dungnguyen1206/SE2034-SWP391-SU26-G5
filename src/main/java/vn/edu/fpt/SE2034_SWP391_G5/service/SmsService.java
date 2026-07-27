package vn.edu.fpt.SE2034_SWP391_G5.service;

public interface SmsService {
    // Gửi mã OTP qua tin nhắn
    void sendOtpSms(String phoneNumber, String otp);

    // Gửi mật khẩu tài khoản cho bệnh nhân đăng ký trực tiếp tại quầy
    void sendWalkInAccountSms(String phoneNumber, String password);
}
