package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
