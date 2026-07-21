package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.TimeSlot;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
//    @Query("select t from TimeSlot t Join fetch t.schedule s" +
//            " join fetch s.doctor d where s.workDate= :date")
//        List<TimeSlot> findTodayDoctor(@Param("date") LocalDate date);

    List<TimeSlot> findByScheduleIdOrderByStartTimeAsc(Long scheduleId);

    // Fetch slot cùng với schedule (để tránh LazyInitializationException khi đặt lịch)
    @Query("SELECT t FROM TimeSlot t JOIN FETCH t.schedule s WHERE t.id = :id")
    Optional<TimeSlot> findByIdWithSchedule(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM TimeSlot t where t.schedule.id=:doctorScheduleId")
    int deleteTimeSlotByDoctorScheduleId(@Param("doctorScheduleId") Long doctorScheduleId);

    //============================ Queue ===========================================
    @Query("SELECT ts FROM TimeSlot ts " +
            "JOIN FETCH ts.schedule sch " +
            "JOIN FETCH sch.room r " +
            "WHERE sch.workDate = :workDate " +
            "AND r.id = :roomId " +
            "ORDER BY ts.startTime ASC")
    List<TimeSlot> findByRoomIdAndWorkDateOrderByStartTimeAsc(
            @Param("roomId") Integer roomId,
            @Param("workDate") LocalDate workDate
    );

    // ======================== WALK-IN BOOKING RECEPTIONIST ========================
    @Query("SELECT ts FROM TimeSlot ts " +
            "JOIN FETCH ts.schedule sch " +
            "JOIN FETCH sch.doctor d " +
            "WHERE d.department.id = :departmentId " +
            "AND sch.workDate = :workDate " +
            "ORDER BY ts.startTime ASC")
    List<TimeSlot> findSlotsByDepartmentAndDate(
            @Param("departmentId") Integer departmentId,
            @Param("workDate") LocalDate workDate
    );
    // ======================== END WALK-IN BOOKING RECEPTIONIST ========================

    @Query("SELECT COUNT(ts) FROM TimeSlot ts " +
           "JOIN ts.schedule ds " +
           "WHERE ds.doctor.id = :doctorId " +
           "AND ds.workDate BETWEEN :startDate AND :endDate " +
           "AND ds.status = 'ACTIVE' " +
           "AND ds.weekSchedule.status <> 'DRAFT'")
    long countSlotsByDoctorAndWeek(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
