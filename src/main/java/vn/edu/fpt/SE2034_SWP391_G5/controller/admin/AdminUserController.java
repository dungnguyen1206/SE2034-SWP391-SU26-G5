package vn.edu.fpt.SE2034_SWP391_G5.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UserSearchCriteria;
import vn.edu.fpt.SE2034_SWP391_G5.enums.UserStatus;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.UserAccountResponse;
import vn.edu.fpt.SE2034_SWP391_G5.service.UserService;

import java.util.List;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    // Hiển thị danh sách tài khoản (có lọc, phân trang)
    @GetMapping("/account-list")
    public String accountList(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String role,
                              @RequestParam(required = false) List<String> searchFields,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {
        UserSearchCriteria criteria = UserSearchCriteria.from(keyword, role, searchFields);

        // Spring Data JPA tính trang từ 0
        Page<UserAccountResponse> userPage = userService.getAccountList(criteria, Math.max(0, page - 1), size);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());

        model.addAttribute("keyword", criteria.getKeyword());
        model.addAttribute("roleFilter", criteria.getRoleName());
        model.addAttribute("searchFirstName", criteria.isSearchFirstName());
        model.addAttribute("searchMiddleName", criteria.isSearchMiddleName());
        model.addAttribute("searchLastName", criteria.isSearchLastName());

        return "admin/users/account-list";
    }

    // Cập nhật vai trò cho user
    @PostMapping("/update-role")
    public String updateRoles(@RequestParam("userId") Long id, 
                              @RequestParam(name = "roles", required = false) List<String> roles,
                              RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserRoles(id, roles);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật vai trò thành công!");
        } catch (BadRequestException | ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/account-list";
    }

    // Khóa tài khoản
    @PostMapping("/account-list/{id}/lock")
    public String lockAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id, UserStatus.INACTIVE.name());
            redirectAttributes.addFlashAttribute("successMessage", "Khóa tài khoản thành công!");
        } catch (BadRequestException | ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/account-list";
    }

    // Mở khóa tài khoản
    @PostMapping("/account-list/{id}/unlock")
    public String unlockAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id, UserStatus.ACTIVE.name());
            redirectAttributes.addFlashAttribute("successMessage", "Mở khóa tài khoản thành công!");
        } catch (BadRequestException | ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/account-list";
    }
}
