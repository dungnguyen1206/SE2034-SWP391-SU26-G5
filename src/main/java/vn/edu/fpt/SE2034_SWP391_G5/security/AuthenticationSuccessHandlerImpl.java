package vn.edu.fpt.SE2034_SWP391_G5.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    // Điều hướng user về trang tương ứng sau khi đăng nhập thành công
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Vào trang của vai trò đầu tiên tìm được
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String homeUrl = getHomeUrl(authority.getAuthority());
            if (homeUrl != null) {
                response.sendRedirect(homeUrl);
                return;
            }
        }

        // Không có vai trò nào nhận diện được thì về trang chủ
        response.sendRedirect("/");
    }



    // Trang chủ tương ứng với từng vai trò
    private String getHomeUrl(String role) {
        switch (role) {
            case "ROLE_PATIENT":
                return "/patient/dashboard";
            case "ROLE_DOCTOR":
                return "/doctor/dashboard";
            case "ROLE_RECEPTIONIST":
                return "/receptionist/dashboard";
            case "ROLE_MANAGER":
                return "/manager/dashboard";
            case "ROLE_ADMIN":
                return "/admin/account-list";
            default:
                return null;
        }
    }
}
