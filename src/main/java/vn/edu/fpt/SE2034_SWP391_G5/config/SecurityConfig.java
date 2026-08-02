package vn.edu.fpt.SE2034_SWP391_G5.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.SecurityFilterChain;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

// Cấu hình bảo mật: phân quyền theo đường dẫn, xử lý đăng nhập và đăng xuất
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationSuccessHandler successHandler;

    // Nơi Spring Security lấy tài khoản và cách so mật khẩu khi đăng nhập
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    // Khai báo trang nào cần vai trò gì, kèm cấu hình đăng nhập và đăng xuất
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth

                // Trang công khai - không cần đăng nhập
                .requestMatchers(
                    "/", "/home", "/home/**",
                    "/register", "/verify-otp",
                    "/login",
                    "/forgot-password", "/reset-password",
                    "/css/**", "/js/**", "/images/**",
                    // Kiosk - màn hình cảm ứng tại bệnh viện, không cần đăng nhập
                    "/kiosk/**",
                    // Xem danh sách + chi tiết chuyên khoa — public
                    "/patient/departments", "/patient/departments/**",
                    "/departments", "/departments/**",
                    // Xem danh sách + chi tiết bác sĩ — public
                    "/doctors", "/doctors/**",
                    // Xem bài viết - public
                    "/articles", "/articles/**"
                ).permitAll()
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/manager/**").hasAuthority("ROLE_MANAGER")
                .requestMatchers("/doctor/**").hasAuthority("ROLE_DOCTOR")
                .requestMatchers("/receptionist/**").hasAuthority("ROLE_RECEPTIONIST")
                // Đặt lịch và các tính năng cá nhân vẫn yêu cầu login
                .requestMatchers("/patient/**").hasAuthority("ROLE_PATIENT")
                // Thông báo dùng chung cho tất cả các role đã đăng nhập
                .requestMatchers("/notifications/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(successHandler)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
