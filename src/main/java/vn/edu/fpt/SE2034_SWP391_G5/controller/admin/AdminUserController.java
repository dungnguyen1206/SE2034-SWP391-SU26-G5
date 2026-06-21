package vn.edu.fpt.SE2034_SWP391_G5.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.UserAccountResponse;
import vn.edu.fpt.SE2034_SWP391_G5.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/account-list")
    public String accountList(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String role,
                              @RequestParam(required = false) List<String> searchFields,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {
        if (keyword != null && keyword.trim().isEmpty()) keyword = null;
        if (role != null && role.trim().isEmpty()) role = null;

        boolean searchFirstName = searchFields == null || searchFields.isEmpty() || searchFields.contains("firstName");
        boolean searchMiddleName = searchFields == null || searchFields.isEmpty() || searchFields.contains("middleName");
        boolean searchLastName = searchFields == null || searchFields.isEmpty() || searchFields.contains("lastName");

        // Page in Spring Data JPA is 0-indexed
        Page<UserAccountResponse> userPage = userService.getAccountList(keyword, role, searchFirstName, searchMiddleName, searchLastName, Math.max(0, page - 1), size);
        
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        
        model.addAttribute("keyword", keyword);
        model.addAttribute("roleFilter", role);
        model.addAttribute("searchFirstName", searchFirstName);
        model.addAttribute("searchMiddleName", searchMiddleName);
        model.addAttribute("searchLastName", searchLastName);
        
        return "admin/users/account-list";
    }

    @PostMapping("/update-role")
    public String updateRoles(@RequestParam("userId") Long id, 
                              @RequestParam(name = "roles", required = false) List<String> roles,
                              RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserRoles(id, roles);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật vai trò thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/account-list";
    }

    @PostMapping("/account-list/{id}/lock")
    public String lockAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id, "LOCKED");
            redirectAttributes.addFlashAttribute("successMessage", "Khóa tài khoản thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/account-list";
    }

    @PostMapping("/account-list/{id}/unlock")
    public String unlockAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id, "ACTIVE");
            redirectAttributes.addFlashAttribute("successMessage", "Mở khóa tài khoản thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/account-list";
    }
}
