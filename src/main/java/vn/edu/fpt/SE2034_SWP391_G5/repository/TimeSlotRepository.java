package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.TimeSlot;

import java.util.List;
import java.util.Optional;

import java.time.LocalDate;


@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
//    @Query("select t from TimeSlot t Join fetch t.schedule s" +
//            " join fetch s.doctor d where s.workDate= :date")
//        List<TimeSlot> findTodayDoctor(@Param("date") LocalDate date);

    List<TimeSlot> findByScheduleIdOrderByStartTimeAsc(Long scheduleId);

    // Fetch slot cùng với schedule (để tránh LazyInitializationException khi đặt lịch)
    @Query("SELECT t FROM TimeSlot t JOIN FETCH t.schedule s WHERE t.id = :id")
    Optional<TimeSlot> findByIdWithSchedule(@Param("id") Long id);

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

    @Modifying
    @Query("DELETE FROM TimeSlot t where t.schedule.id=:doctorScheduleId")
    int deleteTimeSlotByDoctorScheduleId(@Param("doctorScheduleId") Long doctorScheduleId);

    @Query("SELECT ts FROM TimeSlot ts " +
            "JOIN FETCH ts.schedule sch " +
            "WHERE sch.doctor.id = :doctorId " +
            "AND sch.workDate = :workDate " +
            "AND ts.id NOT IN (" +
            "  SELECT a.slot.id FROM Appointment a WHERE a.slot IS NOT NULL AND a.status NOT IN ('CANCELLED')" +
            ") ORDER BY ts.startTime ASC")
    List<TimeSlot> findAvailableSlotsByDoctorAndDate(
            @Param("doctorId") Long doctorId,
            @Param("workDate") LocalDate workDate
    );

    @Query("SELECT ts FROM TimeSlot ts " +
            "JOIN FETCH ts.schedule sch " +
            "JOIN FETCH sch.doctor d " +
            "WHERE d.department.id = :departmentId " +
            "AND sch.workDate = :workDate " +
            "AND ts.bookedCapacity < ts.maxCapacity " +
            "AND ts.startTime > :currentTime " +
            "ORDER BY ts.startTime ASC")
    List<TimeSlot> findAvailableSlotsByDepartmentAndDate(
            @Param("departmentId") Integer departmentId,
            @Param("workDate") LocalDate workDate,
            @Param("currentTime") java.time.LocalTime currentTime
    );
}
