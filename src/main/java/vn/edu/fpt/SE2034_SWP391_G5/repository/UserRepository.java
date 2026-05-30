package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.StaffResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("Select u from User u JOIN u.userRoles r where r.role.name =:roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("Select u from User u JOIN u.userRoles r where r.role.name = :roleName and u.status = :status")
    List<User> countByRoleNameAndStatus(@Param("roleName") String roleName, @Param("status") String status);

    @Query("Select u from User u WHere u.doctorStatus =:doctorStatus")
    List<User> findByDoctorStatus(@Param("doctorStatus") String doctorStatus);

    @Query("select new vn.edu.fpt.SE2034_SWP391_G5.dto.response.StaffResponse(" +
            "  CONCAT(u.firstName, ' ', COALESCE(u.middleName, ''), ' ', u.lastName)," +
            "   u.email," +
            "   u.phone," +
            "   r.name," +
            "   u.status," +
            "  CONCAT(cb.firstName, ' ', COALESCE(cb.middleName, ''), ' ', cb.lastName)," +
            "   u.createdAt," +
            "   u.updatedAt)" +
            " FROM User u" +
            " JOIN u.userRoles ur" +
            " JOIN ur.role r" +
            " LEFT JOIN u.createdBy cb" +
            " WHERE r.name IN ('DOCTOR', 'RECEPTIONIST')" +
            " AND u.status = 'ACTIVE'" +
            " AND (:roleName IS NULL OR r.name = :roleName)")
    List<StaffResponse> findActiveStaffList(@Param("roleName") String roleName);


}
