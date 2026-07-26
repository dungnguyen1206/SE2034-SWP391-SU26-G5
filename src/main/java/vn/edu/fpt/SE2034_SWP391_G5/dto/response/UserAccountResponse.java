package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.entity.UserRole;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Dữ liệu một dòng trong bảng quản lý tài khoản
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private List<String> roles;
    private String status;
    private LocalDateTime createdAt;

    // Tạo dữ liệu hiển thị từ tài khoản lấy trong database
    public static UserAccountResponse from(User user) {
        // Gom tên các vai trò của tài khoản
        List<String> roles = new ArrayList<>();
        if (user.getUserRoles() != null) {
            for (UserRole userRole : user.getUserRoles()) {
                roles.add(userRole.getRole().getName());
            }
        }

        return UserAccountResponse.builder()
                .id(user.getId())
                .fullName(buildFullName(user))
                .email(user.getEmail())
                .phone(user.getPhone())
                .roles(roles)
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }

    // Ghép họ, tên đệm và tên thành họ tên đầy đủ
    private static String buildFullName(User user) {
        String fullName = user.getLastName() + " "
                + (user.getMiddleName() != null ? user.getMiddleName() + " " : "")
                + user.getFirstName();
        return fullName.trim();
    }
}
