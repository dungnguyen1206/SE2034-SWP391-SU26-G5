package vn.edu.fpt.SE2034_SWP391_G5.service;

import org.springframework.data.domain.Page;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.UserAccountResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

import java.util.List;

public interface UserService {
    Page<UserAccountResponse> getAccountList(String keyword, String roleName, boolean searchFirstName, boolean searchMiddleName, boolean searchLastName, int page, int size);
    void updateUserRoles(Long userId, List<String> roleNames);
    void toggleUserStatus(Long userId, String status);
    User getUserById(Long userId);
}
