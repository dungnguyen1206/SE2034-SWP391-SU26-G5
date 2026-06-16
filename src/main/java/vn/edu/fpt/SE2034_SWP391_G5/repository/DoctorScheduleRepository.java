package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.DoctorSchedule;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Room;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

    @Query("SELECT ds from DoctorSchedule ds Join fetch ds.doctor d join fetch d.department dpt join fetch dpt.rooms r where ds.workDate= :date")
    public List<DoctorSchedule> findByDate(@Param("date") LocalDate date);




    // Lấy lịch làm việc của bác sĩ từ ngày hôm nay trở đi
    @Query("SELECT ds FROM DoctorSchedule ds " +
           "WHERE ds.doctor.id = :doctorId " +
           "AND ds.workDate >= :fromDate " +
           "AND ds.status = 'ACTIVE' " +
           "ORDER BY ds.workDate ASC, ds.shift ASC")
    List<DoctorSchedule> findAvailableSchedulesByDoctorId(
            @Param("doctorId") Long doctorId,
            @Param("fromDate") LocalDate fromDate);

    List<DoctorSchedule> findByDoctorIdAndWorkDateBetweenAndStatusOrderByWorkDateAscShiftAsc(
            Long doctorId, LocalDate startDate, LocalDate endDate, String status);


    @Query("SELECT u from User u left join u.department d where d.id=:departmentId")
     List<User> findDoctorByDepartmentId(@Param("departmentId") Integer departmentId);

    @Query("select r from Room r left  join r.department d where d.id=:departmentId")
     List<Room> findRoomsByDepartmentId(@Param("departmentId") Integer departmentId);

    @Query("SELECT COUNT(sd) > 0 FROM DoctorSchedule sd " +
            "WHERE sd.doctor = :doctor " +
            "AND sd.workDate = :workDate " +
            "AND sd.shift IN :shifts")
    boolean existsByDoctorAndWorkDateAndShiftIn(
            @Param("doctor") User doctor,
            @Param("workDate") LocalDate workDate,
            @Param("shifts") List<String> shifts
    );


    @Query("Select count(ds) >0   from DoctorSchedule ds " +
            " where ds.room=:room" +
            " and ds.workDate=:workDate" +
            " and ds.shift in :shifts")
    boolean existsByRoomAndWorkDateAndShift(
      @Param("room") Room room,
      @Param("workDate") LocalDate workDate,
      @Param("shifts")  List<String> shifts
    );
}
