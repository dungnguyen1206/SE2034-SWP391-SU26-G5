package vn.edu.fpt.SE2034_SWP391_G5.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_PATIENT")) {
                // response.sendRedirect("/patient/home");
                response.sendRedirect("/patient/dashboard");
                return;
            } else if (role.equals("ROLE_DOCTOR")) {
                response.sendRedirect("/doctor/home");
                return;
            } else if (role.equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin/home");
                return;
            }else if (role.equals("ROLE_MANAGER")) {
                response.sendRedirect("/manager/dashboard");
                return;
            }else if (role.equals("ROLE_RECEPTIONIST")) {
                response.sendRedirect("/receptionist/dashboard");
                return;
            }
        }

        // Chuyển hướng mặc định nếu không khớp vai trò
        response.sendRedirect("/");
    }
}

