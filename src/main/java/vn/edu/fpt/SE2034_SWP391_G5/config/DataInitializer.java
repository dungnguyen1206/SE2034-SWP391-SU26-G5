package vn.edu.fpt.SE2034_SWP391_G5.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.edu.fpt.SE2034_SWP391_G5.entity.*;
import vn.edu.fpt.SE2034_SWP391_G5.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tự động tạo dữ liệu mẫu khi ứng dụng khởi động lần đầu:
 *
 * 1. Tài khoản:
 *    - admin03 / 123456  (ADMIN)
 *    - manager / 123456  (MANAGER)
 *    - doctor01 / 123456 (DOCTOR)
 *
 * 2. Lịch làm việc cho bác sĩ (từ hôm nay đến +5 ngày):
 *    - Mỗi khoa: bác sĩ ĐẦU TIÊN sẽ KHÔNG có lịch (để test case "bác sĩ không có lịch")
 *    - Các bác sĩ còn lại: tạo 2 ca/ngày (MORNING & AFTERNOON), mỗi ca 4 slot
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final RoomRepository roomRepository;
    private final DepartmentRepository departmentRepository;

    // Time slots cho ca SÁNG: 4 slot, mỗi slot 30 phút
    private static final LocalTime[][] MORNING_SLOTS = {
            {LocalTime.of(8, 0),  LocalTime.of(8, 30)},
            {LocalTime.of(8, 30), LocalTime.of(9, 0)},
            {LocalTime.of(9, 0),  LocalTime.of(9, 30)},
            {LocalTime.of(9, 30), LocalTime.of(10, 0)},
    };

    // Time slots cho ca CHIỀU: 4 slot
    private static final LocalTime[][] AFTERNOON_SLOTS = {
            {LocalTime.of(13, 0),  LocalTime.of(13, 30)},
            {LocalTime.of(13, 30), LocalTime.of(14, 0)},
            {LocalTime.of(14, 0),  LocalTime.of(14, 30)},
            {LocalTime.of(14, 30), LocalTime.of(15, 0)},
    };

    @Override
    public void run(String... args) {
        // 1. Tạo tài khoản mẫu
        createAccountIfNotExists("admin03",  "admin03@hams.vn",  "Admin",   "ADMIN");
        createAccountIfNotExists("manager",  "manager@hams.vn",  "Manager", "MANAGER");
        createAccountIfNotExists("doctor01", "doctor@hams.vn",   "Doctor",  "DOCTOR");

        // 2. Tạo lịch làm việc mẫu
        seedDoctorSchedules();
    }

    // =========================================================================
    // TẠO TÀI KHOẢN
    // =========================================================================

    private void createAccountIfNotExists(String username, String email, String firstName, String roleName) {
        if (userRepository.existsByEmail(email)) {
            log.info("[DataInitializer] Tài khoản '{}' ({}) đã tồn tại, bỏ qua.", username, email);
            return;
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException(
                        "[DataInitializer] Không tìm thấy role '" + roleName + "'. Bảng roles chưa có dữ liệu."));

        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode("123456"))
                .firstName(firstName)
                .lastName("HAMS")
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
            user.setLicenseNumber("LIC-SAMPLE-001");
        }

        user = userRepository.save(user);

        UserRoleId userRoleId = new UserRoleId(user.getId(), role.getId());
        userRoleRepository.save(UserRole.builder()
                .id(userRoleId)
                .user(user)
                .role(role)
                .assignedAt(LocalDateTime.now())
                .build());

        log.info("[DataInitializer] Đã tạo tài khoản {} - {} với role {}", username, email, roleName);
    }

    // =========================================================================
    // TẠO LỊCH LÀM VIỆC
    // =========================================================================

    private void seedDoctorSchedules() {
        // Lấy tất cả doctor ACTIVE
        List<User> allDoctors = userRepository.findByRoleName("DOCTOR").stream()
                .filter(d -> "ACTIVE".equals(d.getDoctorStatus()) && d.getDepartment() != null)
                .collect(Collectors.toList());

        if (allDoctors.isEmpty()) {
            log.warn("[DataInitializer] Không có bác sĩ nào có department, bỏ qua tạo lịch.");
            return;
        }

        // Nhóm bác sĩ theo khoa
        Map<Integer, List<User>> doctorsByDept = allDoctors.stream()
                .collect(Collectors.groupingBy(d -> d.getDepartment().getId()));

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(5);

        for (Map.Entry<Integer, List<User>> entry : doctorsByDept.entrySet()) {
            Integer deptId = entry.getKey();
            List<User> doctors = entry.getValue();

            // Lấy 1 phòng thuộc khoa này (nếu có)
            Room room = roomRepository.findAll().stream()
                    .filter(r -> r.getDepartment() != null
                            && r.getDepartment().getId().equals(deptId)
                            && "ACTIVE".equals(r.getStatus()))
                    .findFirst()
                    .orElse(null);

            // Bác sĩ đầu tiên của mỗi khoa → KHÔNG tạo lịch (test case "không có lịch")
            boolean skipFirst = true;

            for (User doctor : doctors) {
                if (skipFirst) {
                    log.info("[DataInitializer] Khoa {}: Bác sĩ '{}' (id={}) được giữ KHÔNG có lịch để test.",
                            deptId, doctor.getUsername() != null ? doctor.getUsername() : doctor.getEmail(), doctor.getId());
                    skipFirst = false;
                    continue;
                }

                // Tạo lịch cho từng ngày từ hôm nay đến +5
                for (LocalDate date = today; !date.isAfter(endDate); date = date.plusDays(1)) {
                    createScheduleForDoctor(doctor, room, date, "MORNING", MORNING_SLOTS);
                    createScheduleForDoctor(doctor, room, date, "AFTERNOON", AFTERNOON_SLOTS);
                }

                log.info("[DataInitializer] Đã tạo lịch cho bác sĩ '{}' (id={}) từ {} đến {}",
                        doctor.getUsername() != null ? doctor.getUsername() : doctor.getEmail(),
                        doctor.getId(), today, endDate);
            }
        }
    }

    /**
     * Tạo 1 DoctorSchedule + các TimeSlot cho ca đó.
     * Bỏ qua nếu đã tồn tại lịch cùng doctor + ngày + ca.
     */
    private void createScheduleForDoctor(User doctor, Room room, LocalDate date,
                                         String shift, LocalTime[][] slots) {
        // Kiểm tra đã có lịch chưa (tránh duplicate khi restart)
        boolean alreadyExists = doctorScheduleRepository
                .findByDoctorIdAndWorkDateBetweenAndStatusOrderByWorkDateAscShiftAsc(
                        doctor.getId(), date, date, "ACTIVE")
                .stream()
                .anyMatch(s -> shift.equals(s.getShift()));

        if (alreadyExists) {
            return;
        }

        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setDoctor(doctor);
        schedule.setRoom(room);
        schedule.setWorkDate(date);
        schedule.setShift(shift);
        schedule.setStatus("ACTIVE");
        schedule.setCreatedAt(LocalDateTime.now());
        schedule.setUpdatedAt(LocalDateTime.now());
        schedule = doctorScheduleRepository.save(schedule);

        // Tạo time slots cho ca này
        for (LocalTime[] slot : slots) {
            TimeSlot ts = new TimeSlot();
            ts.setSchedule(schedule);
            ts.setStartTime(slot[0]);
            ts.setEndTime(slot[1]);
            ts.setBookedCapacity(0);
            ts.setMaxCapacity(5);
            ts.setStatus("AVAILABLE");
            ts.setVersion(0L);
            timeSlotRepository.save(ts);
        }
    }
}
