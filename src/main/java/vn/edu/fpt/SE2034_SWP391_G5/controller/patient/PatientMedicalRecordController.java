package vn.edu.fpt.SE2034_SWP391_G5.controller.patient;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.MedicalRecordResponse;
import vn.edu.fpt.SE2034_SWP391_G5.security.CustomUserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.service.PatientService;

import java.util.List;

@Controller
@RequestMapping("/patient/records")
@RequiredArgsConstructor
public class PatientMedicalRecordController {

    private final PatientService patientService;

    // private static final Long DEMO_PATIENT_ID = 14L;

    @GetMapping
    // public String listRecords(Model model) {
    //     List<MedicalRecordResponse> records = patientService.getMedicalRecords(DEMO_PATIENT_ID);
    public String listRecords(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<MedicalRecordResponse> records = patientService.getMedicalRecords(userDetails.getUser().getId());
        model.addAttribute("records", records);
        return "patient/records/list";
    }

    @GetMapping("/{id}")
    // public String recordDetail(@PathVariable Long id, Model model) {
    //     MedicalRecordResponse record = patientService.getMedicalRecordDetail(id, DEMO_PATIENT_ID);
    public String recordDetail(@PathVariable Long id,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model) {
        MedicalRecordResponse record = patientService.getMedicalRecordDetail(id, userDetails.getUser().getId());
        model.addAttribute("record", record);
        return "patient/records/detail";
    }
}
