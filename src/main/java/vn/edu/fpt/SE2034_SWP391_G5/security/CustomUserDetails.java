package vn.edu.fpt.SE2034_SWP391_G5.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getUserRoles() == null) {
            return Collections.emptyList();
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (vn.edu.fpt.SE2034_SWP391_G5.entity.UserRole userRole : user.getUserRoles()) {
            String roleName = userRole.getRole().getName();
            // Đảm bảo luôn có prefix ROLE_ để Spring Security nhận diện đúng
            if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName;
            }
            authorities.add(new SimpleGrantedAuthority(roleName));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    // Return username for Spring Security authentication
    public String getUsername() {
        return user.getUsername(); // Đăng nhập sử dụng Username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return "ACTIVE".equalsIgnoreCase(user.getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
