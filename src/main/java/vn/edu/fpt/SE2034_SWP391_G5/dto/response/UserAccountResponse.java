package vn.edu.fpt.SE2034_SWP391_G5.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

// Dữ liệu một dòng trong bảng quản lý tài khoản
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private LocalDateTime createdAt;

    // Mã vai trò dạng thô (PATIENT, DOCTOR...), dùng để tick sẵn trong hộp thoại đổi vai trò
    private List<String> roles;

    // Tên vai trò tiếng Việt đã ghép sẵn, ví dụ "Bác sĩ, Quản lý bệnh viện"
    private String rolesText;

    // Trạng thái dạng thô (ACTIVE / INACTIVE)
    private String status;

    // Tài khoản đang hoạt động hay không
    private boolean active;

    // Trạng thái hiển thị cho người dùng đọc
    private String statusText;

    // Có được phép khóa tài khoản này không
    private boolean canBeLocked;

    // Có được phép mở khóa tài khoản này không
    private boolean canBeUnlocked;
}
