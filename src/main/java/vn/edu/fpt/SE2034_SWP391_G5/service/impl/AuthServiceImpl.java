package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.RegisterPatientRequest;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Role;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.entity.UserRole;
import vn.edu.fpt.SE2034_SWP391_G5.entity.UserRoleId;
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
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;

/**
 * Implementation of AuthService handling authentication logic.
 * Manages user registration, OTP verification, and password resets.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Processes user registration request.
     * Validates input data, creates pending user session, and sends OTP for verification.
     */
    @Override
    public void processRegistration(RegisterPatientRequest registerRequest, HttpSession session, String otpChannel) {
        // Validate registration constraints
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
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        session.setAttribute("pendingRegister", registerRequest);
        session.setAttribute("registerOtp", otp);
        session.setAttribute("otpExpiry", expiryTime);
        session.setAttribute("otpChannel", otpChannel);

        if ("sms".equals(otpChannel)) {
            try {
                smsService.sendOtpSms(registerRequest.getPhone(), otp);
            } catch (Exception e) {
                throw new BadRequestException("Lỗi gửi SMS OTP: " + e.getMessage() + ". Vui lòng thử lại.");
            }
        } else {
            try {
                emailService.sendOtpEmail(registerRequest.getEmail(), otp);
            } catch (Exception e) {
                throw new BadRequestException("Lỗi gửi email OTP: " + e.getMessage() + ". Vui lòng thử lại.");
            }
        }
    }

    /**
     * Verifies OTP for user registration and saves the new user to the database.
     */
    @Override
    @Transactional
    public void verifyOtp(String otp, HttpSession session) {
        RegisterPatientRequest pendingRegister = (RegisterPatientRequest) session.getAttribute("pendingRegister");
        String registerOtp = (String) session.getAttribute("registerOtp");
        LocalDateTime expiryTime = (LocalDateTime) session.getAttribute("otpExpiry");

        // Validate OTP session data
        if (pendingRegister == null || registerOtp == null || expiryTime == null) {
            throw new BadRequestException("Dữ liệu đăng ký không tồn tại hoặc đã bị xóa. Vui lòng đăng ký lại.");
        }

        // Check if OTP is expired
        if (LocalDateTime.now().isAfter(expiryTime)) {
            session.removeAttribute("pendingRegister");
            session.removeAttribute("registerOtp");
            session.removeAttribute("otpExpiry");
            throw new BadRequestException("Mã OTP đã hết hạn. Vui lòng đăng ký lại.");
        }

        // Check if OTP matches
        if (!registerOtp.equals(otp)) {
            throw new BadRequestException("Mã OTP không chính xác. Vui lòng kiểm tra lại.");
        }

        User user = new User();
        user.setUsername(pendingRegister.getUsername());
        user.setEmail(pendingRegister.getEmail());
        user.setPasswordHash(passwordEncoder.encode(pendingRegister.getPassword()));
        user.setFirstName(pendingRegister.getFirstName());
        user.setMiddleName(pendingRegister.getMiddleName());
        user.setLastName(pendingRegister.getLastName());
        user.setPhone(pendingRegister.getPhone());
        user.setLicenseIssueDate(null);
        user.setStatus("ACTIVE");
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

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

        session.removeAttribute("pendingRegister");
        session.removeAttribute("registerOtp");
        session.removeAttribute("otpExpiry");
    }

    /**
     * Processes forgot password request.
     * Verifies user existence by email or phone and sends an OTP for reset.
     */
    @Override
    public void processForgotPassword(String email, String phone, HttpSession session, String otpChannel) {
        User user = null;
        if ("sms".equals(otpChannel)) {
            user = userRepository.findByPhone(phone).orElse(null);
            if (user == null) {
                throw new BadRequestException("Số điện thoại không tồn tại trong hệ thống.");
            }
            session.setAttribute("resetIdentifier", phone);
        } else {
            user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                throw new BadRequestException("Email không tồn tại trong hệ thống.");
            }
            session.setAttribute("resetIdentifier", email);
        }

        String otp = CodeGenerator.generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        session.setAttribute("resetOtp", otp);
        session.setAttribute("resetOtpExpiry", expiryTime);
        session.setAttribute("otpChannel", otpChannel);

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

    /**
     * Processes password reset request.
     * Verifies the OTP and updates the user's password if valid.
     */
    @Override
    public void processResetPassword(String otp, String newPassword, String confirmNewPassword, HttpSession session) {
        String resetIdentifier = (String) session.getAttribute("resetIdentifier");
        String resetOtp = (String) session.getAttribute("resetOtp");
        LocalDateTime expiryTime = (LocalDateTime) session.getAttribute("resetOtpExpiry");
        String otpChannel = (String) session.getAttribute("otpChannel");

        // Validate reset session and OTP
        if (resetIdentifier == null || resetOtp == null || expiryTime == null) {
            throw new BadRequestException("Dữ liệu quên mật khẩu không tồn tại. Vui lòng thử lại.");
        }

        if (LocalDateTime.now().isAfter(expiryTime)) {
            session.removeAttribute("resetIdentifier");
            session.removeAttribute("resetOtp");
            session.removeAttribute("resetOtpExpiry");
            session.removeAttribute("otpChannel");
            throw new BadRequestException("Mã OTP đã hết hạn. Vui lòng thử lại.");
        }

        if (!resetOtp.equals(otp)) {
            throw new BadRequestException("Mã OTP không chính xác.");
        }

        if (!newPassword.equals(confirmNewPassword)) {
            throw new BadRequestException("Mật khẩu xác nhận không khớp.");
        }

        User user = null;
        if ("sms".equals(otpChannel)) {
            user = userRepository.findByPhone(resetIdentifier).orElse(null);
        } else {
            user = userRepository.findByEmail(resetIdentifier).orElse(null);
        }

        // Validate new password and update
        if (user != null && passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new BadRequestException("Mật khẩu mới không được trùng với mật khẩu cũ.");
        }
        
        if (user != null) {
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        } else {
            throw new BadRequestException("Lỗi hệ thống: Không tìm thấy người dùng.");
        }

        session.removeAttribute("resetIdentifier");
        session.removeAttribute("resetOtp");
        session.removeAttribute("resetOtpExpiry");
        session.removeAttribute("otpChannel");
    }
}

