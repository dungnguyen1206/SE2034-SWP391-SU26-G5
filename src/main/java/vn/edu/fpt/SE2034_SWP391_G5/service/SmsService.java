package vn.edu.fpt.SE2034_SWP391_G5.service;

public interface SmsService {
    void sendOtpSms(String phoneNumber, String otp);
    void sendWalkInAccountSms(String phoneNumber, String password);
}
