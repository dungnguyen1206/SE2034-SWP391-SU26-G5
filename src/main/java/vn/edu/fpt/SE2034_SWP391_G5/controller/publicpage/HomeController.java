package vn.edu.fpt.SE2034_SWP391_G5.controller.publicpage;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.SE2034_SWP391_G5.service.DepartmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.DoctorService;
import vn.edu.fpt.SE2034_SWP391_G5.service.MedicalRecordService;
import vn.edu.fpt.SE2034_SWP391_G5.service.MedicalServiceService;

@Controller
@RequestMapping("/home")  // hoặc "/" nếu muốn trang chủ
@RequiredArgsConstructor
public class HomeController {

    private final DepartmentService departmentService;
    private final MedicalServiceService medicalRecordService;
    private final DoctorService doctorService;

    @GetMapping
    public String getHomePage(Model model) {
        model.addAttribute("departments", departmentService.getAllActiveDepartments());
        model.addAttribute("doctors", doctorService.findByDoctorStatus("ACTIVE"));
        model.addAttribute("medicalRecords", medicalRecordService.getMedicalServicelistByDepartment(null));
        return "public/home"; // Trả về tên view (file HTML) cho trang chủ
    }
}
