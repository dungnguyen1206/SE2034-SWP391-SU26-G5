package vn.edu.fpt.SE2034_SWP391_G5.controller.doctor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalRecordRepository;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorDashboardController {

    private final AppointmentService appointmentService;
    private final MedicalRecordRepository medicalRecordRepository;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null || userDetails.getUser() == null) {
            return "redirect:/login";
        }

        Long doctorId = userDetails.getUser().getId();
        LocalDate today = LocalDate.now();
        String doctorAvatar = userDetails.getUser().getAvatar();
        model.addAttribute("doctorAvatar", doctorAvatar);

        // 1. Số bệnh nhân hôm nay (WAITING, IN_PROGRESS, COMPLETED)
        long todayPatients = appointmentService.countAppointmentsForDoctor(doctorId, today, "ALL");
        model.addAttribute("todayPatients", todayPatients);

        // 2. Hồ sơ chờ duyệt (WAITING)
        long pendingRecords = appointmentService.countAppointmentsForDoctor(doctorId, today, "WAITING");
        model.addAttribute("pendingRecords", pendingRecords);

        // 3. Số hồ sơ đã hoàn thành (COMPLETED)
        long completedRecords = appointmentService.countAppointmentsForDoctor(doctorId, today, "COMPLETED");
        model.addAttribute("completedRecords", completedRecords);

        // 4. Đơn thuốc hôm nay
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        long todayPrescriptions = medicalRecordRepository.countPrescriptionsByDoctorAndDate(doctorId, startOfDay, endOfDay);
        model.addAttribute("todayPrescriptions", todayPrescriptions);

        // 5. Danh sách 4 lịch hẹn đầu tiên trong ngày
        Pageable pageable = PageRequest.of(0, 4, Sort.by("slot.startTime").ascending());
        List<AppointmentResponse> appointments = appointmentService.getAppointmentsForDoctor(doctorId, today, "ALL", pageable).getContent();
        model.addAttribute("appointments", appointments);

        // 6. Danh sách 4 bệnh nhân khám xong gần đây nhất
        List<AppointmentResponse> recentPatients = appointmentService.getRecentCompletedAppointmentsForDoctor(doctorId, 4);
        model.addAttribute("recentPatients", recentPatients);

        return "doctor/dashboard";
    }
}

