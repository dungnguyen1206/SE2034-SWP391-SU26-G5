package vn.edu.fpt.SE2034_SWP391_G5.controller.auth;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.RegisterPatientRequest;
import vn.edu.fpt.SE2034_SWP391_G5.service.AuthService;

@Controller
@RequestMapping("")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Show login page
    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    // Show registration page
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterPatientRequest());
        return "auth/register";
    }

    // Handle registration form submission
    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("registerRequest") RegisterPatientRequest registerRequest,
            BindingResult bindingResult,
            Model model,
            HttpSession session,
            @RequestParam(value = "otpChannel", defaultValue = "email") String otpChannel) {

        // Check for basic validation errors
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Vui lòng nhập đầy đủ và đúng định dạng các trường bắt buộc.");
            return "auth/register";
        }

        try {
            authService.processRegistration(registerRequest, session, otpChannel);
            return "redirect:/verify-otp";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    // Show OTP verification page
    @GetMapping("/verify-otp")
    public String showVerifyOtpForm(HttpSession session, Model model) {
        RegisterPatientRequest pending = (RegisterPatientRequest) session.getAttribute("pendingRegister");
        // Redirect to register if no pending registration
        if (pending == null) {
            return "redirect:/register";
        }
        String otpChannel = (String) session.getAttribute("otpChannel");
        model.addAttribute("otpChannel", otpChannel);
        return "auth/verify-otp";
    }

    // Handle OTP verification submission
    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam("otp") String otp,
            HttpSession session,
            Model model) {

        try {
            authService.verifyOtp(otp, session);
            return "redirect:/login?success=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/verify-otp";
        }
    }

    // Show forgot password page
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    // Handle forgot password submission and send OTP
    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "otpChannel", defaultValue = "email") String otpChannel,
            Model model,
            HttpSession session) {
        
        try {
            authService.processForgotPassword(email, phone, session, otpChannel);
            return "redirect:/reset-password";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/forgot-password";
        }
    }

    // Show reset password page
    @GetMapping("/reset-password")
    public String showResetPasswordForm(HttpSession session, Model model) {
        String resetIdentifier = (String) session.getAttribute("resetIdentifier");
        // Redirect to forgot password if no reset session exists
        if (resetIdentifier == null) {
            return "redirect:/forgot-password";
        }
        String otpChannel = (String) session.getAttribute("otpChannel");
        model.addAttribute("otpChannel", otpChannel);
        return "auth/reset-password";
    }

    // Handle reset password submission
    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam("otp") String otp,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmNewPassword") String confirmNewPassword,
            HttpSession session,
            Model model) {

        try {
            authService.processResetPassword(otp, newPassword, confirmNewPassword, session);
            return "redirect:/login?resetSuccess=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/reset-password";
        }
    }
}
