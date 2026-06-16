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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Tiêm Handler cấu hình chuyển hướng vào đây
    @Autowired
    private AuthenticationSuccessHandler successHandler;

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
                // Trước: chỉ có "/", "/register", "/verify-otp", "/login", "/forgot-password", "/reset-password"
                // Thêm: "/home", "/home/**" để guest xem được trang chủ
                .requestMatchers(
                    "/", "/home", "/home/**",
                    "/register", "/verify-otp",
                    "/login",
                    "/forgot-password", "/reset-password",
                    "/css/**", "/js/**", "/images/**"
                ).permitAll()
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/manager/**").hasAuthority("ROLE_MANAGER")
                .requestMatchers("/doctor/**").hasAuthority("ROLE_DOCTOR")
                .requestMatchers("/receptionist/**").hasAuthority("ROLE_RECEPTIONIST")
                .requestMatchers("/patient/**").hasAuthority("ROLE_PATIENT")
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
