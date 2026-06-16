package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.DoctorSchedule;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

    // Trước: join fetch dpt.rooms r → join Collection gây lỗi, và rooms của department khác với room của schedule
    // Fix: chỉ join doctor, department và room của schedule (LEFT JOIN để tránh mất dữ liệu khi room null)
    @Query("SELECT DISTINCT ds FROM DoctorSchedule ds " +
           "JOIN FETCH ds.doctor d " +
           "JOIN FETCH d.department dpt " +
           "LEFT JOIN FETCH ds.room r " +
           "WHERE ds.workDate = :date")
    List<DoctorSchedule> findByDate(@Param("date") LocalDate date);




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
}
