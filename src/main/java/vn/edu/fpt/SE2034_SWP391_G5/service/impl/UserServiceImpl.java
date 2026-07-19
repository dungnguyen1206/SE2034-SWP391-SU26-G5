package vn.edu.fpt.SE2034_SWP391_G5.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.UserAccountResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Role;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.entity.UserRole;
import vn.edu.fpt.SE2034_SWP391_G5.entity.UserRoleId;
import vn.edu.fpt.SE2034_SWP391_G5.repository.RoleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRoleRepository;
import vn.edu.fpt.SE2034_SWP391_G5.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

/**
 * Service implementation for managing users and roles.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    /**
     * Retrieves a paginated list of users based on filter criteria.
     * Maps user entities to user account responses for admin display.
     */
    @Override
    public Page<UserAccountResponse> getAccountList(String keyword, String roleName, boolean searchFirstName, boolean searchMiddleName, boolean searchLastName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAllUsersWithFilters(keyword, roleName, searchFirstName, searchMiddleName, searchLastName, pageable);
        return userPage.map(new java.util.function.Function<User, UserAccountResponse>() {
            @Override
            public UserAccountResponse apply(User user) {
                List<String> roles = new ArrayList<>();
                if (user.getUserRoles() != null) {
                    for (UserRole ur : user.getUserRoles()) {
                        roles.add(ur.getRole().getName());
                    }
                }
                
                String fullName = user.getLastName() + " " + 
                                  (user.getMiddleName() != null ? user.getMiddleName() + " " : "") + 
                                  user.getFirstName();

                return UserAccountResponse.builder()
                        .id(user.getId())
                        .fullName(fullName.trim())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .roles(roles)
                        .status(user.getStatus())
                        .createdAt(user.getCreatedAt())
                        .build();
            }
        });
    }

    /**
     * Updates roles for a specific user.
     * Completely replaces the user's existing roles with the provided role names.
     */
    @Override
    @Transactional
    public void updateUserRoles(Long userId, List<String> roleNames) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException("User not found");
        }
        
        userRoleRepository.deleteByUserId(userId);
        
        if (roleNames != null && !roleNames.isEmpty()) {
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName).orElse(null);
                if (role == null) {
                    throw new vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException("Role not found: " + roleName);
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
    }

    /**
     * Toggles a user's status between ACTIVE and INACTIVE.
     * Prevents locking ADMIN accounts.
     */
    @Override
    public void toggleUserStatus(Long userId, String status) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new vn.edu.fpt.SE2034_SWP391_G5.exception.ResourceNotFoundException("User not found");
        }
        
        if ("INACTIVE".equals(status)) {
            boolean isAdmin = false;
            if (user.getUserRoles() != null) {
                for (UserRole ur : user.getUserRoles()) {
                    if ("ADMIN".equals(ur.getRole().getName())) {
                        isAdmin = true;
                        break;
                    }
                }
            }
            if (isAdmin) {
                throw new vn.edu.fpt.SE2034_SWP391_G5.exception.BadRequestException("Không được phép khóa tài khoản quản trị viên (ADMIN).");
            }
        }
        
        user.setStatus(status);
        userRepository.save(user);
    }
}
