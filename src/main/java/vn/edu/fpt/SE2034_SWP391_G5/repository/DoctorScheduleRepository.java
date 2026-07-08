package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.DoctorSchedule;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Room;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

        @Query(value = "SELECT ds FROM DoctorSchedule ds JOIN FETCH ds.doctor d JOIN FETCH d.department dpt WHERE ds.workDate = :date")
        public Page<DoctorSchedule> findByDate(@Param("date") LocalDate date, Pageable pageable);

        @Query("SELECT ds FROM DoctorSchedule ds " +
                        "LEFT JOIN FETCH ds.doctor d " +
                        "LEFT JOIN FETCH d.department " +
                        "LEFT JOIN FETCH ds.room r " +
                        "WHERE ds.workDate = :date AND ds.status = 'ACTIVE'")
        List<DoctorSchedule> findActiveSchedulesByDate(@Param("date") LocalDate date);

        // Lấy lịch làm việc của bác sĩ từ ngày hôm nay trở đi
        // Chỉ hiển thị lịch thuộc tuần đã FINALIZED — bệnh nhân mới được đặt
        @Query("SELECT ds FROM DoctorSchedule ds " +
                        "JOIN ds.weekSchedule ws " +
                        "WHERE ds.doctor.id = :doctorId " +
                        "AND ds.workDate >= :fromDate " +
                        "AND ds.status = 'ACTIVE' " +
                        "AND ws.status = 'FINALIZED' " +
                        "ORDER BY ds.workDate ASC, ds.shift ASC")
        List<DoctorSchedule> findAvailableSchedulesByDoctorId(
                        @Param("doctorId") Long doctorId,
                        @Param("fromDate") LocalDate fromDate);

        //LinhNH
        List<DoctorSchedule> findByDoctorIdAndWorkDateBetweenAndStatusOrderByWorkDateAscShiftAsc(
                        Long doctorId, LocalDate startDate, LocalDate endDate, String status);

        @Query("SELECT u from User u left join u.department d where d.id=:departmentId")
        List<User> findDoctorByDepartmentId(@Param("departmentId") Integer departmentId);

        @Query("select r from Room r left  join r.department d where d.id=:departmentId")
        List<Room> findRoomsByDepartmentId(@Param("departmentId") Integer departmentId);

        @Query("SELECT COUNT(sd) > 0 FROM DoctorSchedule sd " +
                        "WHERE sd.doctor = :doctor " +
                        "AND sd.workDate = :workDate ")
        boolean existsByDoctorAndWorkDate(
                        @Param("doctor") User doctor,
                        @Param("workDate") LocalDate workDate);

        @Query("Select count(ds) >0   from DoctorSchedule ds " +
                        " where ds.room=:room" +
                        " and ds.workDate=:workDate" +
                        " and ds.shift in :shifts")
        boolean existsByRoomAndWorkDateAndShift(
                        @Param("room") Room room,
                        @Param("workDate") LocalDate workDate,
                        @Param("shifts") List<String> shifts);
        //this function for update Schedule
        @Query("Select count(ds) > 0 from DoctorSchedule ds " +
                " where ds.room = :room" +
                " and ds.workDate = :workDate" +
                " and ds.shift in :shifts" +
                " and ds.id <> :excludeId")
        boolean existsByRoomAndWorkDateAndShiftAndIdNot(
                @Param("room") Room room,
                @Param("workDate") LocalDate workDate,
                @Param("shifts") List<String> shifts,
                @Param("excludeId") Long excludeId);

        @Query("Select distinct ds from DoctorSchedule ds " +
                        " left join fetch ds.doctor d " +
                        " left join ds.weekSchedule ws " +
                        " left join fetch ds.room r" +
                        " left join fetch ds.timeSlots ts" +
                        " where d.id IN :doctorId and ws.id=:weekScheduleId")
        List<DoctorSchedule> getAllDoctorScheduleByWeekSchedule(@Param("doctorId") List<Long> doctorIds,
                        @Param("weekScheduleId") Long weekScheduleId);

        @Query("SELECT DISTINCT ds.doctor FROM DoctorSchedule ds WHERE ds.weekSchedule.id = :weekScheduleId")
        List<User> findDoctorsByWeekScheduleId(@Param("weekScheduleId") Long weekScheduleId);


    @Query("SELECT distinct u FROM User u " +
            " LEFT JOIN u.department d " +
            " LEFT JOIN u.userRoles ur" +
            " WHERE ur.role.name=:role" +
            " AND(:departmentId is null or d.id=:departmentId)" +
            " AND(:doctorName is null or (" +
            " lower(concat( u.firstName,' ',COALESCE(u.middleName,''),' ', u.lastName)) Like lower(concat('%',:doctorName,'%') ) ) )" +
            " AND(:shift is null or exists (" +
            "       SELECT 1 from DoctorSchedule ds" +
            "       WHERE  ds.doctor.id=u.id " +
            "       AND ds.weekSchedule.id=:weekScheduleId" +
            "       AND (:shift = 'ALL' " +
            "            OR ds.shift=:shift" +
            "            OR (:shift IN ('MORNING','AFTERNOON')" +
            "            AND ds.shift = 'FULL_DAY' ) )))")
    Page<User> getDoctorScheduleByFilter(@Param("role") String role,
                                                   @Param("departmentId") Integer departmentId,
                                                   @Param("doctorName") String doctorName,
                                                   @Param("shift") String shift,
                                                   @Param("weekScheduleId")  Long weekScheduleId,
                                                   Pageable pageable);

}
