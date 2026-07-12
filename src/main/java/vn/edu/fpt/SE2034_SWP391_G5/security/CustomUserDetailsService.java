package vn.edu.fpt.SE2034_SWP391_G5.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    // Load user by username for authentication
    public UserDetails loadUserByUsername(String usernameOrPhone) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(usernameOrPhone).orElse(null);
        if (user == null) {
            user = userRepository.findByPhone(usernameOrPhone).orElse(null);
            if (user == null) {
                throw new UsernameNotFoundException("Không tìm thấy người dùng với tên đăng nhập hoặc số điện thoại: " + usernameOrPhone);
            }
        }

        if (user.getUserRoles() != null) {
            for (vn.edu.fpt.SE2034_SWP391_G5.entity.UserRole userRole : user.getUserRoles()) {
                if (userRole.getRole() != null) {
                    userRole.getRole().getName();
                }
            }
        }
        
        return new CustomUserDetails(user);
    }
}
