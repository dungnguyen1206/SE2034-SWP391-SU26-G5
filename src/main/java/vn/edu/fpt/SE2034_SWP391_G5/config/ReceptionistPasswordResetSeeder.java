package vn.edu.fpt.SE2034_SWP391_G5.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;

@Configuration
public class ReceptionistPasswordResetSeeder {

    @Bean
    CommandLineRunner resetReceptionistPassword(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            userRepository.findByEmail("recept.linh@hams.vn").ifPresentOrElse(
                receptionist -> {
                    receptionist.setPasswordHash(passwordEncoder.encode("123456"));
                    receptionist.setStatus("ACTIVE");
                    receptionist.setEmailVerified(true);
                    userRepository.save(receptionist);
                    System.out.println("Receptionist password reset successfully");
                },
                () -> System.out.println("Receptionist recept.linh@hams.vn not found, skipping password reset")
            );
        };
    }
}