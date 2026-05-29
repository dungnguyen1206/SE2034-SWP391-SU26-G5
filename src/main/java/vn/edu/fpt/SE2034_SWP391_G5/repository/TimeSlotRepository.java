package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.entity.TimeSlot;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
//    @Query("select t from TimeSlot t Join fetch t.schedule s" +
//            " join fetch s.doctor d where s.workDate= :date")
//        List<TimeSlot> findTodayDoctor(@Param("date") LocalDate date);
}
