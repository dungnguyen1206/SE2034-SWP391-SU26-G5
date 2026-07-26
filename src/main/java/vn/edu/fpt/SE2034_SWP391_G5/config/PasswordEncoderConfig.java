package vn.edu.fpt.SE2034_SWP391_G5.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Khai báo cách mã hóa mật khẩu dùng chung cho toàn hệ thống
@Configuration
public class PasswordEncoderConfig {

    // Mật khẩu luôn được băm bằng BCrypt trước khi lưu
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
