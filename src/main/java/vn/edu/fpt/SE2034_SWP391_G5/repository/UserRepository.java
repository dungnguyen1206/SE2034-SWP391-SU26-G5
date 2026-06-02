package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByDepartmentId(Integer departmentId);
}
