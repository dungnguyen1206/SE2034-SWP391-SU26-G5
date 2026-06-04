package vn.edu.fpt.SE2034_SWP391_G5.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;

@Configuration
public class DoctorPasswordResetSeeder {

    @Bean
    CommandLineRunner resetDoctorPassword(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            User doctor = userRepository.findByEmail("dr.nguyenan@hams.vn")
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));

            doctor.setPasswordHash(passwordEncoder.encode("123456"));
            doctor.setStatus("ACTIVE");
            doctor.setEmailVerified(true);
            doctor.setExperienceYears(0);

            userRepository.save(doctor);

            System.out.println("Doctor password reset successfully");
        };
    }
}