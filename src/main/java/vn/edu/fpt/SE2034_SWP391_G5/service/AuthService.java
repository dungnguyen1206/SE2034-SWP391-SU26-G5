package vn.edu.fpt.SE2034_SWP391_G5.service;

import vn.edu.fpt.SE2034_SWP391_G5.dto.response.OtpToken;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.RegisterPatientRequest;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

public interface AuthService {
    // Kiểm tra dữ liệu đăng ký rồi gửi OTP, trả về mã để controller giữ trong session
    OtpToken startRegistration(RegisterPatientRequest registerRequest, String otpChannel);

    // Kiểm tra mã OTP người dùng nhập, sai hoặc hết hạn thì báo lỗi
    void verifyOtp(OtpToken otpToken, String inputOtp);

    // Mã không dùng được nữa: hết hạn hoặc nhập sai quá số lần cho phép
    boolean isOtpVoided(OtpToken otpToken);

    // Tạo tài khoản bệnh nhân sau khi OTP đã hợp lệ
    void completeRegistration(RegisterPatientRequest registerRequest);

    // Tìm tài khoản theo email hoặc số điện thoại rồi gửi OTP đặt lại mật khẩu
    OtpToken startPasswordReset(String email, String phone, String otpChannel);

    // Đặt lại mật khẩu cho tài khoản đã xác minh OTP
    void resetPassword(String identifier, String otpChannel, String newPassword, String confirmNewPassword);

    // Đổi mật khẩu cho user đang đăng nhập
    void changePassword(User currentUser, String oldPassword, String newPassword, String confirmNewPassword);
}
