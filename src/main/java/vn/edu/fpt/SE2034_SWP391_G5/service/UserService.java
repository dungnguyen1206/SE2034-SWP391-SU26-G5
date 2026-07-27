package vn.edu.fpt.SE2034_SWP391_G5.service;

import org.springframework.data.domain.Page;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UserSearchCriteria;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.UserAccountResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

import java.util.List;

public interface UserService {
    // Lấy danh sách tài khoản có lọc và phân trang
    Page<UserAccountResponse> getAccountList(UserSearchCriteria criteria, int page, int size);
    // Cập nhật vai trò cho user
    void updateUserRoles(Long userId, List<String> roleNames);
    // Khóa hoặc mở khóa tài khoản
    void toggleUserStatus(Long userId, String status);

    User getUserById(Long userId);

    // Lấy danh sách bác sĩ để chọn làm tác giả bài viết
    List<User> getDoctors();
}
