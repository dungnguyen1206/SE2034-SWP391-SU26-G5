package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import java.util.Optional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT u FROM User u " +
           "JOIN u.userRoles ur JOIN ur.role r " +
           "WHERE r.name = 'DOCTOR' " +
           "AND u.department.id = :departmentId " +
           "AND u.doctorStatus = 'ACTIVE'")
    List<User> findActiveDoctorsByDepartmentId(@Param("departmentId") Integer departmentId);
}
