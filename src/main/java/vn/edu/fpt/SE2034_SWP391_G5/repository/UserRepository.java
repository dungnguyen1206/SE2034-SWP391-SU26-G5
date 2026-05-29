package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("Select u from User u JOIN u.userRoles r where r.role.name =:roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("Select u from User u WHere u.doctorStatus =:doctorStatus")
    List<User> findByDoctorStatus(@Param("doctorStatus") String doctorStatus);
}
