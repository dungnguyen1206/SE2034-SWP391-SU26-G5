package vn.edu.fpt.SE2034_SWP391_G5.controller.doctor;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.MedicalRecordResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalRecord;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalService;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalRecordRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.MedicalServiceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.ProvinceRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.MedicalRecordService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorPatientDetailController {

    private final AppointmentService appointmentService;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordService medicalRecordService;
    private final MedicalServiceRepository medicalServiceRepository;
    private final ProvinceRepository provinceRepository;
    private final UserRepository userRepository;

    @GetMapping("/appointments/{id}/detail")
    public String appointmentDetail(
            @PathVariable Long id,
            @RequestParam(value = "tab", required = false, defaultValue = "info") String tab,
            @RequestParam(value = "action", required = false) String action,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        Long doctorId = userDetails.getUser().getId();
        AppointmentResponse appointment = appointmentService.getAppointmentDetailForReceptionist(id);
        
        // Security check: ensure this appointment belongs to the logged-in doctor
        if (appointment == null || !doctorId.equals(appointment.getDoctorId())) {
            throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền xem thông tin lịch hẹn này");
        }
        
        Optional<MedicalRecord> medicalRecordOpt = medicalRecordRepository.findByAppointmentId(id);
        model.addAttribute("medicalRecord", medicalRecordOpt.orElse(null));
        
        List<MedicalService> activeServices = medicalServiceRepository.findByStatus("ACTIVE");
        model.addAttribute("activeServices", activeServices);
        
        List<MedicalRecordResponse> historyList = medicalRecordService.getPatientMedicalHistory(appointment.getPatientId());
        User currentDoctor = userRepository.findById(doctorId).orElse(null);
        if (currentDoctor != null && currentDoctor.getDepartment() != null) {
            String deptName = currentDoctor.getDepartment().getName();
            if (deptName != null) {
                historyList = historyList.stream()
                        .filter(r -> deptName.equals(r.getDepartmentName()))
                        .toList();
            }
        }
        model.addAttribute("historyList", historyList);
        
        model.addAttribute("appointment", appointment);
        model.addAttribute("tab", tab);
        model.addAttribute("action", action);
        model.addAttribute("provinces", provinceRepository.findAll());
        return "doctor/patient-detail";
    }
}
