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

import java.util.ArrayList;
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
        UserSearchCriteria criteria = buildSearchCriteria(keyword, role, searchFields);

        // Spring Data JPA tính trang từ 0
        Page<UserAccountResponse> userPage = userService.getAccountList(criteria, Math.max(0, page - 1), size);

        int totalPages = userPage.getTotalPages();

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", userPage.getTotalElements());

        // Dữ liệu cho thanh phân trang, tính sẵn để template chỉ việc hiển thị
        model.addAttribute("middlePages", buildMiddlePages(page, totalPages));
        model.addAttribute("showLeftDots", page > 3);
        model.addAttribute("showRightDots", page < totalPages - 2);

        model.addAttribute("keyword", criteria.getKeyword());
        model.addAttribute("roleFilter", criteria.getRoleName());
        model.addAttribute("searchFirstName", criteria.isSearchFirstName());
        model.addAttribute("searchMiddleName", criteria.isSearchMiddleName());
        model.addAttribute("searchLastName", criteria.isSearchLastName());

        // Các liên kết lọc vai trò và chuyển trang phải mang theo trường tìm kiếm đang chọn
        model.addAttribute("searchFields", buildSearchFieldList(criteria));

        return "admin/account-list";
    }

    // Đổi ba cờ tìm kiếm thành danh sách tên trường để gắn vào đường dẫn
    private List<String> buildSearchFieldList(UserSearchCriteria criteria) {
        List<String> fields = new ArrayList<>();

        if (criteria.isSearchLastName()) {
            fields.add("lastName");
        }
        if (criteria.isSearchMiddleName()) {
            fields.add("middleName");
        }
        if (criteria.isSearchFirstName()) {
            fields.add("firstName");
        }
        return fields;
    }

    // Các số trang hiện ở giữa thanh phân trang.
    // Trang đầu và trang cuối luôn hiện riêng nên không tính vào đây.
    private List<Integer> buildMiddlePages(int currentPage, int totalPages) {
        List<Integer> pages = new ArrayList<>();

        // Lấy 2 trang trước và 2 trang sau trang hiện tại, không vượt ra ngoài khoảng giữa
        int from = Math.max(currentPage - 2, 2);
        int to = Math.min(currentPage + 2, totalPages - 1);

        for (int page = from; page <= to; page++) {
            pages.add(page);
        }
        return pages;
    }

    // Dựng điều kiện tìm kiếm từ tham số trên URL
    private UserSearchCriteria buildSearchCriteria(String keyword, String roleName, List<String> searchFields) {
        UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setKeyword(normalize(keyword));
        criteria.setRoleName(normalize(roleName));

        // Không tick trường nào thì mặc định tìm trong cả họ, tên đệm và tên
        boolean noneSelected = searchFields == null || searchFields.isEmpty();
        criteria.setSearchFirstName(noneSelected || searchFields.contains("firstName"));
        criteria.setSearchMiddleName(noneSelected || searchFields.contains("middleName"));
        criteria.setSearchLastName(noneSelected || searchFields.contains("lastName"));

        return criteria;
    }

    // Chuỗi rỗng coi như người dùng không nhập gì
    private String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value;
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
