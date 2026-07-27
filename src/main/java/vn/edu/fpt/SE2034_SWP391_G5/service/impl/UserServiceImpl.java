package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.dto.request.UserSearchCriteria;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.UserAccountResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Role;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.entity.UserRole;
import vn.edu.fpt.SE2034_SWP391_G5.entity.UserRoleId;
import vn.edu.fpt.SE2034_SWP391_G5.enums.UserStatus;
import vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException;
import vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException;
import vn.edu.fpt.SE2034_SWP391_G5.repository.RoleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRoleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

// Xử lý nghiệp vụ quản lý tài khoản và vai trò
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    // Lấy danh sách tài khoản có lọc và phân trang
    @Override
    public Page<UserAccountResponse> getAccountList(UserSearchCriteria criteria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAllUsersWithFilters(
                criteria.getKeyword(),
                criteria.getRoleName(),
                criteria.isSearchFirstName(),
                criteria.isSearchMiddleName(),
                criteria.isSearchLastName(),
                pageable);

        // Đổi từng tài khoản sang dữ liệu hiển thị
        List<UserAccountResponse> accounts = new ArrayList<>();
        for (User user : userPage.getContent()) {
            accounts.add(toAccountResponse(user));
        }

        return new PageImpl<>(accounts, pageable, userPage.getTotalElements());
    }

    // Cập nhật vai trò cho user
    @Override
    @Transactional
    public void updateUserRoles(Long userId, List<String> roleNames) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        // Mỗi tài khoản phải giữ ít nhất một vai trò
        if (roleNames == null || roleNames.isEmpty()) {
            throw new BadRequestException("Tài khoản phải có ít nhất một vai trò.");
        }

        userRoleRepository.deleteByUserId(userId);

        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName).orElse(null);
            if (role == null) {
                throw new ResourceNotFoundException("Role not found: " + roleName);
            }

            UserRole userRole = new UserRole();
            UserRoleId id = new UserRoleId(userId, role.getId());
            userRole.setId(id);
            userRole.setUser(user);
            userRole.setRole(role);
            userRole.setAssignedAt(LocalDateTime.now());

            userRoleRepository.save(userRole);
        }
    }

    // Khóa/mở khóa tài khoản (chặn khóa ADMIN)
    @Override
    public void toggleUserStatus(Long userId, String status) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        if (UserStatus.INACTIVE.name().equals(status)) {
            // Không cho khóa tài khoản có vai trò ADMIN
            boolean isAdmin = false;
            if (user.getUserRoles() != null) {
                for (UserRole userRole : user.getUserRoles()) {
                    if ("ADMIN".equals(userRole.getRole().getName())) {
                        isAdmin = true;
                        break;
                    }
                }
            }
            if (isAdmin) {
                throw new BadRequestException("Không được phép khóa tài khoản quản trị viên (ADMIN).");
            }
        }

        user.setStatus(status);
        userRepository.save(user);
    }

    // Lấy danh sách bác sĩ để chọn làm tác giả bài viết
    @Override
    public List<User> getDoctors() {
        return userRepository.findByRoleName("DOCTOR");
    }

    // Đổi tài khoản trong database sang dữ liệu hiển thị
    private UserAccountResponse toAccountResponse(User user) {
        // Gom mã vai trò và tên vai trò tiếng Việt (lấy từ cột description của bảng roles)
        List<String> roles = new ArrayList<>();
        List<String> roleLabels = new ArrayList<>();
        boolean isAdmin = false;

        if (user.getUserRoles() != null) {
            for (UserRole userRole : user.getUserRoles()) {
                Role role = userRole.getRole();
                roles.add(role.getName());
                roleLabels.add(role.getDescription() != null ? role.getDescription() : role.getName());

                if ("ADMIN".equals(role.getName())) {
                    isAdmin = true;
                }
            }
        }

        boolean active = UserStatus.ACTIVE.name().equals(user.getStatus());

        UserAccountResponse response = new UserAccountResponse();
        response.setId(user.getId());
        response.setFullName(buildFullName(user));
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setCreatedAt(user.getCreatedAt());
        response.setRoles(roles);
        response.setRolesText(String.join(", ", roleLabels));
        response.setStatus(user.getStatus());
        response.setActive(active);
        response.setStatusText(active ? "Hoạt động" : "Tạm khóa");

        // Không được phép khóa tài khoản quản trị viên
        response.setCanBeLocked(active && !isAdmin);
        response.setCanBeUnlocked(!active && !isAdmin);

        return response;
    }

    // Ghép họ, tên đệm và tên thành họ tên đầy đủ
    private String buildFullName(User user) {
        String fullName = user.getLastName() + " "
                + (user.getMiddleName() != null ? user.getMiddleName() + " " : "")
                + user.getFirstName();
        return fullName.trim();
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
