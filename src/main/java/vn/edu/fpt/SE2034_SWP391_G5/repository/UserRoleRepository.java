package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.UserRole;
import vn.edu.fpt.SE2034_SWP391_G5.entity.UserRoleId;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Truy vấn dữ liệu vai trò đã gán cho từng tài khoản
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

    // Lấy toàn bộ vai trò của một tài khoản
    List<UserRole> findByUserId(Long userId);

    // Xóa hết vai trò cũ của tài khoản trước khi gán vai trò mới
    @Transactional
    void deleteByUserId(Long userId);
}
