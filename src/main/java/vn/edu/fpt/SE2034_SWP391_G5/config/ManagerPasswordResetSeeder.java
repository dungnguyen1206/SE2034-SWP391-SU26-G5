package vn.edu.fpt.SE2034_SWP391_G5.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;

@Configuration
public class ManagerPasswordResetSeeder {

    @Bean
    CommandLineRunner resetManagerPassword(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            User manager = userRepository.findByEmail("manager01@hams.vn")
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));

            manager.setPasswordHash(passwordEncoder.encode("123"));
            manager.setStatus("ACTIVE");
            manager.setEmailVerified(true);

            userRepository.save(manager);

            System.out.println("Doctor password reset successfully");
        };
    }
}