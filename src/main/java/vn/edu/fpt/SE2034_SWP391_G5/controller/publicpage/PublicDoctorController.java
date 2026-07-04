package vn.edu.fpt.SE2034_SWP391_G5.controller.publicpage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.DoctorResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Department;
import vn.edu.fpt.SE2034_SWP391_G5.service.DepartmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.DoctorService;

import java.util.List;

@Controller
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class PublicDoctorController {

    private final DoctorService doctorService;
    private final DepartmentService departmentService;

    @GetMapping
    public String listDoctors(
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) String search,
            Model model) {

        List<Department> departments = departmentService.getAllActiveDepartments();
        
        // Fetch all active doctors (doctorStatus = 'ACTIVE')
        List<DoctorResponse> doctors = doctorService.findByDoctorStatus("ACTIVE").stream()
                .map(doctorService::toResponse)
                .toList();

        // Apply department filter
        if (departmentId != null) {
            doctors = doctors.stream()
                    .filter(d -> departmentId.equals(d.getDepartmentId()))
                    .toList();
            model.addAttribute("selectedDepartmentId", departmentId);
        } else {
            model.addAttribute("selectedDepartmentId", null);
        }

        // Apply name search filter
        if (search != null && !search.trim().isEmpty()) {
            String keyword = search.trim().toLowerCase();
            doctors = doctors.stream()
                    .filter(d -> d.getFullName() != null && d.getFullName().toLowerCase().contains(keyword))
                    .toList();
            model.addAttribute("searchQuery", search);
        } else {
            model.addAttribute("searchQuery", "");
        }

        model.addAttribute("doctors", doctors);
        model.addAttribute("departments", departments);

        return "public/doctors/list";
    }

    @GetMapping("/{id}")
    public String doctorDetail(@PathVariable Long id, Model model) {
        DoctorResponse doctor = doctorService.getDoctorById(id);
        model.addAttribute("doctor", doctor);
        return "public/doctors/detail";
    }
}
