package vn.edu.fpt.SE2034_SWP391_G5.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    // Load user by username for authentication
    public UserDetails loadUserByUsername(String usernameOrPhone) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(usernameOrPhone)
                .orElseGet(() -> userRepository.findByPhone(usernameOrPhone)
                        .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với tên đăng nhập hoặc số điện thoại: " + usernameOrPhone)));
        
        // Cần truy xuất trước các Role vì fetch type trong entity UserRole thường là Lazy
        if (user.getUserRoles() != null) {
            user.getUserRoles().forEach(userRole -> {
                if (userRole.getRole() != null) {
                    userRole.getRole().getName(); // Kích hoạt (initialize) proxy của Role
                }
            });
        }
        
        return new CustomUserDetails(user);
    }
}
