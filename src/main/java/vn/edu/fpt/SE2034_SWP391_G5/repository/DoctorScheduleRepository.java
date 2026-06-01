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

    // Lấy lịch làm việc của bác sĩ từ ngày hôm nay trở đi
    @Query("SELECT ds FROM DoctorSchedule ds " +
           "WHERE ds.doctor.id = :doctorId " +
           "AND ds.workDate >= :fromDate " +
           "AND ds.status = 'ACTIVE' " +
           "ORDER BY ds.workDate ASC, ds.shift ASC")
    List<DoctorSchedule> findAvailableSchedulesByDoctorId(
            @Param("doctorId") Long doctorId,
            @Param("fromDate") LocalDate fromDate);
}
