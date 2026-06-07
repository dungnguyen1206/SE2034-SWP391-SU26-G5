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

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Process user registration request, validate data and send OTP email
    @Override
    public void processRegistration(RegisterPatientRequest registerRequest, HttpSession session) {
        // Check if password and confirm password match
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp.");
        }

        // Check if username is already taken
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Tên đăng nhập này đã được sử dụng.");
        }

        // Check if email is already registered
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email này đã được sử dụng.");
        }

        String otp = String.format("%06d", (int) (Math.random() * 1000000));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        session.setAttribute("pendingRegister", registerRequest);
        session.setAttribute("registerOtp", otp);
        session.setAttribute("otpExpiry", expiryTime);

        try {
            emailService.sendOtpEmail(registerRequest.getEmail(), otp);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi gửi email OTP: " + e.getMessage() + ". Vui lòng thử lại.");
        }
    }

    // Verify OTP for user registration and save new user to database
    @Override
    public void verifyOtp(String otp, HttpSession session) {
        RegisterPatientRequest pendingRegister = (RegisterPatientRequest) session.getAttribute("pendingRegister");
        String registerOtp = (String) session.getAttribute("registerOtp");
        LocalDateTime expiryTime = (LocalDateTime) session.getAttribute("otpExpiry");

        // Check if registration session data exists
        if (pendingRegister == null || registerOtp == null || expiryTime == null) {
            throw new RuntimeException("Dữ liệu đăng ký không tồn tại hoặc đã bị xóa. Vui lòng đăng ký lại.");
        }

        // Check if OTP is expired
        if (LocalDateTime.now().isAfter(expiryTime)) {
            session.removeAttribute("pendingRegister");
            session.removeAttribute("registerOtp");
            session.removeAttribute("otpExpiry");
            throw new RuntimeException("Mã OTP đã hết hạn. Vui lòng đăng ký lại.");
        }

        // Check if OTP matches
        if (!registerOtp.equals(otp)) {
            throw new RuntimeException("Mã OTP không chính xác. Vui lòng kiểm tra lại.");
        }

        User user = new User();
        user.setUsername(pendingRegister.getUsername());
        user.setEmail(pendingRegister.getEmail());
        user.setPasswordHash(passwordEncoder.encode(pendingRegister.getPassword()));
        user.setFirstName(pendingRegister.getFirstName());
        user.setMiddleName(pendingRegister.getMiddleName());
        user.setLastName(pendingRegister.getLastName());
        user.setPhone(pendingRegister.getPhone());
        user.setExperienceYears(0);
        user.setStatus("ACTIVE");
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        Role patientRole = roleRepository.findByName("PATIENT")
                .orElseThrow(() -> new RuntimeException("Lỗi hệ thống: Vai trò PATIENT không tồn tại."));

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

    // Process forgot password request, verify email and send OTP
    @Override
    public void processForgotPassword(String email, HttpSession session) {
        User user = userRepository.findByEmail(email).orElse(null);
        // Check if user exists with the provided email
        if (user == null) {
            throw new RuntimeException("Email không tồn tại trong hệ thống.");
        }

        String otp = String.format("%06d", (int) (Math.random() * 1000000));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        session.setAttribute("resetEmail", email);
        session.setAttribute("resetOtp", otp);
        session.setAttribute("resetOtpExpiry", expiryTime);

        try {
            emailService.sendOtpEmail(user.getEmail(), otp);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi gửi email OTP: " + e.getMessage() + ". Vui lòng thử lại.");
        }
    }

    // Process reset password request, verify OTP and update new password
    @Override
    public void processResetPassword(String otp, String newPassword, String confirmNewPassword, HttpSession session) {
        String resetEmail = (String) session.getAttribute("resetEmail");
        String resetOtp = (String) session.getAttribute("resetOtp");
        LocalDateTime expiryTime = (LocalDateTime) session.getAttribute("resetOtpExpiry");

        // Check if password reset session data exists
        if (resetEmail == null || resetOtp == null || expiryTime == null) {
            throw new RuntimeException("Dữ liệu quên mật khẩu không tồn tại. Vui lòng thử lại.");
        }

        // Check if OTP is expired
        if (LocalDateTime.now().isAfter(expiryTime)) {
            session.removeAttribute("resetEmail");
            session.removeAttribute("resetOtp");
            session.removeAttribute("resetOtpExpiry");
            throw new RuntimeException("Mã OTP đã hết hạn. Vui lòng thử lại.");
        }

        // Check if OTP matches
        if (!resetOtp.equals(otp)) {
            throw new RuntimeException("Mã OTP không chính xác.");
        }

        // Check if passwords match
        if (!newPassword.equals(confirmNewPassword)) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp.");
        }

        User user = userRepository.findByEmail(resetEmail).orElse(null);
        // Update password if user is found
        if (user != null) {
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }

        session.removeAttribute("resetEmail");
        session.removeAttribute("resetOtp");
        session.removeAttribute("resetOtpExpiry");
    }
}
