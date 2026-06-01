package vn.edu.fpt.SE2034_SWP391_G5.controller.auth;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.RegisterPatientRequest;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Role;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.entity.UserRole;
import vn.edu.fpt.SE2034_SWP391_G5.entity.UserRoleId;
import vn.edu.fpt.SE2034_SWP391_G5.repository.RoleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRoleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.EmailService;

import java.time.LocalDateTime;

@Controller
public class AuthController {

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

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterPatientRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("registerRequest") RegisterPatientRequest registerRequest,
            BindingResult bindingResult,
            Model model,
            HttpSession session) {

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        // Kiểm tra mật khẩu khớp nhau
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.registerRequest", "Mật khẩu xác nhận không khớp");
            return "auth/register";
        }

        // Kiểm tra email tồn tại
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            bindingResult.rejectValue("email", "error.registerRequest", "Email này đã được sử dụng");
            return "auth/register";
        }

        // Tạo mã OTP ngẫu nhiên gồm 6 chữ số
        String otp = String.format("%06d", (int) (Math.random() * 1000000));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        // Lưu thông tin đăng ký tạm thời vào session
        session.setAttribute("pendingRegister", registerRequest);
        session.setAttribute("registerOtp", otp);
        session.setAttribute("otpExpiry", expiryTime);

        try {
            // Gửi OTP qua email
            emailService.sendOtpEmail(registerRequest.getEmail(), otp);
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi gửi email OTP: " + e.getMessage() + ". Vui lòng thử lại.");
            return "auth/register";
        }

        return "redirect:/verify-otp";
    }

    @GetMapping("/verify-otp")
    public String showVerifyOtpForm(HttpSession session) {
        RegisterPatientRequest pending = (RegisterPatientRequest) session.getAttribute("pendingRegister");
        if (pending == null) {
            return "redirect:/register";
        }
        return "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam("otp") String otp,
            HttpSession session,
            Model model) {

        RegisterPatientRequest pendingRegister = (RegisterPatientRequest) session.getAttribute("pendingRegister");
        String registerOtp = (String) session.getAttribute("registerOtp");
        LocalDateTime expiryTime = (LocalDateTime) session.getAttribute("otpExpiry");

        if (pendingRegister == null || registerOtp == null || expiryTime == null) {
            return "redirect:/register";
        }

        // Kiểm tra hết hạn mã OTP
        if (LocalDateTime.now().isAfter(expiryTime)) {
            model.addAttribute("error", "Mã OTP đã hết hạn. Vui lòng đăng ký lại.");
            // Xóa session cũ
            session.removeAttribute("pendingRegister");
            session.removeAttribute("registerOtp");
            session.removeAttribute("otpExpiry");
            return "auth/verify-otp";
        }

        // Kiểm tra khớp OTP
        if (!registerOtp.equals(otp)) {
            model.addAttribute("error", "Mã OTP không chính xác. Vui lòng kiểm tra lại.");
            return "auth/verify-otp";
        }

        // Tạo tài khoản người dùng
        User user = new User();
        user.setUsername(pendingRegister.getEmail());
        user.setEmail(pendingRegister.getEmail());
        user.setPasswordHash(passwordEncoder.encode(pendingRegister.getPassword()));
        user.setFirstName(pendingRegister.getFirstName());
        user.setMiddleName(pendingRegister.getMiddleName());
        user.setLastName(pendingRegister.getLastName());
        user.setPhone(pendingRegister.getPhone());
        user.setExperienceYears(0); // Thiết lập giá trị mặc định là 0 để tránh lỗi NOT NULL
        user.setStatus("ACTIVE");
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Lưu User
        userRepository.save(user);

        // Lấy Role PATIENT mặc định
        Role patientRole = roleRepository.findByName("PATIENT")
                .orElseThrow(() -> new RuntimeException("Lỗi cấu hình hệ thống: Vai trò PATIENT không tồn tại."));

        // Gán vai trò cho User thông qua bảng trung gian UserRole
        UserRole userRole = new UserRole();
        UserRoleId userRoleId = new UserRoleId(user.getId(), patientRole.getId());
        userRole.setId(userRoleId);
        userRole.setUser(user);
        userRole.setRole(patientRole);
        userRole.setAssignedAt(LocalDateTime.now());

        userRoleRepository.save(userRole);

        // Xóa thông tin tạm thời trong Session
        session.removeAttribute("pendingRegister");
        session.removeAttribute("registerOtp");
        session.removeAttribute("otpExpiry");

        return "redirect:/login?success=true";
    }
}
