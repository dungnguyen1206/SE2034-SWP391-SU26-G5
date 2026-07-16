package vn.edu.fpt.SE2034_SWP391_G5.controller.publicpage;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Article;
import vn.edu.fpt.SE2034_SWP391_G5.service.ArticleService;
import vn.edu.fpt.SE2034_SWP391_G5.service.DepartmentService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final DepartmentService departmentService;
    private final ArticleService articleService;

    @GetMapping({"/", "/home"})
    public String getHomePage(Model model, Authentication authentication) {
        // Nếu user đã login, kiểm tra role
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                String role = authority.getAuthority();
                
                // Staff roles nên redirect về dashboard của họ
                switch (role) {
                    case "ROLE_ADMIN":
                        return "redirect:/admin/account-list";
                    case "ROLE_MANAGER":
                        return "redirect:/manager/dashboard";
                    case "ROLE_DOCTOR":
                        return "redirect:/doctor/dashboard";
                    case "ROLE_RECEPTIONIST":
                        return "redirect:/receptionist/dashboard";
                    case "ROLE_PATIENT":
                        // Patient được phép vào home
                        model.addAttribute("isPatient", true);
                        break;
                }
            }
        }
        
        model.addAttribute("departments", departmentService.getAllActiveDepartments());
        
        // Load articles đã publish (lấy 3 bài mới nhất)
        try {
            List<Article> articles = articleService.getArticlesByFilters(null, null, "PUBLISHED");
            // Lấy tối đa 3 bài mới nhất
            if (articles.size() > 3) {
                articles = articles.subList(0, 3);
            }
            model.addAttribute("articles", articles);
        } catch (Exception e) {
            // Nếu có lỗi (ví dụ database không kết nối), truyền list rỗng
            model.addAttribute("articles", List.of());
        }
        
        return "public/home";
    }
}
