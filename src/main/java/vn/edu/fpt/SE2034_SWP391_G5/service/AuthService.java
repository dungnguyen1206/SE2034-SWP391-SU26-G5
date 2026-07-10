package vn.edu.fpt.SE2034_SWP391_G5.service;

import jakarta.servlet.http.HttpSession;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.RegisterPatientRequest;

public interface AuthService {
    // Process new user registration and trigger OTP
    void processRegistration(RegisterPatientRequest registerRequest, HttpSession session, String otpChannel);
    
    // Verify user's OTP to activate account
    void verifyOtp(String otp, HttpSession session);
    
    // Process forgot password request and send reset OTP
    void processForgotPassword(String email, String phone, HttpSession session, String otpChannel);
    
    // Verify OTP and update user's password
    void processResetPassword(String otp, String newPassword, String confirmNewPassword, HttpSession session);
}
