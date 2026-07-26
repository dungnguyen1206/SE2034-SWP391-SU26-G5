package vn.edu.fpt.SE2034_SWP391_G5.controller.auth;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.OtpToken;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.RegisterPatientRequest;
import vn.edu.fpt.SE2034_SWP391_G5.service.AuthService;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

// Controller xử lý xác thực: đăng nhập, đăng ký, OTP, mật khẩu
@Controller
@RequestMapping("")
@RequiredArgsConstructor
public class AuthController {

    // Tên các khóa lưu tạm trong session
    private static final String PENDING_REGISTER = "pendingRegister";
    private static final String REGISTER_OTP_TOKEN = "registerOtpToken";
    private static final String RESET_OTP_TOKEN = "resetOtpToken";

    private final AuthService authService;

    // Hiển thị form đăng nhập
    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    // Hiển thị form đăng ký
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterPatientRequest());
        return "auth/register";
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("registerRequest") RegisterPatientRequest registerRequest,
            BindingResult bindingResult,
            Model model,
            HttpSession session,
            @RequestParam(value = "otpChannel", defaultValue = "email") String otpChannel) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Vui lòng nhập đầy đủ và đúng định dạng các trường bắt buộc.");
            return "auth/register";
        }

        try {
            OtpToken otpToken = authService.startRegistration(registerRequest, otpChannel);

            // Giữ thông tin đăng ký và mã OTP để bước sau xác minh
            session.setAttribute(PENDING_REGISTER, registerRequest);
            session.setAttribute(REGISTER_OTP_TOKEN, otpToken);

            return "redirect:/verify-otp";
        } catch (BadRequestException | ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    // Hiển thị form xác minh OTP
    @GetMapping("/verify-otp")
    public String showVerifyOtpForm(HttpSession session, Model model) {
        OtpToken otpToken = (OtpToken) session.getAttribute(REGISTER_OTP_TOKEN);

        // Không có phiên đăng ký → quay lại trang đăng ký
        if (session.getAttribute(PENDING_REGISTER) == null || otpToken == null) {
            return "redirect:/register";
        }

        model.addAttribute("otpChannel", otpToken.getChannel());
        return "auth/verify-otp";
    }

    // Xử lý xác minh OTP
    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam("otp") String otp,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        RegisterPatientRequest pendingRegister = (RegisterPatientRequest) session.getAttribute(PENDING_REGISTER);
        OtpToken otpToken = (OtpToken) session.getAttribute(REGISTER_OTP_TOKEN);

        // Mất dữ liệu đăng ký thì phải làm lại từ đầu
        if (pendingRegister == null || otpToken == null) {
            model.addAttribute("error", "Dữ liệu đăng ký không tồn tại hoặc đã bị xóa. Vui lòng đăng ký lại.");
            return "auth/verify-otp";
        }

        try {
            authService.verifyOtp(otpToken, otp);
            authService.completeRegistration(pendingRegister);

            // Đăng ký xong thì dọn dữ liệu tạm
            session.removeAttribute(PENDING_REGISTER);
            session.removeAttribute(REGISTER_OTP_TOKEN);

            return "redirect:/login?success=true";
        } catch (BadRequestException | ResourceNotFoundException e) {
            // Mã hết hạn hoặc sai quá số lần cho phép thì hủy phiên, bắt đăng ký lại
            if (authService.isOtpVoided(otpToken)) {
                session.removeAttribute(PENDING_REGISTER);
                session.removeAttribute(REGISTER_OTP_TOKEN);
                redirectAttributes.addFlashAttribute("error", e.getMessage());
                return "redirect:/register";
            }

            model.addAttribute("error", e.getMessage());
            model.addAttribute("otpChannel", otpToken.getChannel());
            return "auth/verify-otp";
        }
    }

    // Hiển thị form quên mật khẩu
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    // Xử lý quên mật khẩu
    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "otpChannel", defaultValue = "email") String otpChannel,
            Model model,
            HttpSession session) {

        try {
            OtpToken otpToken = authService.startPasswordReset(email, phone, otpChannel);

            // Giữ mã OTP và tài khoản cần đặt lại mật khẩu
            session.setAttribute(RESET_OTP_TOKEN, otpToken);

            return "redirect:/reset-password";
        } catch (BadRequestException | ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/forgot-password";
        }
    }

    // Hiển thị trang đặt lại mật khẩu
    @GetMapping("/reset-password")
    public String showResetPasswordForm(HttpSession session, Model model) {
        OtpToken otpToken = (OtpToken) session.getAttribute(RESET_OTP_TOKEN);

        // Chưa có phiên reset → quay lại trang quên mật khẩu
        if (otpToken == null) {
            return "redirect:/forgot-password";
        }

        model.addAttribute("otpChannel", otpToken.getChannel());
        return "auth/reset-password";
    }

    // Xử lý đặt lại mật khẩu
    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam("otp") String otp,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmNewPassword") String confirmNewPassword,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        OtpToken otpToken = (OtpToken) session.getAttribute(RESET_OTP_TOKEN);

        // Mất phiên quên mật khẩu thì phải làm lại từ đầu
        if (otpToken == null) {
            model.addAttribute("error", "Dữ liệu quên mật khẩu không tồn tại. Vui lòng thử lại.");
            return "auth/reset-password";
        }

        try {
            authService.verifyOtp(otpToken, otp);
            authService.resetPassword(otpToken.getIdentifier(), otpToken.getChannel(), newPassword, confirmNewPassword);

            // Đặt lại xong thì dọn dữ liệu tạm
            session.removeAttribute(RESET_OTP_TOKEN);

            return "redirect:/login?resetSuccess=true";
        } catch (BadRequestException | ResourceNotFoundException e) {
            // Mã hết hạn hoặc sai quá số lần cho phép thì hủy phiên, bắt yêu cầu mã mới
            if (authService.isOtpVoided(otpToken)) {
                session.removeAttribute(RESET_OTP_TOKEN);
                redirectAttributes.addFlashAttribute("error", e.getMessage());
                return "redirect:/forgot-password";
            }

            model.addAttribute("error", e.getMessage());
            model.addAttribute("otpChannel", otpToken.getChannel());
            return "auth/reset-password";
        }
    }

    // Hiển thị trang đổi mật khẩu
    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "auth/change-password";
    }

    // Xử lý đổi mật khẩu
    @PostMapping("/change-password")
    public String processChangePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmNewPassword") String confirmNewPassword,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        User currentUser = userDetails != null ? userDetails.getUser() : null;

        try {
            authService.changePassword(currentUser, oldPassword, newPassword, confirmNewPassword);
            return "redirect:/change-password?success=true";
        } catch (BadRequestException | ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/change-password";
        }
    }
}
