package vn.edu.fpt.SE2034_SWP391_G5.controller.doctor;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentResponse;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.MedicalRecordResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalRecord;
import vn.edu.fpt.SE2034_SWP391_G5.entity.MedicalService;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.AppointmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.MedicalRecordService;
import vn.edu.fpt.SE2034_SWP391_G5.service.MedicalServiceService;
import vn.edu.fpt.SE2034_SWP391_G5.service.ProvinceService;
import vn.edu.fpt.SE2034_SWP391_G5.service.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorPatientDetailController {

    private final AppointmentService appointmentService;
    private final MedicalRecordService medicalRecordService;
    private final MedicalServiceService medicalServiceService;
    private final ProvinceService provinceService;
    private final UserService userService;

    @GetMapping("/appointments/{id}/detail")
    public String appointmentDetail(
            @PathVariable Long id,
            @RequestParam(value = "tab", required = false, defaultValue = "info") String tab,
            @RequestParam(value = "action", required = false) String action,
            @RequestParam(value = "historyPage", required = false, defaultValue = "0") int historyPage,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        Long doctorId = userDetails.getUser().getId();
        AppointmentResponse appointment = appointmentService.getAppointmentDetailForReceptionist(id);
        
        // Security check: ensure this appointment belongs to the logged-in doctor
        if (appointment == null || !doctorId.equals(appointment.getDoctorId())) {
            throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền xem thông tin lịch hẹn này");
        }
        
        Optional<MedicalRecord> medicalRecordOpt = medicalRecordService.getMedicalRecordByAppointmentId(id);
        model.addAttribute("medicalRecord", medicalRecordOpt.orElse(null));
        
        List<MedicalService> activeServices = medicalServiceService.getMedicalServicelistByDepartment(null);
        model.addAttribute("activeServices", activeServices);
        
        User currentDoctor = userService.getUserById(doctorId);
        String deptName = null;
        if (currentDoctor != null && currentDoctor.getDepartment() != null) {
            deptName = currentDoctor.getDepartment().getName();
        }
        
        Page<MedicalRecordResponse> historyPageData = medicalRecordService.getPatientMedicalHistoryPaginated(
                appointment.getPatientId(), deptName, historyPage, 8);
        
        model.addAttribute("historyList", historyPageData.getContent());
        model.addAttribute("historyPage", historyPageData);
        
        model.addAttribute("appointment", appointment);
        model.addAttribute("tab", tab);
        model.addAttribute("action", action);
        model.addAttribute("provinces", provinceService.getAllProvinces());
        return "doctor/patient-detail";
    }
}
