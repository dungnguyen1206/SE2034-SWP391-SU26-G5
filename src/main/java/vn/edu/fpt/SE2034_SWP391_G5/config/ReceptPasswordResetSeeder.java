package vn.edu.fpt.SE2034_SWP391_G5.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;

@Configuration
public class ReceptPasswordResetSeeder {

    @Bean
    CommandLineRunner resetReceptPassword(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            User recept = userRepository.findByEmail("recept01@hams.vn")
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));

            recept.setPasswordHash(passwordEncoder.encode("123"));
            recept.setStatus("ACTIVE");
            recept.setEmailVerified(true);

            userRepository.save(recept);

            System.out.println("Doctor password reset successfully");
        };
    }
}