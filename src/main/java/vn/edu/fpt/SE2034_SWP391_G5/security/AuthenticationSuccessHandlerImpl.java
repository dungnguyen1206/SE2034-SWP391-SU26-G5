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
        
        // Ưu tiên role theo thứ tự: ADMIN > MANAGER > DOCTOR > RECEPTIONIST > PATIENT
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if ("ADMIN".equals(role)) {
                response.sendRedirect("/admin/account-list");
                return;
            }
        }
        
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if ("MANAGER".equals(role)) {
                response.sendRedirect("/manager/dashboard");
                return;
            }
        }
        
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if ("DOCTOR".equals(role)) {
                response.sendRedirect("/doctor/dashboard");
                return;
            }
        }
        
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if ("RECEPTIONIST".equals(role)) {
                response.sendRedirect("/receptionist/dashboard");
                return;
            }
        }
        
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if ("PATIENT".equals(role)) {
                response.sendRedirect("/patient/dashboard");
                return;
            }
        }

        response.sendRedirect("/");
    }
}
