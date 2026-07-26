package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.OtpToken;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.RegisterPatientRequest;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Role;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.entity.UserRole;
import vn.edu.fpt.SE2034_SWP391_G5.entity.UserRoleId;
import vn.edu.fpt.SE2034_SWP391_G5.enums.UserStatus;
import vn.edu.fpt.SE2034_SWP391_G5.repository.RoleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRoleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.AuthService;
import vn.edu.fpt.SE2034_SWP391_G5.service.EmailService;
import vn.edu.fpt.SE2034_SWP391_G5.service.SmsService;

import vn.edu.fpt.SE2034_SWP391_G5.util.CodeGenerator;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;

// Xử lý nghiệp vụ xác thực: đăng ký, OTP, quên/đổi mật khẩu
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final int OTP_EXPIRY_MINUTES = 5;

    private static final int MAX_OTP_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final PasswordEncoder passwordEncoder;

    // Kiểm tra dữ liệu đăng ký rồi gửi OTP
    @Override
    public OtpToken startRegistration(RegisterPatientRequest registerRequest, String otpChannel) {
        // Kiểm tra ràng buộc dữ liệu đăng ký
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu xác nhận không khớp.");
        }

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Tên đăng nhập này đã được sử dụng.");
        }

        if (userRepository.existsByPhone(registerRequest.getPhone())) {
            throw new BadRequestException("Số điện thoại này đã được sử dụng.");
        }

        if (registerRequest.getEmail() != null && !registerRequest.getEmail().trim().isEmpty()) {
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                throw new BadRequestException("Email này đã được sử dụng.");
            }
        }

        if ("email".equals(otpChannel) && (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty())) {
            throw new BadRequestException("Vui lòng nhập Email để nhận mã OTP.");
        }

        String otp = CodeGenerator.generateOtp();
        sendOtp(otpChannel, registerRequest.getPhone(), registerRequest.getEmail(), otp);

        return new OtpToken(otp, LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES), otpChannel, null);
    }

    // Kiểm tra mã OTP người dùng nhập, sai hoặc hết hạn thì báo lỗi
    @Override
    public void verifyOtp(OtpToken otpToken, String inputOtp) {
        if (LocalDateTime.now().isAfter(otpToken.getExpiryTime())) {
            throw new BadRequestException("Mã OTP đã hết hạn. Vui lòng thực hiện lại từ đầu.");
        }

        if (!otpToken.getOtp().equals(inputOtp)) {
            otpToken.setAttemptCount(otpToken.getAttemptCount() + 1);

            // Nhập sai đủ số lần cho phép thì mã bị hủy
            if (otpToken.getAttemptCount() >= MAX_OTP_ATTEMPTS) {
                throw new BadRequestException("Bạn đã nhập sai mã OTP " + MAX_OTP_ATTEMPTS + " lần. Vui lòng thực hiện lại từ đầu.");
            }

            int remaining = MAX_OTP_ATTEMPTS - otpToken.getAttemptCount();
            throw new BadRequestException("Mã OTP không chính xác. Bạn còn " + remaining + " lần thử.");
        }
    }

    // Mã không dùng được nữa: hết hạn hoặc nhập sai quá số lần cho phép
    @Override
    public boolean isOtpVoided(OtpToken otpToken) {
        if (otpToken.getAttemptCount() >= MAX_OTP_ATTEMPTS) {
            return true;
        }
        return LocalDateTime.now().isAfter(otpToken.getExpiryTime());
    }

    // Tạo tài khoản bệnh nhân sau khi OTP đã hợp lệ
    @Override
    @Transactional
    public void completeRegistration(RegisterPatientRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setMiddleName(registerRequest.getMiddleName());
        user.setLastName(registerRequest.getLastName());
        user.setPhone(registerRequest.getPhone());
        user.setStatus(UserStatus.ACTIVE.name());
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        // Tài khoản tự đăng ký luôn nhận vai trò PATIENT
        Role patientRole = roleRepository.findByName("PATIENT").orElse(null);
        if (patientRole == null) {
            throw new BadRequestException("Lỗi hệ thống: Vai trò PATIENT không tồn tại.");
        }

        UserRole userRole = new UserRole();
        UserRoleId userRoleId = new UserRoleId(user.getId(), patientRole.getId());
        userRole.setId(userRoleId);
        userRole.setUser(user);
        userRole.setRole(patientRole);
        userRole.setAssignedAt(LocalDateTime.now());

        userRoleRepository.save(userRole);
    }

    // Tìm tài khoản theo email hoặc số điện thoại rồi gửi OTP đặt lại mật khẩu
    @Override
    public OtpToken startPasswordReset(String email, String phone, String otpChannel) {
        String identifier;

        if ("sms".equals(otpChannel)) {
            User user = userRepository.findByPhone(phone).orElse(null);
            if (user == null) {
                throw new BadRequestException("Số điện thoại không tồn tại trong hệ thống.");
            }
            identifier = phone;
        } else {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                throw new BadRequestException("Email không tồn tại trong hệ thống.");
            }
            identifier = email;
        }

        String otp = CodeGenerator.generateOtp();
        sendOtp(otpChannel, phone, email, otp);

        return new OtpToken(otp, LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES), otpChannel, identifier);
    }

    // Đặt lại mật khẩu cho tài khoản đã xác minh OTP
    @Override
    public void resetPassword(String identifier, String otpChannel, String newPassword, String confirmNewPassword) {
        if (!newPassword.equals(confirmNewPassword)) {
            throw new BadRequestException("Mật khẩu xác nhận không khớp.");
        }

        User user;
        if ("sms".equals(otpChannel)) {
            user = userRepository.findByPhone(identifier).orElse(null);
        } else {
            user = userRepository.findByEmail(identifier).orElse(null);
        }

        if (user == null) {
            throw new BadRequestException("Lỗi hệ thống: Không tìm thấy người dùng.");
        }

        // Mật khẩu mới phải khác mật khẩu cũ
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new BadRequestException("Mật khẩu mới không được trùng với mật khẩu cũ.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // Đổi mật khẩu cho user đang đăng nhập
    @Override
    @Transactional
    public void changePassword(User currentUser, String oldPassword, String newPassword, String confirmNewPassword) {
        if (currentUser == null) {
            throw new BadRequestException("Bạn cần đăng nhập để đổi mật khẩu.");
        }

        User user = userRepository.findById(currentUser.getId()).orElse(null);
        if (user == null) {
            throw new BadRequestException("Lỗi hệ thống: Không tìm thấy người dùng.");
        }

        if (oldPassword == null || !passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BadRequestException("Mật khẩu hiện tại không chính xác.");
        }

        if (newPassword == null || !newPassword.equals(confirmNewPassword)) {
            throw new BadRequestException("Mật khẩu xác nhận không khớp.");
        }

        if (newPassword.length() < 6) {
            throw new BadRequestException("Mật khẩu mới phải có ít nhất 6 ký tự.");
        }

        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new BadRequestException("Mật khẩu mới không được trùng với mật khẩu cũ.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // Gửi mã OTP qua SMS hoặc email tùy kênh người dùng chọn
    private void sendOtp(String otpChannel, String phone, String email, String otp) {
        if ("sms".equals(otpChannel)) {
            try {
                smsService.sendOtpSms(phone, otp);
            } catch (Exception e) {
                throw new BadRequestException("Lỗi gửi SMS OTP: " + e.getMessage() + ". Vui lòng thử lại.");
            }
        } else {
            try {
                emailService.sendOtpEmail(email, otp);
            } catch (Exception e) {
                throw new BadRequestException("Lỗi gửi email OTP: " + e.getMessage() + ". Vui lòng thử lại.");
            }
        }
    }
}
