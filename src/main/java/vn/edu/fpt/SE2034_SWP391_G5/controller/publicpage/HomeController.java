package vn.edu.fpt.SE2034_SWP391_G5.controller.publicpage;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.SE2034_SWP391_G5.service.DepartmentService;
import vn.edu.fpt.SE2034_SWP391_G5.service.DoctorService;
import vn.edu.fpt.SE2034_SWP391_G5.service.ArticleService;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Article;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final DepartmentService departmentService;
    private final DoctorService doctorService;
    private final ArticleService articleService;

    @GetMapping({"/", "/home"})
    public String getHomePage(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                String role = authority.getAuthority();
                if ("ROLE_DOCTOR".equals(role)) return "redirect:/doctor/dashboard";
                if ("ROLE_RECEPTIONIST".equals(role)) return "redirect:/receptionist/dashboard";
                if ("ROLE_MANAGER".equals(role)) return "redirect:/manager/dashboard";
                if ("ROLE_ADMIN".equals(role)) return "redirect:/admin/account-list";
            }
        }
        
        // Pass counts for Hero stats
        model.addAttribute("totalDepartments", departmentService.getAllActiveDepartments().size());
        model.addAttribute("totalDoctors", doctorService.findByRoleNameAndStatus("DOCTOR", "ACTIVE").size());
        
        // Fetch published articles for the latest news section
        List<Article> articles = articleService.getArticlesByFilters(null, null, "PUBLISHED");
        model.addAttribute("totalArticles", articles.size());
        
        // Just show the latest 3 articles on home page
        if (articles.size() > 3) {
            articles = articles.subList(0, 3);
        }
        model.addAttribute("articles", articles);
        
        return "public/home";
    }

    @GetMapping("/departments")
    public String listDepartments(Model model) {
        model.addAttribute("departments", departmentService.getAllActiveDepartments());
        return "public/departments/list";
    }

    @GetMapping("/departments/{id}")
    public String detailDepartment(@org.springframework.web.bind.annotation.PathVariable Integer id, Model model) {
        vn.edu.fpt.SE2034_SWP391_G5.entity.Department department = departmentService.getDepartmentById(id);
        model.addAttribute("department", department);
        return "public/departments/detail";
    }
}
