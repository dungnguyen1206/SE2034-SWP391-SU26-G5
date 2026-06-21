package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Department;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Room;
import vn.edu.fpt.SE2034_SWP391_G5.entity.User;
import vn.edu.fpt.SE2034_SWP391_G5.entity.WeekSchedule;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeekScheduleRepository extends JpaRepository<WeekSchedule, Long> {


    @Query("select w from WeekSchedule w where w.id=:id")
    WeekSchedule findWeekScheduleById(Long id);

    @Query("select w from WeekSchedule w left join fetch w.createdBy where w.startDate = :startDate")
    WeekSchedule findWeekScheduleByStartDate(@Param("startDate") LocalDate startDate);

    @Query("SELECT w from WeekSchedule w Where w.endDate <:startDate order by w.endDate desc limit 1")
    WeekSchedule findPreviousWeekSchedule(@Param("startDate") LocalDate startDate);


    @Query("SELECT w from WeekSchedule w Where w.startDate >:endDate order by w.startDate ASC limit 1")
    WeekSchedule findNextWeekSchedule(@Param("endDate") LocalDate endDate);

}
