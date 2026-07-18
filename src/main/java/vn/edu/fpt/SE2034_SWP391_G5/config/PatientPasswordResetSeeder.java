package vn.edu.fpt.SE2034_SWP391_G5.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;

@Configuration
public class PatientPasswordResetSeeder {

    @Bean
    CommandLineRunner resetPatientPassword(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            User patient = userRepository.findByEmail("nguyenminhthanh@gmail.com")
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));

            patient.setPasswordHash(passwordEncoder.encode("123"));
            patient.setStatus("ACTIVE");
            patient.setEmailVerified(true);

            userRepository.save(patient);

            System.out.println("Doctor password reset successfully");
        };
    }
}