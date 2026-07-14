package vn.edu.fpt.SE2034_SWP391_G5.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationSuccessHandler successHandler;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

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
                    // Xem danh sách + chi tiết chuyên khoa — public
                    "/patient/departments", "/patient/departments/**",
                    // Xem danh sách + chi tiết bác sĩ — public
                    "/doctors", "/doctors/**",
                    // Xem bài viết - public
                    "/articles", "/articles/**"
                    // Public pages - bất kỳ ai cũng xem được
                    "/public/**",
                    "/doctors", "/doctors/**"  // Public doctor list & detail
                ).permitAll()
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/manager/**").hasAuthority("ROLE_MANAGER")
                .requestMatchers("/doctor/**").hasAuthority("ROLE_DOCTOR")
                .requestMatchers("/receptionist/**").hasAuthority("ROLE_RECEPTIONIST")
                // Patient pages - CHỈ cho ROLE_PATIENT
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
                .logoutSuccessUrl("/home")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
