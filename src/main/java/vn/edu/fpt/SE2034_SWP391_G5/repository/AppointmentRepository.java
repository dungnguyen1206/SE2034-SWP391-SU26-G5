package vn.edu.fpt.SE2034_SWP391_G5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentStatusCountResponse;
import vn.edu.fpt.SE2034_SWP391_G5.entity.Appointment;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT new vn.edu.fpt.SE2034_SWP391_G5.dto.response.AppointmentStatusCountResponse(a.status, count (a)) " +
            " from Appointment a WHere a.bookingDate= :date group by a.status")
    List<AppointmentStatusCountResponse> findTodayAppointmentsByStatus(@Param("date") LocalDate date );

    @Query("SELECT a from Appointment a " +
            "JOIN fetch a.patient p " +
            "JOIN fetch a.doctor d " +
            "JOIN fetch a.service s " +
            "join fetch a.slot sl " +
            "join fetch sl.schedule ds " +
            "join fetch ds.room r " +
            "WHERE a.bookingDate =:today " +
            "order by sl.startTime asc")
    List<Appointment> findAppointmentsByBookingDate(@Param("today") LocalDate today);



}
