package vn.edu.fpt.SE2034_SWP391_G5.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.repository.UserRepository;

@Component
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));

        // Cần truy xuất trước các Role vì fetch type trong entity UserRole thường là Lazy
        if (user.getUserRoles() != null) {
            // Cũ: user.getUserRoles().size(); → chỉ load UserRole, chưa load Role bên trong → LazyInitializationException
            user.getUserRoles().size();
            // Fix: force-load Role của từng UserRole trong cùng transaction
            user.getUserRoles().forEach(ur -> {
                if (ur.getRole() != null) {
                    ur.getRole().getName(); // trigger load Role proxy
                }
            });
        }

        return new CustomUserDetails(user);
    }
}