package vn.edu.fpt.SE2034_SWP391_G5.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.edu.fpt.SE2034_SWP391_G5.entity.*;
import vn.edu.fpt.SE2034_SWP391_G5.repository.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Tạo tài khoản dev khi app khởi động — chỉ tạo nếu chưa tồn tại.
 *
 * Tài khoản (username / password):
 *   admin123  / 123456   → ADMIN
 *   admin456  / 123456   → ADMIN
 *   manager123/ 123456   → MANAGER
 *   manager456/ 123456   → MANAGER
 *   doctor123 / 123456   → DOCTOR
 *   doctor456 / 123456   → DOCTOR
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DevDataInitializer implements CommandLineRunner {

    private final UserRepository     userRepository;
    private final RoleRepository     roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder    passwordEncoder;

    private static final String PASSWORD = "123456";

    @Override
    public void run(String... args) {
        seed("admin123",   "admin123@hams.vn",   N("Admin"),   N("One"),   "ADMIN",   null);
        seed("admin456",   "admin456@hams.vn",   N("Admin"),   N("Two"),   "ADMIN",   null);
        seed("manager123", "manager123@hams.vn", N("Manager"), N("One"),   "MANAGER", null);
        seed("manager456", "manager456@hams.vn", N("Manager"), N("Two"),   "MANAGER", null);
        seed("doctor123",  "doctor123@hams.vn",  N("Doctor"),  N("One"),   "DOCTOR",  firstDept());
        seed("doctor456",  "doctor456@hams.vn",  N("Doctor"),  N("Two"),   "DOCTOR",  firstDept());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private static String N(String s) { return s; }   // just for readability

    private Department firstDept() {
        return departmentRepository.findAll().stream()
                .filter(d -> "ACTIVE".equals(d.getStatus()))
                .findFirst()
                .orElse(null);
    }

    private void seed(String username, String email, String firstName, String lastName,
                      String roleName, Department department) {

        if (userRepository.existsByUsername(username)) {
            log.info("[DevDataInitializer] '{}' đã tồn tại, bỏ qua.", username);
            return;
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException(
                        "Thiếu role '" + roleName + "' trong bảng roles."));

        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(PASSWORD))
                .firstName(firstName)
                .lastName(lastName)
                .status("ACTIVE")
                .emailVerified(true)
                .emailVerifiedAt(LocalDateTime.now())
                .experienceYears(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        if ("DOCTOR".equals(roleName)) {
            user.setDoctorStatus("ACTIVE");
            user.setDegree("MD");
            user.setExperienceYears(1);
            user.setLicenseNumber("DEV-" + username.toUpperCase());
            if (department != null) user.setDepartment(department);
        }

        user = userRepository.save(user);

        userRoleRepository.save(
                UserRole.builder()
                        .id(new UserRoleId(user.getId(), role.getId()))
                        .user(user)
                        .role(role)
                        .assignedAt(LocalDateTime.now())
                        .build()
        );

        log.info("[DevDataInitializer] Tạo xong {} ({}) → {}", username, email, roleName);
    }
}
