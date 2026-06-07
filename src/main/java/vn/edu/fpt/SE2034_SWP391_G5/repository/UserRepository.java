package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.StaffResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.enums.AppointmentStatus;

import java.util.Optional;

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
            "   u.id," +
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
            " AND (:roleName IS NULL OR r.name = :roleName)" +
            " AND (:keyword IS NULL OR :keyword = ''" +
            "          OR LOWER(CONCAT(u.firstName, ' ', COALESCE(u.middleName, ''), ' ', u.lastName)) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "          OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "          OR u.phone LIKE CONCAT('%', :keyword, '%'))")
    List<StaffResponse> findActiveStaffList(@Param("roleName") String roleName, String keyword);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN u.userRoles ur JOIN ur.role r " +
            "WHERE r.name = 'DOCTOR' " +
            "AND u.department.id = :departmentId " +
            "AND u.doctorStatus = 'ACTIVE'")
    List<User> findActiveDoctorsByDepartmentId(@Param("departmentId") Integer departmentId);


    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByPhone(String phone);
    boolean existsByLicenseNumber(String licenseNumber);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @Query("SELECT u.id, " +
            "CONCAT(CONCAT(CONCAT(u.lastName, ' '), COALESCE(CONCAT(u.middleName, ' '), '')), u.firstName), " +
            "UPPER(CONCAT(SUBSTRING(u.lastName, 1, 1), SUBSTRING(u.firstName, 1, 1))) " +
            "FROM User u " +
            "WHERE u.email = :email")
    List<Object[]> findReceptionistInfoByEmail(@Param("email") String email);
    @Query("select new vn.edu.fpt.SE2034_SWP391_G5.dto.response.StaffResponse (" +
            " u.id, " +
            " concat(u.firstName,' ' , COALESCE(u.middleName,' ') ,' ', u.lastName)," +
            " u.email," +
            " u.phone," +
            " r.name," +
            " u.status," +
            " concat(cb.firstName, ' '  , COALESCE(cb.middleName,' ') ,' ',cb.lastName)," +
            " u.createdAt," +
            " u.updatedAt)" +
            " From User u " +
            "  join  u.userRoles ur" +
            "  join   ur.role r" +
            " left join  u.createdBy cb" +
            " WHERE u.id =:id ")
     StaffResponse selectStaffById(@Param("id") Long id);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN FETCH u.userRoles ur " +
            "JOIN FETCH ur.role r " +
            "LEFT JOIN FETCH u.department d " +
            "LEFT JOIN FETCH u.createdBy cb " +
            "WHERE u.id = :doctorId " +
            "AND r.name = 'DOCTOR'")
    Optional<User> findDoctorStaffDetailById(@Param("doctorId") Long doctorId);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN FETCH u.userRoles ur " +
            "JOIN FETCH ur.role r " +
            "LEFT JOIN FETCH u.createdBy cb " +
            "WHERE u.id = :receptionistId " +
            "AND r.name = 'RECEPTIONIST'")
    Optional<User> findReceptionistStaffDetailById(@Param("receptionistId") Long receptionistId);


    @Query("select count(*) from Appointment a join a.doctor d where d.id=:doctorId And a.status=:appointmentStatus")
    long countDoctorsAppointmentByAppointmentStatus(@Param("appointmentStatus") String  appointmentStatus, @Param("doctorId") Long doctorId);


}
