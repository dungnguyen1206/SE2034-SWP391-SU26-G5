package vn.edu.fpt.SE2034_SWP391_G5.controller.patient;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.DepartmentService;

@Controller
@RequestMapping("/patient/departments")
@RequiredArgsConstructor
public class PatientDepartmentController {

    private final DepartmentService departmentService;
    private final UserRepository userRepository;

    @GetMapping
    public String listDepartments(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Load thông tin patient để hiển thị trong sidebar
        User patient = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        model.addAttribute("profile", patient);
        model.addAttribute("departments", departmentService.getAllActiveDepartments());
        
        return "patient/departments/list";
    }

    @GetMapping("/{id}")
    public String viewDepartmentDetail(@PathVariable Integer id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Load thông tin patient để hiển thị trong sidebar
        User patient = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        model.addAttribute("profile", patient);
        model.addAttribute("department", departmentService.getDepartmentById(id));
        
        return "patient/departments/detail";
    }
}
